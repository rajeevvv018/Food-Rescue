package com.foodrescue.controller;

import com.foodrescue.service.PublicService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/public/stats")
public class PublicStatsServlet extends HttpServlet {

    private final PublicService publicService;

    public PublicStatsServlet() {
        this.publicService = new PublicService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            Map<String, Integer> stats = publicService.getPublicStats();

            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"donationsRescued\":").append(stats.getOrDefault("donationsRescued", 0)).append(",");
            json.append("\"foodProviders\":").append(stats.getOrDefault("foodProviders", 0)).append(",");
            json.append("\"ngoPartners\":").append(stats.getOrDefault("ngoPartners", 0)).append(",");
            json.append("\"successfulPickups\":").append(stats.getOrDefault("successfulPickups", 0));
            json.append("}");

            out.print(json.toString());
        } catch (SQLException e) {
            getServletContext().log("Database error in PublicStatsServlet", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}
