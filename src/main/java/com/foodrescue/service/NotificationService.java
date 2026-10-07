package com.foodrescue.service;

import com.foodrescue.dao.NotificationDAO;
import com.foodrescue.model.Notification;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    // Create a new notification
    public boolean createNotification(Notification notification)
            throws SQLException {

        if (notification == null) {
            return false;
        }

        if (notification.getUserId() <= 0) {
            return false;
        }

        if (notification.getTitle() == null
                || notification.getTitle().trim().isEmpty()) {
            return false;
        }

        if (notification.getMessage() == null
                || notification.getMessage().trim().isEmpty()) {
            return false;
        }

        notification.setTitle(
                notification.getTitle().trim()
        );

        notification.setMessage(
                notification.getMessage().trim()
        );

        // New notifications should be unread
        notification.setRead(false);

        return notificationDAO.createNotification(
                notification
        );
    }

    // Get notification by ID
    public Notification getNotificationById(long notificationId)
            throws SQLException {

        if (notificationId <= 0) {
            return null;
        }

        return notificationDAO.getNotificationById(
                notificationId
        );
    }

    // Get all notifications
    public List<Notification> getAllNotifications()
            throws SQLException {

        return notificationDAO.getAllNotifications();
    }

    // Get notifications belonging to a user
    public List<Notification> getNotificationsByUser(long userId)
            throws SQLException {

        if (userId <= 0) {
            return Collections.emptyList();
        }

        return notificationDAO.getNotificationsByUser(
                userId
        );
    }

    // Get unread notifications belonging to a user
    public List<Notification> getUnreadNotifications(long userId)
            throws SQLException {

        if (userId <= 0) {
            return Collections.emptyList();
        }

        return notificationDAO.getUnreadNotifications(
                userId
        );
    }

    // Mark one notification as read
    // User ID is required to enforce ownership
    public boolean markAsRead(
            long notificationId,
            long userId)
            throws SQLException {

        if (notificationId <= 0 || userId <= 0) {
            return false;
        }

        return notificationDAO.markAsRead(
                notificationId,
                userId
        );
    }

    // Mark all notifications of a user as read
    public boolean markAllAsRead(long userId)
            throws SQLException {

        if (userId <= 0) {
            return false;
        }

        return notificationDAO.markAllAsRead(
                userId
        );
    }

    // Get unread notification count
    public int getUnreadCount(long userId)
            throws SQLException {

        if (userId <= 0) {
            return 0;
        }

        return notificationDAO.getUnreadCount(
                userId
        );
    }

    // Delete a notification
    // User ID is required to enforce ownership
    public boolean deleteNotification(
            long notificationId,
            long userId)
            throws SQLException {

        if (notificationId <= 0 || userId <= 0) {
            return false;
        }

        return notificationDAO.deleteNotification(
                notificationId,
                userId
        );
    }
}