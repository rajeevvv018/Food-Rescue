package com.foodrescue.controller;

import com.foodrescue.model.User;
import com.foodrescue.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService;

    public RegisterServlet() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<html><body><h1>FoodRescue Registration Page</h1></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");
        String address = request.getParameter("address");

        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {
            
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("<html><body><p>Please fill all required fields.</p></body></html>");
            return;
        }

        role = role.trim().toUpperCase();
        if (!role.equals("PROVIDER") && !role.equals("NGO") && !role.equals("VOLUNTEER")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("<html><body><p>Invalid user role.</p></body></html>");
            return;
        }

        email = email.trim().toLowerCase();
        
        try {
            User existingUser = userService.getUserByEmail(email);
            if (existingUser != null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("<html><body><p>Email is already registered.</p></body></html>");
                return;
            }

            User user = new User();
            user.setName(name.trim());
            user.setEmail(email);
            user.setPassword(password); // Do not trim password
            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone.trim());
            }
            user.setRole(role);
            if (address != null && !address.trim().isEmpty()) {
                user.setAddress(address.trim());
            }

            boolean success = userService.registerUser(user);

            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("<html><body><p>Registration successful.</p><p>Your account has been created successfully.</p></body></html>");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("<html><body><p>Registration failed.</p></body></html>");
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("<html><body><p>Something went wrong while registering. Please try again later.</p></body></html>");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("<html><body><p>An unexpected error occurred.</p></body></html>");
        }
    }
}
