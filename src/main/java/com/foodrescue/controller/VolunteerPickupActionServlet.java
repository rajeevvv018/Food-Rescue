package com.foodrescue.controller;

import com.foodrescue.service.PickupService;
import com.foodrescue.service.FoodClaimService;
import com.foodrescue.service.NotificationService;
import com.foodrescue.service.FoodListingService;
import com.foodrescue.model.FoodClaim;
import com.foodrescue.model.Pickup;
import com.foodrescue.model.FoodListing;
import com.foodrescue.model.Notification;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/volunteer/pickup-action")
public class VolunteerPickupActionServlet extends HttpServlet {

    private PickupService pickupService;
    private FoodClaimService foodClaimService;
    private NotificationService notificationService;
    private FoodListingService foodListingService;

    @Override
    public void init() throws ServletException {
        pickupService = new PickupService();
        foodClaimService = new FoodClaimService();
        notificationService = new NotificationService();
        foodListingService = new FoodListingService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"VOLUNTEER".equals(session.getAttribute("userRole"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
            return;
        }

        long volunteerId = (Long) session.getAttribute("userId");
        String action = request.getParameter("action");
        String claimIdStr = request.getParameter("claimId");
        String pickupIdStr = request.getParameter("pickupId");

        if (action == null || action.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing action\"}");
            return;
        }

        boolean success = false;
        long claimIdForNotification = -1;

        try {
            switch (action.toUpperCase()) {
                case "ACCEPT":
                    if (claimIdStr == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Missing claimId for ACCEPT\"}");
                        return;
                    }
                    long claimId = Long.parseLong(claimIdStr);
                    success = pickupService.acceptPickup(claimId, volunteerId);
                    if (success) {
                        claimIdForNotification = claimId;
                    }
                    break;
                case "PICKED_UP":
                    if (pickupIdStr == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Missing pickupId for PICKED_UP\"}");
                        return;
                    }
                    long pickupId = Long.parseLong(pickupIdStr);
                    Pickup pickupObj = pickupService.getPickupById(pickupId);
                    if (pickupObj == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Pickup record not found\"}");
                        return;
                    }
                    success = pickupService.markPickedUp(pickupId, volunteerId);
                    if (success) {
                        claimIdForNotification = pickupObj.getClaimId();
                    }
                    break;
                case "DELIVERED":
                    if (pickupIdStr == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Missing pickupId for DELIVERED\"}");
                        return;
                    }
                    long pickupId2 = Long.parseLong(pickupIdStr);
                    Pickup pickupToDeliver = pickupService.getPickupById(pickupId2);
                    if (pickupToDeliver == null || pickupToDeliver.getClaimId() <= 0) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Pickup record not found\"}");
                        return;
                    }

                    // Atomic single JDBC transaction:
                    // 1. Verifies pickup belongs to volunteer and is currently in PICKED_UP status (locked FOR UPDATE)
                    // 2. Verifies associated claim is in expected READY_FOR_PICKUP status
                    // 3. Atomically updates claim to COMPLETED and pickup to DELIVERED
                    success = pickupService.completePickupAndClaim(pickupId2, volunteerId);
                    if (success) {
                        claimIdForNotification = pickupToDeliver.getClaimId();
                    }
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Invalid action\"}");
                    return;
            }

            if (success) {
                // Notifications are best-effort and decoupled from core status update success
                try {
                    if (claimIdForNotification > 0) {
                        FoodClaim claim = foodClaimService.getFoodClaimById(claimIdForNotification);
                        if (claim != null) {
                            FoodListing listing = foodListingService.getFoodListingById(claim.getFoodId());
                            if (listing != null) {
                                String foodName = listing.getFoodName();

                                Notification notificationProvider = new Notification();
                                notificationProvider.setUserId(listing.getProviderId());
                                notificationProvider.setRead(false);

                                Notification notificationNgo = new Notification();
                                notificationNgo.setUserId(claim.getNgoId());
                                notificationNgo.setRead(false);

                                String claimRef = " (Claim #" + claimIdForNotification + ")";

                                if ("ACCEPT".equalsIgnoreCase(action)) {
                                    notificationProvider.setTitle("Pickup Accepted");
                                    notificationProvider.setMessage("A volunteer has accepted the pickup for " + foodName + claimRef + ".");
                                    notificationNgo.setTitle("Pickup Accepted");
                                    notificationNgo.setMessage("A volunteer has accepted the pickup for " + foodName + claimRef + ".");
                                } else if ("PICKED_UP".equalsIgnoreCase(action)) {
                                    notificationProvider.setTitle("Food Picked Up");
                                    notificationProvider.setMessage("The volunteer has picked up " + foodName + claimRef + ".");
                                    notificationNgo.setTitle("Food Picked Up");
                                    notificationNgo.setMessage("The volunteer has picked up " + foodName + " and is en route" + claimRef + ".");
                                } else if ("DELIVERED".equalsIgnoreCase(action)) {
                                    notificationProvider.setTitle("Food Delivered");
                                    notificationProvider.setMessage("The volunteer has successfully delivered " + foodName + claimRef + ".");
                                    notificationNgo.setTitle("Food Delivered");
                                    notificationNgo.setMessage("The volunteer has successfully delivered " + foodName + claimRef + ".");
                                }

                                try {
                                    notificationService.createNotification(notificationProvider);
                                } catch (Exception e) {
                                    getServletContext().log("[VolunteerPickupAction] WARNING: Failed to send provider notification: " + e.getMessage(), e);
                                }

                                try {
                                    notificationService.createNotification(notificationNgo);
                                } catch (Exception e) {
                                    getServletContext().log("[VolunteerPickupAction] WARNING: Failed to send NGO notification: " + e.getMessage(), e);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    getServletContext().log("[VolunteerPickupAction] WARNING: Notification dispatch failed: " + e.getMessage(), e);
                }

                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true, \"message\": \"Action completed successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Action failed. Invalid ID, wrong status, or unauthorized.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid ID format\"}");
        } catch (Exception e) {
            getServletContext().log("[VolunteerPickupAction] Error processing action: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
}
