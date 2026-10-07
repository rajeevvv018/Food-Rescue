package com.foodrescue.controller;

import com.foodrescue.model.FoodListing;
import com.foodrescue.service.FoodListingService;
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

@WebServlet("/ngo/available-food")
public class NGOAvailableFoodServlet extends HttpServlet {

    private final FoodListingService foodListingService;

    public NGOAvailableFoodServlet() {
        this.foodListingService = new FoodListingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        if (!"NGO".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            List<FoodListing> listings = foodListingService.getAvailableFoodListings();
            
            long currentTime = System.currentTimeMillis();
            listings.removeIf(l -> l.getExpiryTime() != null && l.getExpiryTime().getTime() < currentTime);

            StringBuilder json = new StringBuilder();
            json.append("[");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (int i = 0; i < listings.size(); i++) {
                FoodListing listing = listings.get(i);
                json.append("{");
                json.append("\"id\":").append(listing.getId()).append(",");
                json.append("\"providerId\":").append(listing.getProviderId()).append(",");
                json.append("\"foodName\":\"").append(escapeJson(listing.getFoodName())).append("\",");
                json.append("\"description\":\"").append(escapeJson(listing.getDescription())).append("\",");
                json.append("\"quantity\":").append(listing.getQuantity()).append(",");
                json.append("\"unit\":\"").append(escapeJson(listing.getUnit())).append("\",");
                json.append("\"foodType\":\"").append(escapeJson(listing.getFoodType())).append("\",");

                String preparedAtStr = listing.getPreparedAt() != null ? dateFormat.format(listing.getPreparedAt()) : "";
                json.append("\"preparedAt\":\"").append(escapeJson(preparedAtStr)).append("\",");

                String expiryStr = listing.getExpiryTime() != null ? dateFormat.format(listing.getExpiryTime()) : "";
                json.append("\"expiryTime\":\"").append(escapeJson(expiryStr)).append("\",");

                String createdAtStr = listing.getCreatedAt() != null ? dateFormat.format(listing.getCreatedAt()) : "";
                json.append("\"createdAt\":\"").append(escapeJson(createdAtStr)).append("\",");

                json.append("\"pickupAddress\":\"").append(escapeJson(listing.getPickupAddress())).append("\",");
                json.append("\"status\":\"").append(escapeJson(listing.getStatus())).append("\"");
                json.append("}");

                if (i < listings.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");
            out.print(json.toString());

        } catch (SQLException e) {
            getServletContext().log("Database error in NGOAvailableFoodServlet", e);
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
