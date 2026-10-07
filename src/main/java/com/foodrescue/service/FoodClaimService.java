package com.foodrescue.service;

import com.foodrescue.dao.FoodClaimDAO;
import com.foodrescue.dao.FoodListingDAO;
import com.foodrescue.model.FoodClaim;
import com.foodrescue.model.FoodClaimDTO;
import com.foodrescue.model.FoodListing;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class FoodClaimService {

    private final FoodClaimDAO foodClaimDAO;
    private final FoodListingDAO foodListingDAO;

    public FoodClaimService() {
        this.foodClaimDAO = new FoodClaimDAO();
        this.foodListingDAO = new FoodListingDAO();
    }

    // Method 1: Create Food Claim
    public boolean createFoodClaim(FoodClaim foodClaim) throws SQLException {
        if (foodClaim == null) {
            return false;
        }
        if (foodClaim.getFoodId() <= 0) {
            return false;
        }
        if (foodClaim.getNgoId() <= 0) {
            return false;
        }
        if (foodClaim.getClaimedQuantity() <= 0) {
            return false;
        }
        if (foodClaim.getStatus() == null || foodClaim.getStatus().trim().isEmpty()) {
            return false;
        }

        FoodListing foodListing = foodListingDAO.getFoodListingById(foodClaim.getFoodId());
        
        if (foodListing == null) {
            return false;
        }

        if (!"AVAILABLE".equalsIgnoreCase(foodListing.getStatus())) {
            return false;
        }

        if (foodClaim.getClaimedQuantity() > foodListing.getQuantity()) {
            return false;
        }

        foodClaim.setStatus(foodClaim.getStatus().trim());

        return foodClaimDAO.createFoodClaim(foodClaim);
    }

    // Method 2: Get Claim by ID
    public FoodClaim getFoodClaimById(long id) throws SQLException {
        if (id <= 0) {
            return null;
        }
        return foodClaimDAO.getFoodClaimById(id);
    }

    // Method 3: Get All Claims
    public List<FoodClaim> getAllFoodClaims() throws SQLException {
        return foodClaimDAO.getAllFoodClaims();
    }

    // Method 4: Get Claims by NGO
    public List<FoodClaim> getFoodClaimsByNgo(long ngoId) throws SQLException {
        if (ngoId <= 0) {
            return Collections.emptyList();
        }
        return foodClaimDAO.getFoodClaimsByNgo(ngoId);
    }

    // Method 5: Get Claims by Food
    public List<FoodClaim> getFoodClaimsByFood(long foodId) throws SQLException {
        if (foodId <= 0) {
            return Collections.emptyList();
        }
        return foodClaimDAO.getFoodClaimsByFood(foodId);
    }

    // Method 6: Update Claim
    public boolean updateFoodClaim(FoodClaim foodClaim) throws SQLException {
        if (foodClaim == null) {
            return false;
        }
        if (foodClaim.getId() <= 0) {
            return false;
        }
        if (foodClaim.getClaimedQuantity() <= 0) {
            return false;
        }
        if (foodClaim.getStatus() == null || foodClaim.getStatus().trim().isEmpty()) {
            return false;
        }

        foodClaim.setStatus(foodClaim.getStatus().trim());
        
        return foodClaimDAO.updateFoodClaim(foodClaim);
    }

    // Method 7: Update Claim Status
    public boolean updateFoodClaimStatus(long id, String status) throws SQLException {
        if (id <= 0) {
            return false;
        }
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        return foodClaimDAO.updateFoodClaimStatus(id, status.trim());
    }

    // Method 8: Delete Claim
    public boolean deleteFoodClaim(long id) throws SQLException {
        if (id <= 0) {
            return false;
        }
        return foodClaimDAO.deleteFoodClaim(id);
    }

    // Method 9: Get Claims with Details by NGO
    public List<FoodClaimDTO> getClaimsWithDetailsByNgo(long ngoId) throws SQLException {
        if (ngoId <= 0) {
            return Collections.emptyList();
        }
        return foodClaimDAO.getClaimsWithDetailsByNgo(ngoId);
    }

    // Method 10: Get Claims with Details by Provider
    public List<FoodClaimDTO> getClaimsWithDetailsByProvider(long providerId) throws SQLException {
        if (providerId <= 0) {
            return Collections.emptyList();
        }
        return foodClaimDAO.getClaimsWithDetailsByProvider(providerId);
    }
}
