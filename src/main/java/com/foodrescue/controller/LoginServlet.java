package com.foodrescue.controller;

import com.foodrescue.model.User;
import com.foodrescue.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect("login.html");
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate input
        if (email == null
                || email.trim().isEmpty()
                || password == null
                || password.isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Email and password are required."
            );

            return;
        }

        email = email.trim().toLowerCase();

        try {

            User user = userService.loginUser(
                    email,
                    password
            );

            // Invalid credentials
            if (user == null) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid email or password."
                );

                return;
            }

            /*
             * Create a fresh session after successful login.
             */
            HttpSession oldSession =
                    request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session =
                    request.getSession(true);

            // Store user information in session
            session.setAttribute(
                    "userId",
                    user.getId()
            );

            session.setAttribute(
                    "userName",
                    user.getName()
            );

            session.setAttribute(
                    "userEmail",
                    user.getEmail()
            );

            session.setAttribute(
                    "userRole",
                    user.getRole()
            );

            // Session timeout: 30 minutes
            session.setMaxInactiveInterval(
                    30 * 60
            );

            // Role-based redirect
            switch (user.getRole()) {

                case "PROVIDER":

                    response.sendRedirect(
                            "provider/dashboard.html"
                    );

                    break;

                case "NGO":

                    response.sendRedirect(
                            "ngo/dashboard.html"
                    );

                    break;

                case "VOLUNTEER":

                    response.sendRedirect(
                            "volunteer/dashboard.html"
                    );

                    break;

                case "ADMIN":

                    // Admin registration/login will be
                    // handled separately.
                    session.invalidate();

                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Admin login is restricted."
                    );

                    break;

                default:

                    session.invalidate();

                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Invalid user role."
                    );
            }

        } catch (SQLException e) {

            getServletContext().log(
                    "Database error during login",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to process login."
            );
        }
    }
}