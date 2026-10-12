package com.foodrescue.dao;

import com.foodrescue.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

public class PublicDAO {

    public Map<String, Integer> getPublicStats() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("donationsRescued", 0);
        stats.put("foodProviders", 0);
        stats.put("ngoPartners", 0);
        stats.put("successfulPickups", 0);

        String sqlDonations = "SELECT COUNT(id) FROM food_claims WHERE status = 'COMPLETED'";
        String sqlProviders = "SELECT COUNT(id) FROM users WHERE role = 'PROVIDER'";
        String sqlNGOs = "SELECT COUNT(id) FROM users WHERE role = 'NGO'";
        String sqlPickups = "SELECT COUNT(id) FROM pickups WHERE status = 'DELIVERED'";

        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sqlDonations);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("donationsRescued", rs.getInt(1));
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(sqlProviders);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("foodProviders", rs.getInt(1));
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(sqlNGOs);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("ngoPartners", rs.getInt(1));
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(sqlPickups);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("successfulPickups", rs.getInt(1));
                }
            }
        }
        return stats;
    }

    public List<Map<String, String>> getPublicAvailableFood() throws SQLException {
        List<Map<String, String>> foodList = new ArrayList<>();
        String sql = "SELECT f.id, f.food_name, f.quantity, f.unit, f.pickup_address, f.expiry_time, u.name AS provider_name " +
                     "FROM food_listings f " +
                     "JOIN users u ON f.provider_id = u.id " +
                     "WHERE f.status = 'AVAILABLE' AND f.expiry_time > CURRENT_TIMESTAMP " +
                     "ORDER BY f.expiry_time ASC LIMIT 6";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, String> food = new HashMap<>();
                food.put("id", String.valueOf(rs.getLong("id")));
                food.put("foodName", rs.getString("food_name"));
                food.put("quantity", String.valueOf(rs.getInt("quantity")));
                food.put("unit", rs.getString("unit"));
                food.put("pickupAddress", rs.getString("pickup_address"));
                food.put("providerName", rs.getString("provider_name"));

                java.sql.Timestamp expiry = rs.getTimestamp("expiry_time");
                if (expiry != null) {
                    food.put("expiryTime", dateFormat.format(expiry));
                } else {
                    food.put("expiryTime", "");
                }
                foodList.add(food);
            }
        }
        return foodList;
    }
}
