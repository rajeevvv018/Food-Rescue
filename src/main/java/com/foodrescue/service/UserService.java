package com.foodrescue.service;

import com.foodrescue.dao.UserDAO;
import com.foodrescue.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // Method 1: Register User
    public boolean registerUser(User user) throws SQLException {
        if (user == null) {
            return false;
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return false;
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return false;
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return false;
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            return false;
        }

        User existingUser = userDAO.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            return false;
        }

        return userDAO.registerUser(user);
    }

    // Method 2: Get User By Email
    public User getUserByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userDAO.getUserByEmail(email.trim());
    }

    // Method 3: Get User By Id
    public User getUserById(long id) throws SQLException {
        if (id <= 0) {
            return null;
        }
        return userDAO.getUserById(id);
    }

    // Method 4: Get All Users
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    // Method 5: Update User
    public boolean updateUser(User user) throws SQLException {
        if (user == null) {
            return false;
        }
        if (user.getId() <= 0) {
            return false;
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return false;
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return false;
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            return false;
        }

        User existingUser = userDAO.getUserByEmail(user.getEmail());
        if (existingUser != null && existingUser.getId() != user.getId()) {
            return false;
        }

        return userDAO.updateUser(user);
    }

    // Method 6: Delete User
    public boolean deleteUser(long id) throws SQLException {
        if (id <= 0) {
            return false;
        }
        return userDAO.deleteUser(id);
    }
}
