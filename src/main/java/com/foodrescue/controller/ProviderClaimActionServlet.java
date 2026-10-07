package com.foodrescue.controller;

import com.foodrescue.service.FoodClaimService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/provider/claim-action")
public class ProviderClaimActionServlet extends HttpServlet {

    private FoodClaimService foodClaimService;

    @Override
    public void init() throws ServletException {
        foodClaimService = new FoodClaimService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"PROVIDER".equals(session.getAttribute("userRole"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
            return;
        }

        long providerId = (Long) session.getAttribute("userId");
        String claimIdStr = request.getParameter("claimId");
        String action = request.getParameter("action");

        if (claimIdStr == null || claimIdStr.trim().isEmpty() || action == null || action.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing claimId or action\"}");
            return;
        }

        long claimId;
        try {
            claimId = Long.parseLong(claimIdStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid claimId\"}");
            return;
        }

        boolean success = false;
        try {
            switch (action.toUpperCase()) {
                case "APPROVE":
                    success = foodClaimService.approveClaim(claimId, providerId);
                    break;
                case "REJECT":
                    success = foodClaimService.rejectClaim(claimId, providerId);
                    break;
                case "READY":
                    success = foodClaimService.markClaimReadyForPickup(claimId, providerId);
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
                response.getWriter().write("{\"error\": \"Action failed. Invalid claim ID, wrong status, or unauthorized.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
}
