package com.foodrescue.dao;

import com.foodrescue.model.Notification;
import com.foodrescue.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // INSERT a new notification
    public boolean createNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, title, message, is_read) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, notification.getUserId());
            statement.setString(2, notification.getTitle());
            statement.setString(3, notification.getMessage());
            statement.setBoolean(4, notification.isRead());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // SELECT notification by ID
    public Notification getNotificationById(long id) throws SQLException {
        String sql = "SELECT id, user_id, title, message, is_read, created_at FROM notifications WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotification(rs);
                }
            }
        }
        return null;
    }

    // SELECT all notifications
    public List<Notification> getAllNotifications() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT id, user_id, title, message, is_read, created_at FROM notifications ORDER BY created_at DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        }
        return notifications;
    }

    // SELECT notifications by user
    public List<Notification> getNotificationsByUser(long userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT id, user_id, title, message, is_read, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    // SELECT unread notifications by user
    public List<Notification> getUnreadNotifications(long userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT id, user_id, title, message, is_read, created_at FROM notifications WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    // UPDATE mark notification as read
    public boolean markAsRead(long id) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // UPDATE mark all user notifications as read
    public boolean markAllAsRead(long userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, userId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // DELETE a notification
    public boolean deleteNotification(long id) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map ResultSet to Notification object
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getLong("id"));
        notification.setUserId(rs.getLong("user_id"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        return notification;
    }
}
