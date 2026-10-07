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
import java.util.List;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;

@WebServlet("/volunteer/available-pickups")
public class VolunteerAvailablePickupsServlet extends HttpServlet {

    private FoodClaimService foodClaimService;

    @Override
    public void init() throws ServletException {
        foodClaimService = new FoodClaimService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"VOLUNTEER".equals(session.getAttribute("userRole"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
            return;
        }

        try {
            List<FoodClaimDTO> availablePickups = foodClaimService.getAvailablePickups();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            StringBuilder json = new StringBuilder();
            json.append("[");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (int i = 0; i < availablePickups.size(); i++) {
                FoodClaimDTO dto = availablePickups.get(i);
                json.append("{");
                json.append("\"claimId\":").append(dto.getClaimId()).append(",");
                json.append("\"foodId\":").append(dto.getFoodId()).append(",");
                json.append("\"foodName\":\"").append(escapeJson(dto.getFoodName())).append("\",");
                json.append("\"claimedQuantity\":").append(dto.getClaimedQuantity()).append(",");
                json.append("\"unit\":\"").append(escapeJson(dto.getUnit())).append("\",");
                json.append("\"pickupAddress\":\"").append(escapeJson(dto.getPickupAddress())).append("\",");
                json.append("\"providerName\":\"").append(escapeJson(dto.getProviderName())).append("\",");
                json.append("\"ngoName\":\"").append(escapeJson(dto.getNgoName())).append("\",");

                String expiryStr = dto.getExpiryTime() != null ? dateFormat.format(dto.getExpiryTime()) : "";
                json.append("\"expiryTime\":\"").append(escapeJson(expiryStr)).append("\"");
                json.append("}");

                if (i < availablePickups.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            out.print(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
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
