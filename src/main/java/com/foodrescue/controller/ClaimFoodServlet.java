package com.foodrescue.controller;

import com.foodrescue.model.FoodClaim;
import com.foodrescue.model.FoodListing;
import com.foodrescue.service.FoodClaimService;
import com.foodrescue.service.FoodListingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/ngo/claim-food")
public class ClaimFoodServlet extends HttpServlet {

    private final FoodClaimService foodClaimService;
    private final FoodListingService foodListingService;

    public ClaimFoodServlet() {
        this.foodClaimService = new FoodClaimService();
        this.foodListingService = new FoodListingService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            getServletContext().log("[ClaimFood] REJECTED: No session or userId");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        if (!"NGO".equals(userRole)) {
            getServletContext().log("[ClaimFood] REJECTED: Role is '" + userRole + "', not NGO");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }

        long ngoId = (Long) session.getAttribute("userId");
        String foodIdStr = request.getParameter("foodId");
        String claimedQuantityStr = request.getParameter("claimedQuantity");

        getServletContext().log("[ClaimFood] ngoId=" + ngoId + " foodId=" + foodIdStr + " claimedQuantity=" + claimedQuantityStr);

        if (foodIdStr == null || foodIdStr.trim().isEmpty() ||
            claimedQuantityStr == null || claimedQuantityStr.trim().isEmpty()) {
            getServletContext().log("[ClaimFood] REJECTED: Missing parameters");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters.");
            return;
        }

        try {
            long foodId = Long.parseLong(foodIdStr.trim());
            int claimedQuantity = Integer.parseInt(claimedQuantityStr.trim());

            if (foodId <= 0 || claimedQuantity <= 0) {
                getServletContext().log("[ClaimFood] REJECTED: foodId=" + foodId + " claimedQuantity=" + claimedQuantity + " (invalid)");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid food ID or quantity.");
                return;
            }

            FoodListing listing = foodListingService.getFoodListingById(foodId);
            if (listing == null) {
                getServletContext().log("[ClaimFood] REJECTED: Food listing " + foodId + " not found");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Food listing not found.");
                return;
            }

            if (!"AVAILABLE".equals(listing.getStatus())) {
                getServletContext().log("[ClaimFood] REJECTED: Food listing " + foodId + " status=" + listing.getStatus());
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Food listing is not available.");
                return;
            }

            long currentTime = System.currentTimeMillis();
            if (listing.getExpiryTime() != null && listing.getExpiryTime().getTime() < currentTime) {
                getServletContext().log("[ClaimFood] REJECTED: Food listing " + foodId + " has expired");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Food listing has expired.");
                return;
            }

            if (claimedQuantity > listing.getQuantity()) {
                getServletContext().log("[ClaimFood] REJECTED: claimedQuantity=" + claimedQuantity + " > available=" + listing.getQuantity());
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Claimed quantity exceeds available quantity.");
                return;
            }

            FoodClaim claim = new FoodClaim();
            claim.setFoodId(foodId);
            claim.setNgoId(ngoId);
            claim.setClaimedQuantity(claimedQuantity);
            claim.setStatus("PENDING");

            boolean success = foodClaimService.createFoodClaim(claim);
            getServletContext().log("[ClaimFood] createFoodClaim result=" + success + " for ngoId=" + ngoId + " foodId=" + foodId + " qty=" + claimedQuantity);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/ngo/dashboard.html?claimSuccess=true");
            } else {
                getServletContext().log("[ClaimFood] FAILED: Service returned false");
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create food claim.");
            }

        } catch (NumberFormatException e) {
            getServletContext().log("[ClaimFood] REJECTED: Number format error - " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid numeric format.");
        } catch (SQLException e) {
            getServletContext().log("[ClaimFood] DATABASE ERROR: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}
