package com.foodrescue.service;

import com.foodrescue.dao.FoodClaimDAO;
import com.foodrescue.dao.PickupDAO;
import com.foodrescue.model.FoodClaim;
import com.foodrescue.model.Pickup;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PickupService {

    private final PickupDAO pickupDAO;
    private final FoodClaimDAO foodClaimDAO;

    public PickupService() {
        this.pickupDAO = new PickupDAO();
        this.foodClaimDAO = new FoodClaimDAO();
    }

    // Method 1: Create Pickup
    public boolean createPickup(Pickup pickup) throws SQLException {
        if (pickup == null) {
            return false;
        }
        if (pickup.getClaimId() <= 0) {
            return false;
        }
        if (pickup.getVolunteerId() <= 0) {
            return false;
        }
        if (pickup.getStatus() == null || pickup.getStatus().trim().isEmpty()) {
            return false;
        }

        FoodClaim claim = foodClaimDAO.getFoodClaimById(pickup.getClaimId());
        
        if (claim == null) {
            return false;
        }

        if (!"READY_FOR_PICKUP".equalsIgnoreCase(claim.getStatus())) {
            return false;
        }

        pickup.setStatus(pickup.getStatus().trim());
        if (pickup.getNotes() != null) {
            pickup.setNotes(pickup.getNotes().trim());
        }

        return pickupDAO.createPickup(pickup);
    }

    // Method 2: Get Pickup by ID
    public Pickup getPickupById(long id) throws SQLException {
        if (id <= 0) {
            return null;
        }
        return pickupDAO.getPickupById(id);
    }

    // Method 3: Get All Pickups
    public List<Pickup> getAllPickups() throws SQLException {
        return pickupDAO.getAllPickups();
    }

    // Method 4: Get Pickups by Volunteer
    public List<Pickup> getPickupsByVolunteer(long volunteerId) throws SQLException {
        if (volunteerId <= 0) {
            return Collections.emptyList();
        }
        return pickupDAO.getPickupsByVolunteer(volunteerId);
    }

    // Method 5: Get Pickup by Claim
    public Pickup getPickupByClaim(long claimId) throws SQLException {
        if (claimId <= 0) {
            return null;
        }
        return pickupDAO.getPickupByClaim(claimId);
    }

    // Method 6: Update Pickup
    public boolean updatePickup(Pickup pickup) throws SQLException {
        if (pickup == null) {
            return false;
        }
        if (pickup.getId() <= 0) {
            return false;
        }
        if (pickup.getVolunteerId() <= 0) {
            return false;
        }
        if (pickup.getStatus() == null || pickup.getStatus().trim().isEmpty()) {
            return false;
        }

        pickup.setStatus(pickup.getStatus().trim());
        if (pickup.getNotes() != null) {
            pickup.setNotes(pickup.getNotes().trim());
        }

        return pickupDAO.updatePickup(pickup);
    }

    // Method 7: Update Pickup Status
    public boolean updatePickupStatus(long id, String status) throws SQLException {
        if (id <= 0) {
            return false;
        }
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        return pickupDAO.updatePickupStatus(id, status.trim());
    }

    // Method 8: Delete Pickup
    public boolean deletePickup(long id) throws SQLException {
        if (id <= 0) {
            return false;
        }
        return pickupDAO.deletePickup(id);
    }

    // Method 9: Accept Pickup
    public boolean acceptPickup(long claimId, long volunteerId) throws SQLException {
        if (claimId <= 0 || volunteerId <= 0) {
            return false;
        }
        return pickupDAO.acceptPickup(claimId, volunteerId);
    }

    // Method 10: Mark Picked Up
    public boolean markPickedUp(long pickupId, long volunteerId) throws SQLException {
        if (pickupId <= 0 || volunteerId <= 0) {
            return false;
        }
        return pickupDAO.updatePickupStatusSafe(pickupId, volunteerId, "ACCEPTED", "PICKED_UP");
    }

    // Method 11: Mark Delivered
    public boolean markDelivered(long pickupId, long volunteerId) throws SQLException {
        if (pickupId <= 0 || volunteerId <= 0) {
            return false;
        }
        return pickupDAO.updatePickupStatusSafe(pickupId, volunteerId, "PICKED_UP", "DELIVERED");
    }

    // Method 12: Get My Pickups With Details
    public List<com.foodrescue.model.PickupDTO> getMyPickupsWithDetails(long volunteerId) throws SQLException {
        if (volunteerId <= 0) {
            return Collections.emptyList();
        }
        return pickupDAO.getMyPickupsWithDetails(volunteerId);
    }
}
