package com.foodrescue.dao;

import com.foodrescue.model.Notification;
import com.foodrescue.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // Create a new notification
    public boolean createNotification(Notification notification)
            throws SQLException {

        String sql = """
                INSERT INTO notifications
                (user_id, title, message, is_read)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, notification.getUserId());
            statement.setString(2, notification.getTitle());
            statement.setString(3, notification.getMessage());
            statement.setBoolean(4, notification.isRead());

            return statement.executeUpdate() > 0;
        }
    }

    // Get notification by ID
    public Notification getNotificationById(long id)
            throws SQLException {

        String sql = """
                SELECT id, user_id, title, message, is_read, created_at
                FROM notifications
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToNotification(resultSet);
                }
            }
        }

        return null;
    }

    // Get all notifications
    public List<Notification> getAllNotifications()
            throws SQLException {

        String sql = """
                SELECT id, user_id, title, message, is_read, created_at
                FROM notifications
                ORDER BY created_at DESC
                """;

        List<Notification> notifications = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                notifications.add(
                        mapResultSetToNotification(resultSet)
                );
            }
        }

        return notifications;
    }

    // Get notifications belonging to a specific user
    public List<Notification> getNotificationsByUser(long userId)
            throws SQLException {

        String sql = """
                SELECT id, user_id, title, message, is_read, created_at
                FROM notifications
                WHERE user_id = ?
                ORDER BY created_at DESC
                """;

        List<Notification> notifications = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    notifications.add(
                            mapResultSetToNotification(resultSet)
                    );
                }
            }
        }

        return notifications;
    }

    // Get unread notifications belonging to a specific user
    public List<Notification> getUnreadNotifications(long userId)
            throws SQLException {

        String sql = """
                SELECT id, user_id, title, message, is_read, created_at
                FROM notifications
                WHERE user_id = ?
                  AND is_read = FALSE
                ORDER BY created_at DESC
                """;

        List<Notification> notifications = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    notifications.add(
                            mapResultSetToNotification(resultSet)
                    );
                }
            }
        }

        return notifications;
    }

    // Mark one notification as read
    // User ownership is checked for security
    public boolean markAsRead(long notificationId, long userId)
            throws SQLException {

        String sql = """
                UPDATE notifications
                SET is_read = TRUE
                WHERE id = ?
                  AND user_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, notificationId);
            statement.setLong(2, userId);

            return statement.executeUpdate() > 0;
        }
    }

    // Mark all notifications of a user as read
    public boolean markAllAsRead(long userId)
            throws SQLException {

        String sql = """
                UPDATE notifications
                SET is_read = TRUE
                WHERE user_id = ?
                  AND is_read = FALSE
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            return statement.executeUpdate() > 0;
        }
    }

    // Get unread notification count
    public int getUnreadCount(long userId)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM notifications
                WHERE user_id = ?
                  AND is_read = FALSE
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }

    // Delete a notification
    // User ownership is checked for security
    public boolean deleteNotification(
            long notificationId,
            long userId)
            throws SQLException {

        String sql = """
                DELETE FROM notifications
                WHERE id = ?
                  AND user_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, notificationId);
            statement.setLong(2, userId);

            return statement.executeUpdate() > 0;
        }
    }

    // Convert ResultSet into Notification object
    private Notification mapResultSetToNotification(
            ResultSet resultSet)
            throws SQLException {

        Notification notification = new Notification();

        notification.setId(
                resultSet.getLong("id")
        );

        notification.setUserId(
                resultSet.getLong("user_id")
        );

        notification.setTitle(
                resultSet.getString("title")
        );

        notification.setMessage(
                resultSet.getString("message")
        );

        notification.setRead(
                resultSet.getBoolean("is_read")
        );

        notification.setCreatedAt(
                resultSet.getTimestamp("created_at")
        );

        return notification;
    }
}