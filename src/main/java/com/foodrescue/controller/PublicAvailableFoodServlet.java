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
import java.util.List;
import java.util.Map;

@WebServlet("/public/available-food")
public class PublicAvailableFoodServlet extends HttpServlet {

    private final PublicService publicService;

    public PublicAvailableFoodServlet() {
        this.publicService = new PublicService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            List<Map<String, String>> foods = publicService.getPublicAvailableFood();

            StringBuilder json = new StringBuilder();
            json.append("[");

            for (int i = 0; i < foods.size(); i++) {
                Map<String, String> food = foods.get(i);
                json.append("{");
                json.append("\"id\":").append(food.get("id")).append(",");
                json.append("\"foodName\":\"").append(escapeJson(food.get("foodName"))).append("\",");
                json.append("\"quantity\":").append(food.get("quantity")).append(",");
                json.append("\"unit\":\"").append(escapeJson(food.get("unit"))).append("\",");
                json.append("\"pickupAddress\":\"").append(escapeJson(food.get("pickupAddress"))).append("\",");
                json.append("\"providerName\":\"").append(escapeJson(food.get("providerName"))).append("\",");
                json.append("\"expiryTime\":\"").append(escapeJson(food.get("expiryTime"))).append("\"");
                json.append("}");
                if (i < foods.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");
            out.print(json.toString());
        } catch (SQLException e) {
            getServletContext().log("Database error in PublicAvailableFoodServlet", e);
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
