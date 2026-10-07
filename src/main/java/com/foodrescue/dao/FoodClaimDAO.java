package com.foodrescue.dao;

import com.foodrescue.model.FoodClaim;
import com.foodrescue.model.FoodClaimDTO;
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

    // SELECT food claims with details by NGO ID using a single JOIN query
    public List<FoodClaimDTO> getClaimsWithDetailsByNgo(long ngoId) throws SQLException {
        List<FoodClaimDTO> list = new ArrayList<>();
        String sql = "SELECT " +
                "c.id AS claim_id, " +
                "c.food_id, " +
                "f.food_name, " +
                "c.claimed_quantity, " +
                "f.unit, " +
                "f.pickup_address, " +
                "u.name AS provider_name, " +
                "c.status AS claim_status, " +
                "c.claimed_at, " +
                "c.updated_at " +
                "FROM food_claims c " +
                "JOIN food_listings f ON c.food_id = f.id " +
                "LEFT JOIN users u ON f.provider_id = u.id " +
                "WHERE c.ngo_id = ? " +
                "ORDER BY c.id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, ngoId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFoodClaimDTO(rs));
                }
            }
        }

        return list;
    }

    // Helper method to map ResultSet to FoodClaimDTO object
    private FoodClaimDTO mapResultSetToFoodClaimDTO(ResultSet rs) throws SQLException {
        FoodClaimDTO dto = new FoodClaimDTO();
        dto.setClaimId(rs.getLong("claim_id"));
        dto.setFoodId(rs.getLong("food_id"));
        dto.setFoodName(rs.getString("food_name"));
        dto.setClaimedQuantity(rs.getInt("claimed_quantity"));
        dto.setUnit(rs.getString("unit"));
        dto.setPickupAddress(rs.getString("pickup_address"));
        dto.setProviderName(rs.getString("provider_name"));
        dto.setStatus(rs.getString("claim_status"));
        dto.setClaimedAt(rs.getTimestamp("claimed_at"));
        dto.setUpdatedAt(rs.getTimestamp("updated_at"));
        return dto;
    }

    // SELECT food claims with details by Provider ID using a single JOIN query
    public List<FoodClaimDTO> getClaimsWithDetailsByProvider(long providerId) throws SQLException {
        List<FoodClaimDTO> list = new ArrayList<>();
        String sql = "SELECT " +
                "c.id AS claim_id, " +
                "c.food_id, " +
                "f.food_name, " +
                "c.claimed_quantity, " +
                "f.unit, " +
                "f.pickup_address, " +
                "u.name AS ngo_name, " +
                "u.phone AS ngo_phone, " +
                "u.email AS ngo_email, " +
                "c.status AS claim_status, " +
                "c.claimed_at, " +
                "c.updated_at " +
                "FROM food_claims c " +
                "JOIN food_listings f ON c.food_id = f.id " +
                "JOIN users u ON c.ngo_id = u.id " +
                "WHERE f.provider_id = ? " +
                "ORDER BY c.id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, providerId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    FoodClaimDTO dto = new FoodClaimDTO();
                    dto.setClaimId(rs.getLong("claim_id"));
                    dto.setFoodId(rs.getLong("food_id"));
                    dto.setFoodName(rs.getString("food_name"));
                    dto.setClaimedQuantity(rs.getInt("claimed_quantity"));
                    dto.setUnit(rs.getString("unit"));
                    dto.setPickupAddress(rs.getString("pickup_address"));
                    dto.setNgoName(rs.getString("ngo_name"));
                    dto.setNgoPhone(rs.getString("ngo_phone"));
                    dto.setNgoEmail(rs.getString("ngo_email"));
                    dto.setStatus(rs.getString("claim_status"));
                    dto.setClaimedAt(rs.getTimestamp("claimed_at"));
                    dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                    list.add(dto);
                }
            }
        }

        return list;
    }

    // Provider Action: APPROVE (requires transaction)
    public boolean approveClaim(long claimId, long providerId) throws SQLException {
        String checkSql = "SELECT c.food_id, c.status FROM food_claims c JOIN food_listings f ON c.food_id = f.id WHERE c.id = ? AND f.provider_id = ? FOR UPDATE";
        String updateClaimSql = "UPDATE food_claims SET status = 'APPROVED' WHERE id = ?";
        String updateFoodSql = "UPDATE food_listings SET status = 'CLAIMED' WHERE id = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long foodId = -1;
                try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                    checkStmt.setLong(1, claimId);
                    checkStmt.setLong(2, providerId);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            String status = rs.getString("status");
                            if (!"PENDING".equals(status)) {
                                connection.rollback();
                                return false;
                            }
                            foodId = rs.getLong("food_id");
                        } else {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement claimStmt = connection.prepareStatement(updateClaimSql)) {
                    claimStmt.setLong(1, claimId);
                    claimStmt.executeUpdate();
                }

                try (PreparedStatement foodStmt = connection.prepareStatement(updateFoodSql)) {
                    foodStmt.setLong(1, foodId);
                    foodStmt.executeUpdate();
                }

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    // Provider Action: REJECT
    public boolean rejectClaim(long claimId, long providerId) throws SQLException {
        String sql = "UPDATE food_claims c JOIN food_listings f ON c.food_id = f.id " +
                     "SET c.status = 'REJECTED' " +
                     "WHERE c.id = ? AND f.provider_id = ? AND c.status = 'PENDING'";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, claimId);
            statement.setLong(2, providerId);
            return statement.executeUpdate() > 0;
        }
    }

    // Provider Action: READY_FOR_PICKUP
    public boolean markClaimReadyForPickup(long claimId, long providerId) throws SQLException {
        String sql = "UPDATE food_claims c JOIN food_listings f ON c.food_id = f.id " +
                     "SET c.status = 'READY_FOR_PICKUP' " +
                     "WHERE c.id = ? AND f.provider_id = ? AND c.status = 'APPROVED'";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, claimId);
            statement.setLong(2, providerId);
            return statement.executeUpdate() > 0;
        }
    }

    // Volunteer Dashboard: Get claims ready for pickup that have no active pickups
    public List<com.foodrescue.model.FoodClaimDTO> getAvailablePickups() throws SQLException {
        List<com.foodrescue.model.FoodClaimDTO> list = new ArrayList<>();
        String sql = "SELECT c.id AS claim_id, c.food_id, f.food_name, c.claimed_quantity, " +
                     "f.unit, f.pickup_address, p.name AS provider_name, n.name AS ngo_name, " +
                     "f.expiry_time, c.claimed_at " +
                     "FROM food_claims c " +
                     "JOIN food_listings f ON c.food_id = f.id " +
                     "JOIN users p ON f.provider_id = p.id " +
                     "JOIN users n ON c.ngo_id = n.id " +
                     "WHERE c.status = 'READY_FOR_PICKUP' " +
                     "AND NOT EXISTS (SELECT 1 FROM pickups pick WHERE pick.claim_id = c.id AND pick.status != 'CANCELLED') " +
                     "ORDER BY c.id ASC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                com.foodrescue.model.FoodClaimDTO dto = new com.foodrescue.model.FoodClaimDTO();
                dto.setClaimId(rs.getLong("claim_id"));
                dto.setFoodId(rs.getLong("food_id"));
                dto.setFoodName(rs.getString("food_name"));
                dto.setClaimedQuantity(rs.getInt("claimed_quantity"));
                dto.setUnit(rs.getString("unit"));
                dto.setPickupAddress(rs.getString("pickup_address"));
                dto.setProviderName(rs.getString("provider_name"));
                dto.setNgoName(rs.getString("ngo_name"));
                dto.setClaimedAt(rs.getTimestamp("claimed_at"));
                dto.setExpiryTime(rs.getTimestamp("expiry_time"));
                list.add(dto);
            }
        }
        return list;
    }
}
