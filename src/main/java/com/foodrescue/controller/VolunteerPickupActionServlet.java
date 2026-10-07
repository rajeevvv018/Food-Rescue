package com.foodrescue.controller;

import com.foodrescue.service.PickupService;
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

    @Override
    public void init() throws ServletException {
        pickupService = new PickupService();
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
                    break;
                case "PICKED_UP":
                    if (pickupIdStr == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Missing pickupId for PICKED_UP\"}");
                        return;
                    }
                    long pickupId = Long.parseLong(pickupIdStr);
                    success = pickupService.markPickedUp(pickupId, volunteerId);
                    break;
                case "DELIVERED":
                    if (pickupIdStr == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Missing pickupId for DELIVERED\"}");
                        return;
                    }
                    long pickupId2 = Long.parseLong(pickupIdStr);
                    success = pickupService.markDelivered(pickupId2, volunteerId);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Invalid action\"}");
                    return;
            }

            if (success) {
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
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
}
