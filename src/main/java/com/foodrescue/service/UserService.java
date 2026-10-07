package com.foodrescue.service;

import com.foodrescue.dao.UserDAO;
import com.foodrescue.model.User;
import com.foodrescue.util.PasswordUtil;

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

        // Check whether email already exists
        User existingUser = userDAO.getUserByEmail(user.getEmail());

        if (existingUser != null) {
            return false;
        }

        // Hash password before saving it to the database
        user.setPassword(
                PasswordUtil.hashPassword(user.getPassword())
        );

        // Save user
        return userDAO.registerUser(user);
    }

    // Method 2: Login User
    public User loginUser(String email, String password) throws SQLException {

        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.isEmpty()) {
            return null;
        }

        User user = userDAO.getUserByEmail(email.trim().toLowerCase());

        if (user == null) {
            return null;
        }

        boolean passwordMatched =
                PasswordUtil.verifyPassword(
                        password,
                        user.getPassword()
                );

        if (!passwordMatched) {
            return null;
        }

        return user;
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

        // Check whether another user is using the same email
        User existingUser = userDAO.getUserByEmail(user.getEmail());

        if (existingUser != null && existingUser.getId() != user.getId()) {
            return false;
        }

        // Password is intentionally not updated here
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