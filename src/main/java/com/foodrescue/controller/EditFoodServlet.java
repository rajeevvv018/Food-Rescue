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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@WebServlet("/provider/edit-food")
public class EditFoodServlet extends HttpServlet {

    private final FoodListingService foodListingService;

    public EditFoodServlet() {
        this.foodListingService = new FoodListingService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        if (!"PROVIDER".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }

        long providerId = (Long) session.getAttribute("userId");
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing listing ID.");
            return;
        }

        try {
            long listingId = Long.parseLong(idStr);
            FoodListing existing = foodListingService.getFoodListingById(listingId);
            
            if (existing == null || existing.getProviderId() != providerId) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to this listing.");
                return;
            }

            String foodName = request.getParameter("foodName");
            String description = request.getParameter("description");
            String quantityStr = request.getParameter("quantity");
            String unit = request.getParameter("unit");
            String foodType = request.getParameter("foodType");
            String preparedAtStr = request.getParameter("preparedAt");
            String expiryTimeStr = request.getParameter("expiryTime");
            String pickupAddress = request.getParameter("pickupAddress");

            if (foodName == null || foodName.trim().isEmpty() ||
                quantityStr == null || quantityStr.trim().isEmpty() ||
                unit == null || unit.trim().isEmpty() ||
                expiryTimeStr == null || expiryTimeStr.trim().isEmpty() ||
                pickupAddress == null || pickupAddress.trim().isEmpty()) {
                
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields.");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr.trim());
                if (quantity <= 0) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quantity must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid quantity.");
                return;
            }

            Timestamp preparedAt = null;
            if (preparedAtStr != null && !preparedAtStr.trim().isEmpty()) {
                try {
                    LocalDateTime ldt = LocalDateTime.parse(preparedAtStr.trim());
                    preparedAt = Timestamp.valueOf(ldt);
                } catch (DateTimeParseException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid prepared date/time format.");
                    return;
                }
            }

            Timestamp expiryTime;
            try {
                LocalDateTime ldt = LocalDateTime.parse(expiryTimeStr.trim());
                expiryTime = Timestamp.valueOf(ldt);
            } catch (DateTimeParseException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid expiry date/time format.");
                return;
            }

            existing.setFoodName(foodName);
            existing.setDescription(description);
            existing.setQuantity(quantity);
            existing.setUnit(unit);
            existing.setFoodType(foodType);
            existing.setPreparedAt(preparedAt);
            existing.setExpiryTime(expiryTime);
            existing.setPickupAddress(pickupAddress);

            boolean success = foodListingService.updateFoodListing(existing);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/provider/dashboard.html");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to update food listing due to invalid input.");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid listing ID.");
        } catch (SQLException e) {
            getServletContext().log("Database error in EditFoodServlet", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}
