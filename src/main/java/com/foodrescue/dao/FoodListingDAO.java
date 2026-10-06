package com.foodrescue.dao;

import com.foodrescue.model.FoodListing;
import com.foodrescue.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodListingDAO {

    // INSERT a new food listing
    public boolean createFoodListing(FoodListing foodListing) throws SQLException {
        String sql = "INSERT INTO food_listings (provider_id, food_name, description, quantity, unit, food_type, prepared_at, expiry_time, pickup_address, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, foodListing.getProviderId());
            statement.setString(2, foodListing.getFoodName());
            statement.setString(3, foodListing.getDescription());
            statement.setInt(4, foodListing.getQuantity());
            statement.setString(5, foodListing.getUnit());
            statement.setString(6, foodListing.getFoodType());
            statement.setTimestamp(7, foodListing.getPreparedAt());
            statement.setTimestamp(8, foodListing.getExpiryTime());
            statement.setString(9, foodListing.getPickupAddress());
            statement.setString(10, foodListing.getStatus());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // SELECT food listing by ID
    public FoodListing getFoodListingById(long id) throws SQLException {
        String sql = "SELECT id, provider_id, food_name, description, quantity, unit, food_type, prepared_at, expiry_time, pickup_address, status, created_at, updated_at FROM food_listings WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFoodListing(rs);
                }
            }
        }
        return null;
    }

    // SELECT all food listings
    public List<FoodListing> getAllFoodListings() throws SQLException {
        List<FoodListing> listings = new ArrayList<>();
        String sql = "SELECT id, provider_id, food_name, description, quantity, unit, food_type, prepared_at, expiry_time, pickup_address, status, created_at, updated_at FROM food_listings ORDER BY id ASC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            
            while (rs.next()) {
                listings.add(mapResultSetToFoodListing(rs));
            }
        }
        return listings;
    }

    // SELECT food listings by provider
    public List<FoodListing> getFoodListingsByProvider(long providerId) throws SQLException {
        List<FoodListing> listings = new ArrayList<>();
        String sql = "SELECT id, provider_id, food_name, description, quantity, unit, food_type, prepared_at, expiry_time, pickup_address, status, created_at, updated_at FROM food_listings WHERE provider_id = ? ORDER BY id DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, providerId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    listings.add(mapResultSetToFoodListing(rs));
                }
            }
        }
        return listings;
    }

    // SELECT available food listings
    public List<FoodListing> getAvailableFoodListings() throws SQLException {
        List<FoodListing> listings = new ArrayList<>();
        String sql = "SELECT id, provider_id, food_name, description, quantity, unit, food_type, prepared_at, expiry_time, pickup_address, status, created_at, updated_at FROM food_listings WHERE status = 'AVAILABLE' AND expiry_time > CURRENT_TIMESTAMP ORDER BY expiry_time ASC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            
            while (rs.next()) {
                listings.add(mapResultSetToFoodListing(rs));
            }
        }
        return listings;
    }

    // UPDATE a food listing
    public boolean updateFoodListing(FoodListing foodListing) throws SQLException {
        String sql = "UPDATE food_listings SET food_name = ?, description = ?, quantity = ?, unit = ?, food_type = ?, prepared_at = ?, expiry_time = ?, pickup_address = ?, status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, foodListing.getFoodName());
            statement.setString(2, foodListing.getDescription());
            statement.setInt(3, foodListing.getQuantity());
            statement.setString(4, foodListing.getUnit());
            statement.setString(5, foodListing.getFoodType());
            statement.setTimestamp(6, foodListing.getPreparedAt());
            statement.setTimestamp(7, foodListing.getExpiryTime());
            statement.setString(8, foodListing.getPickupAddress());
            statement.setString(9, foodListing.getStatus());
            statement.setLong(10, foodListing.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // UPDATE food listing status
    public boolean updateFoodListingStatus(long id, String status) throws SQLException {
        String sql = "UPDATE food_listings SET status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, status);
            statement.setLong(2, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // DELETE a food listing
    public boolean deleteFoodListing(long id) throws SQLException {
        String sql = "DELETE FROM food_listings WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map ResultSet to FoodListing object
    private FoodListing mapResultSetToFoodListing(ResultSet rs) throws SQLException {
        FoodListing foodListing = new FoodListing();
        foodListing.setId(rs.getLong("id"));
        foodListing.setProviderId(rs.getLong("provider_id"));
        foodListing.setFoodName(rs.getString("food_name"));
        foodListing.setDescription(rs.getString("description"));
        foodListing.setQuantity(rs.getInt("quantity"));
        foodListing.setUnit(rs.getString("unit"));
        foodListing.setFoodType(rs.getString("food_type"));
        foodListing.setPreparedAt(rs.getTimestamp("prepared_at"));
        foodListing.setExpiryTime(rs.getTimestamp("expiry_time"));
        foodListing.setPickupAddress(rs.getString("pickup_address"));
        foodListing.setStatus(rs.getString("status"));
        foodListing.setCreatedAt(rs.getTimestamp("created_at"));
        foodListing.setUpdatedAt(rs.getTimestamp("updated_at"));
        return foodListing;
    }
}
