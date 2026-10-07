package com.foodrescue.dao;

import com.foodrescue.model.Pickup;
import com.foodrescue.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PickupDAO {

    // INSERT a new pickup
    public boolean createPickup(Pickup pickup) throws SQLException {
        String sql = "INSERT INTO pickups (claim_id, volunteer_id, pickup_time, status, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, pickup.getClaimId());
            statement.setLong(2, pickup.getVolunteerId());
            statement.setTimestamp(3, pickup.getPickupTime());
            statement.setString(4, pickup.getStatus());
            statement.setString(5, pickup.getNotes());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // SELECT pickup by ID
    public Pickup getPickupById(long id) throws SQLException {
        String sql = "SELECT id, claim_id, volunteer_id, pickup_time, status, notes, created_at, updated_at FROM pickups WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPickup(rs);
                }
            }
        }
        return null;
    }

    // SELECT all pickups
    public List<Pickup> getAllPickups() throws SQLException {
        List<Pickup> pickups = new ArrayList<>();
        String sql = "SELECT id, claim_id, volunteer_id, pickup_time, status, notes, created_at, updated_at FROM pickups ORDER BY id ASC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                pickups.add(mapResultSetToPickup(rs));
            }
        }
        return pickups;
    }

    // SELECT pickups by Volunteer
    public List<Pickup> getPickupsByVolunteer(long volunteerId) throws SQLException {
        List<Pickup> pickups = new ArrayList<>();
        String sql = "SELECT id, claim_id, volunteer_id, pickup_time, status, notes, created_at, updated_at FROM pickups WHERE volunteer_id = ? ORDER BY id DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, volunteerId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    pickups.add(mapResultSetToPickup(rs));
                }
            }
        }
        return pickups;
    }

    // SELECT pickup by Claim
    public Pickup getPickupByClaim(long claimId) throws SQLException {
        String sql = "SELECT id, claim_id, volunteer_id, pickup_time, status, notes, created_at, updated_at FROM pickups WHERE claim_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, claimId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPickup(rs);
                }
            }
        }
        return null;
    }

    // UPDATE pickup
    public boolean updatePickup(Pickup pickup) throws SQLException {
        String sql = "UPDATE pickups SET volunteer_id = ?, pickup_time = ?, status = ?, notes = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, pickup.getVolunteerId());
            statement.setTimestamp(2, pickup.getPickupTime());
            statement.setString(3, pickup.getStatus());
            statement.setString(4, pickup.getNotes());
            statement.setLong(5, pickup.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // UPDATE pickup status
    public boolean updatePickupStatus(long id, String status) throws SQLException {
        String sql = "UPDATE pickups SET status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setLong(2, id);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // DELETE pickup
    public boolean deletePickup(long id) throws SQLException {
        String sql = "DELETE FROM pickups WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map ResultSet to Pickup object
    private Pickup mapResultSetToPickup(ResultSet rs) throws SQLException {
        Pickup pickup = new Pickup();
        pickup.setId(rs.getLong("id"));
        pickup.setClaimId(rs.getLong("claim_id"));
        pickup.setVolunteerId(rs.getLong("volunteer_id"));
        pickup.setPickupTime(rs.getTimestamp("pickup_time"));
        pickup.setStatus(rs.getString("status"));
        pickup.setNotes(rs.getString("notes"));
        pickup.setCreatedAt(rs.getTimestamp("created_at"));
        pickup.setUpdatedAt(rs.getTimestamp("updated_at"));
        return pickup;
    }

    // Volunteer Action: ACCEPT pickup (concurrency safe)
    public boolean acceptPickup(long claimId, long volunteerId) throws SQLException {
        String checkClaimSql = "SELECT status FROM food_claims WHERE id = ? FOR UPDATE";
        String checkPickupSql = "SELECT id FROM pickups WHERE claim_id = ? AND status != 'CANCELLED'";
        String insertSql = "INSERT INTO pickups (claim_id, volunteer_id, status, pickup_time) VALUES (?, ?, 'ACCEPTED', NOW())";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // 1. Lock and check claim status
                try (PreparedStatement stmt = connection.prepareStatement(checkClaimSql)) {
                    stmt.setLong(1, claimId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            if (!"READY_FOR_PICKUP".equals(rs.getString("status"))) {
                                connection.rollback();
                                return false; // Claim is not ready
                            }
                        } else {
                            connection.rollback();
                            return false; // Claim doesn't exist
                        }
                    }
                }

                // 2. Check if pickup already exists
                try (PreparedStatement stmt = connection.prepareStatement(checkPickupSql)) {
                    stmt.setLong(1, claimId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            connection.rollback();
                            return false; // Already has an active pickup
                        }
                    }
                }

                // 3. Insert new pickup
                try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                    stmt.setLong(1, claimId);
                    stmt.setLong(2, volunteerId);
                    stmt.executeUpdate();
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

    // Volunteer Action: Update status (safe ownership check)
    public boolean updatePickupStatusSafe(long pickupId, long volunteerId, String expectedStatus, String newStatus) throws SQLException {
        String sql = "UPDATE pickups SET status = ? WHERE id = ? AND volunteer_id = ? AND status = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setLong(2, pickupId);
            stmt.setLong(3, volunteerId);
            stmt.setString(4, expectedStatus);
            return stmt.executeUpdate() > 0;
        }
    }

    // Volunteer Dashboard: Get my pickups with details
    public List<com.foodrescue.model.PickupDTO> getMyPickupsWithDetails(long volunteerId) throws SQLException {
        List<com.foodrescue.model.PickupDTO> list = new ArrayList<>();
        String sql = "SELECT p.id AS pickup_id, c.id AS claim_id, f.food_name, " +
                     "p_usr.name AS provider_name, f.pickup_address AS provider_address, " +
                     "n_usr.name AS ngo_name, n_usr.address AS ngo_address, " +
                     "p.status, p.pickup_time, f.expiry_time, c.claimed_quantity, f.unit " +
                     "FROM pickups p " +
                     "JOIN food_claims c ON p.claim_id = c.id " +
                     "JOIN food_listings f ON c.food_id = f.id " +
                     "JOIN users p_usr ON f.provider_id = p_usr.id " +
                     "JOIN users n_usr ON c.ngo_id = n_usr.id " +
                     "WHERE p.volunteer_id = ? " +
                     "ORDER BY p.id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, volunteerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    com.foodrescue.model.PickupDTO dto = new com.foodrescue.model.PickupDTO();
                    dto.setPickupId(rs.getLong("pickup_id"));
                    dto.setClaimId(rs.getLong("claim_id"));
                    dto.setFoodName(rs.getString("food_name"));
                    dto.setProviderName(rs.getString("provider_name"));
                    dto.setProviderAddress(rs.getString("provider_address"));
                    dto.setNgoName(rs.getString("ngo_name"));
                    dto.setNgoAddress(rs.getString("ngo_address"));
                    dto.setStatus(rs.getString("status"));
                    dto.setPickupTime(rs.getTimestamp("pickup_time"));
                    dto.setExpiryTime(rs.getTimestamp("expiry_time"));
                    dto.setQuantity(rs.getInt("claimed_quantity"));
                    dto.setUnit(rs.getString("unit"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
}
