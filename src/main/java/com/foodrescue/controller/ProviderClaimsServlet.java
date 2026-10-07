package com.foodrescue.controller;

import com.foodrescue.model.FoodClaimDTO;
import com.foodrescue.service.FoodClaimService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/provider/claims")
public class ProviderClaimsServlet extends HttpServlet {

    private final FoodClaimService foodClaimService;

    public ProviderClaimsServlet() {
        this.foodClaimService = new FoodClaimService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        if (!"PROVIDER".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }

        long providerId = (Long) session.getAttribute("userId");

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            List<FoodClaimDTO> claims = foodClaimService.getClaimsWithDetailsByProvider(providerId);

            StringBuilder json = new StringBuilder();
            json.append("[");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (int i = 0; i < claims.size(); i++) {
                FoodClaimDTO claim = claims.get(i);
                json.append("{");
                json.append("\"claimId\":").append(claim.getClaimId()).append(",");
                json.append("\"foodId\":").append(claim.getFoodId()).append(",");
                json.append("\"foodName\":\"").append(escapeJson(claim.getFoodName())).append("\",");
                json.append("\"claimedQuantity\":").append(claim.getClaimedQuantity()).append(",");
                json.append("\"unit\":\"").append(escapeJson(claim.getUnit())).append("\",");
                json.append("\"ngoName\":\"").append(escapeJson(claim.getNgoName())).append("\",");
                json.append("\"ngoPhone\":\"").append(escapeJson(claim.getNgoPhone())).append("\",");
                json.append("\"ngoEmail\":\"").append(escapeJson(claim.getNgoEmail())).append("\",");
                json.append("\"pickupAddress\":\"").append(escapeJson(claim.getPickupAddress())).append("\",");
                json.append("\"status\":\"").append(escapeJson(claim.getStatus())).append("\",");

                String claimedAtStr = claim.getClaimedAt() != null ? dateFormat.format(claim.getClaimedAt()) : "";
                json.append("\"claimedAt\":\"").append(escapeJson(claimedAtStr)).append("\",");

                String updatedAtStr = claim.getUpdatedAt() != null ? dateFormat.format(claim.getUpdatedAt()) : "";
                json.append("\"updatedAt\":\"").append(escapeJson(updatedAtStr)).append("\"");
                json.append("}");

                if (i < claims.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");
            out.print(json.toString());

        } catch (SQLException e) {
            getServletContext().log("Database error in ProviderClaimsServlet", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    private String escapeJson(String data) {
        if (data == null) {
            return "";
        }
        return data.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
