package com.foodrescue.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/provider/*",
        "/ngo/*",
        "/volunteer/*"
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        HttpSession session =
                httpRequest.getSession(false);

        String requestURI =
                httpRequest.getRequestURI();

        String contextPath =
                httpRequest.getContextPath();

        // User is not logged in
        if (session == null
                || session.getAttribute("userId") == null) {

            httpResponse.sendRedirect(
                    contextPath + "/login.html"
            );

            return;
        }

        String userRole =
                (String) session.getAttribute("userRole");

        // Provider dashboard
        if (requestURI.startsWith(
                contextPath + "/provider/")) {

            if (!"PROVIDER".equals(userRole)) {

                httpResponse.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Access denied."
                );

                return;
            }
        }

        // NGO dashboard
        else if (requestURI.startsWith(
                contextPath + "/ngo/")) {

            if (!"NGO".equals(userRole)) {

                httpResponse.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Access denied."
                );

                return;
            }
        }

        // Volunteer dashboard
        else if (requestURI.startsWith(
                contextPath + "/volunteer/")) {

            if (!"VOLUNTEER".equals(userRole)) {

                httpResponse.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Access denied."
                );

                return;
            }
        }

        // Access allowed
        chain.doFilter(
                request,
                response
        );
    }
}