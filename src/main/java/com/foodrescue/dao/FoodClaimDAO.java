package com.foodrescue.dao;

import com.foodrescue.model.FoodClaim;
import com.foodrescue.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodClaimDAO {

    // INSERT a new food claim
    public boolean createFoodClaim(FoodClaim foodClaim) throws SQLException {
        String sql = "INSERT INTO food_claims (food_id, ngo_id, claimed_quantity, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, foodClaim.getFoodId());
            statement.setLong(2, foodClaim.getNgoId());
            statement.setInt(3, foodClaim.getClaimedQuantity());
            statement.setString(4, foodClaim.getStatus());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // SELECT food claim by ID
    public FoodClaim getFoodClaimById(long id) throws SQLException {
        String sql = "SELECT id, food_id, ngo_id, claimed_quantity, status, claimed_at, updated_at FROM food_claims WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFoodClaim(rs);
                }
            }
        }
        return null;
    }

    // SELECT all food claims
    public List<FoodClaim> getAllFoodClaims() throws SQLException {
        List<FoodClaim> claims = new ArrayList<>();
        String sql = "SELECT id, food_id, ngo_id, claimed_quantity, status, claimed_at, updated_at FROM food_claims ORDER BY id ASC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            
            while (rs.next()) {
                claims.add(mapResultSetToFoodClaim(rs));
            }
        }
        return claims;
    }

    // SELECT food claims by NGO ID
    public List<FoodClaim> getFoodClaimsByNgo(long ngoId) throws SQLException {
        List<FoodClaim> claims = new ArrayList<>();
        String sql = "SELECT id, food_id, ngo_id, claimed_quantity, status, claimed_at, updated_at FROM food_claims WHERE ngo_id = ? ORDER BY id DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, ngoId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    claims.add(mapResultSetToFoodClaim(rs));
                }
            }
        }
        return claims;
    }

    // SELECT food claims by Food ID
    public List<FoodClaim> getFoodClaimsByFood(long foodId) throws SQLException {
        List<FoodClaim> claims = new ArrayList<>();
        String sql = "SELECT id, food_id, ngo_id, claimed_quantity, status, claimed_at, updated_at FROM food_claims WHERE food_id = ? ORDER BY id DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, foodId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    claims.add(mapResultSetToFoodClaim(rs));
                }
            }
        }
        return claims;
    }

    // UPDATE food claim
    public boolean updateFoodClaim(FoodClaim foodClaim) throws SQLException {
        String sql = "UPDATE food_claims SET claimed_quantity = ?, status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, foodClaim.getClaimedQuantity());
            statement.setString(2, foodClaim.getStatus());
            statement.setLong(3, foodClaim.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // UPDATE food claim status
    public boolean updateFoodClaimStatus(long id, String status) throws SQLException {
        String sql = "UPDATE food_claims SET status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, status);
            statement.setLong(2, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // DELETE food claim
    public boolean deleteFoodClaim(long id) throws SQLException {
        String sql = "DELETE FROM food_claims WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map ResultSet to FoodClaim object
    private FoodClaim mapResultSetToFoodClaim(ResultSet rs) throws SQLException {
        FoodClaim foodClaim = new FoodClaim();
        foodClaim.setId(rs.getLong("id"));
        foodClaim.setFoodId(rs.getLong("food_id"));
        foodClaim.setNgoId(rs.getLong("ngo_id"));
        foodClaim.setClaimedQuantity(rs.getInt("claimed_quantity"));
        foodClaim.setStatus(rs.getString("status"));
        foodClaim.setClaimedAt(rs.getTimestamp("claimed_at"));
        foodClaim.setUpdatedAt(rs.getTimestamp("updated_at"));
        return foodClaim;
    }
}
