package com.foodrescue.controller;

import com.foodrescue.model.Notification;
import com.foodrescue.service.NotificationService;
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

@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {

    private final NotificationService notificationService;

    public NotificationServlet() {
        this.notificationService = new NotificationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }

        long userId = (Long) session.getAttribute("userId");
        String action = request.getParameter("action");

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            if ("unreadCount".equals(action)) {
                int count = notificationService.getUnreadCount(userId);
                out.print("{\"unreadCount\": " + count + "}");
                return;
            }

            List<Notification> notifications = notificationService.getNotificationsByUser(userId);

            StringBuilder json = new StringBuilder();
            json.append("[");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < notifications.size(); i++) {
                Notification n = notifications.get(i);
                json.append("{");
                json.append("\"id\":").append(n.getId()).append(",");
                json.append("\"userId\":").append(n.getUserId()).append(",");
                json.append("\"title\":\"").append(escapeJson(n.getTitle())).append("\",");
                json.append("\"message\":\"").append(escapeJson(n.getMessage())).append("\",");
                json.append("\"isRead\":").append(n.isRead()).append(",");
                
                String createdStr = n.getCreatedAt() != null ? dateFormat.format(n.getCreatedAt()) : "";
                json.append("\"createdAt\":\"").append(escapeJson(createdStr)).append("\"");
                json.append("}");

                if (i < notifications.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");
            out.print(json.toString());

        } catch (SQLException e) {
            getServletContext().log("Database error in NotificationServlet", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }

        long userId = (Long) session.getAttribute("userId");
        String action = request.getParameter("action");

        try {
            if ("markAllRead".equals(action)) {
                boolean success = notificationService.markAllAsRead(userId);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": " + success + "}");
            } else if ("markRead".equals(action)) {
                String idParam = request.getParameter("id");
                if (idParam != null) {
                    long notificationId = Long.parseLong(idParam);
                    boolean success = notificationService.markAsRead(notificationId, userId);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": " + success + "}");
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing notification ID");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (SQLException | NumberFormatException e) {
            getServletContext().log("Database error in NotificationServlet POST", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
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
