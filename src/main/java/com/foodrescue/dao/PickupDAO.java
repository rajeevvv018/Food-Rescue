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
}
