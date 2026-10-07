package com.foodrescue.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/foodrescue";

    private static final String USER = "root";

    private DBConnection() {
        // Utility class
    }

    public static Connection getConnection() throws SQLException {

        String password =
                System.getenv("FOODRESCUE_DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new SQLException(
                    "FOODRESCUE_DB_PASSWORD environment variable is not set."
            );
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC Driver not found.",
                    e
            );
        }

        return DriverManager.getConnection(
                URL,
                USER,
                password
        );
    }
}