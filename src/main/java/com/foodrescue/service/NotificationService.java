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

    // Method 1: Create Notification
    public boolean createNotification(Notification notification) throws SQLException {
        if (notification == null) {
            return false;
        }
        if (notification.getUserId() <= 0) {
            return false;
        }
        if (notification.getTitle() == null || notification.getTitle().trim().isEmpty()) {
            return false;
        }
        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            return false;
        }

        notification.setTitle(notification.getTitle().trim());
        notification.setMessage(notification.getMessage().trim());

        return notificationDAO.createNotification(notification);
    }

    // Method 2: Get Notification By ID
    public Notification getNotificationById(long id) throws SQLException {
        if (id <= 0) {
            return null;
        }
        return notificationDAO.getNotificationById(id);
    }

    // Method 3: Get All Notifications
    public List<Notification> getAllNotifications() throws SQLException {
        return notificationDAO.getAllNotifications();
    }

    // Method 4: Get User Notifications
    public List<Notification> getNotificationsByUser(long userId) throws SQLException {
        if (userId <= 0) {
            return Collections.emptyList();
        }
        return notificationDAO.getNotificationsByUser(userId);
    }

    // Method 5: Get Unread Notifications
    public List<Notification> getUnreadNotifications(long userId) throws SQLException {
        if (userId <= 0) {
            return Collections.emptyList();
        }
        return notificationDAO.getUnreadNotifications(userId);
    }

    // Method 6: Mark As Read
    public boolean markAsRead(long id) throws SQLException {
        if (id <= 0) {
            return false;
        }
        return notificationDAO.markAsRead(id);
    }

    // Method 7: Mark All As Read
    public boolean markAllAsRead(long userId) throws SQLException {
        if (userId <= 0) {
            return false;
        }
        return notificationDAO.markAllAsRead(userId);
    }

    // Method 8: Delete Notification
    public boolean deleteNotification(long id) throws SQLException {
        if (id <= 0) {
            return false;
        }
        return notificationDAO.deleteNotification(id);
    }
}
