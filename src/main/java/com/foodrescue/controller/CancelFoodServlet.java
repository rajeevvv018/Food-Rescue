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

@WebServlet("/provider/cancel-food")
public class CancelFoodServlet extends HttpServlet {

    private final FoodListingService foodListingService;

    public CancelFoodServlet() {
        this.foodListingService = new FoodListingService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            FoodListing listing = foodListingService.getFoodListingById(listingId);
            if (listing == null || listing.getProviderId() != providerId) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to this listing.");
                return;
            }

            if (!"AVAILABLE".equals(listing.getStatus())) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Only AVAILABLE listings can be cancelled.");
                return;
            }

            boolean success = foodListingService.updateFoodListingStatus(listingId, "CANCELLED");
            if (success) {
                response.sendRedirect(request.getContextPath() + "/provider/dashboard.html");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to cancel listing.");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid listing ID.");
        } catch (SQLException e) {
            getServletContext().log("Database error in CancelFoodServlet", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}
