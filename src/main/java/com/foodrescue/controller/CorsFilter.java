package com.foodrescue.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class CorsFilter implements Filter {

    // In a real environment, this could be read from a properties file or environment variable.
    // For now, allow the frontend origin explicitly, e.g., Netlify.
    private static final String ALLOWED_ORIGIN = System.getenv("FRONTEND_URL"); // Configure via env

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");

        // If ALLOWED_ORIGIN is set, use it. Otherwise fallback to the actual origin only for local testing.
        // Never use "*" with allow-credentials=true
        String safeOrigin = "";
        if (ALLOWED_ORIGIN != null && !ALLOWED_ORIGIN.isEmpty()) {
            if (ALLOWED_ORIGIN.equals(origin)) {
                safeOrigin = origin;
            }
        } else if (origin != null && (origin.startsWith("http://localhost:") || origin.startsWith("http://127.0.0.1:"))) {
            safeOrigin = origin;
        }

        if (origin != null && !safeOrigin.isEmpty()) {
            httpResponse.setHeader("Access-Control-Allow-Origin", safeOrigin);
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
        }

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
