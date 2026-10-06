package com.foodrescue.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/foodrescue";

    private static final String USER =
            "root";

    public static Connection getConnection() throws SQLException {
        String password = System.getenv("FOODRESCUE_DB_PASSWORD");
        if (password == null) {
            throw new RuntimeException("FOODRESCUE_DB_PASSWORD environment variable is not configured.");
        }

        return DriverManager.getConnection(
                URL,
                USER,
                password
        );
    }
}