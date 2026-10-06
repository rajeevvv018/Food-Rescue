package com.foodrescue.service;

import com.foodrescue.dao.FoodListingDAO;
import com.foodrescue.model.FoodListing;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class FoodListingService {

    private final FoodListingDAO foodListingDAO;

    public FoodListingService() {
        this.foodListingDAO = new FoodListingDAO();
    }

    // Method 1: Create Food Listing
    public boolean createFoodListing(FoodListing foodListing) throws SQLException {
        if (foodListing == null) {
            return false;
        }
        if (foodListing.getProviderId() <= 0) {
            return false;
        }
        if (foodListing.getFoodName() == null || foodListing.getFoodName().trim().isEmpty()) {
            return false;
        }
        if (foodListing.getQuantity() <= 0) {
            return false;
        }
        if (foodListing.getUnit() == null || foodListing.getUnit().trim().isEmpty()) {
            return false;
        }
        if (foodListing.getExpiryTime() == null) {
            return false;
        }
        if (foodListing.getPickupAddress() == null || foodListing.getPickupAddress().trim().isEmpty()) {
            return false;
        }
        if (foodListing.getStatus() == null || foodListing.getStatus().trim().isEmpty()) {
            return false;
        }

        // Normalize simple String fields
        foodListing.setFoodName(foodListing.getFoodName().trim());
        foodListing.setUnit(foodListing.getUnit().trim());
        foodListing.setPickupAddress(foodListing.getPickupAddress().trim());
        foodListing.setStatus(foodListing.getStatus().trim());
        
        if (foodListing.getDescription() != null) {
            foodListing.setDescription(foodListing.getDescription().trim());
        }
        if (foodListing.getFoodType() != null) {
            foodListing.setFoodType(foodListing.getFoodType().trim());
        }

        return foodListingDAO.createFoodListing(foodListing);
    }

    // Method 2: Get Food Listing By ID
    public FoodListing getFoodListingById(long id) throws SQLException {
        if (id <= 0) {
            return null;
        }
        return foodListingDAO.getFoodListingById(id);
    }

    // Method 3: Get All Food Listings
    public List<FoodListing> getAllFoodListings() throws SQLException {
        return foodListingDAO.getAllFoodListings();
    }

    // Method 4: Get Provider Food Listings
    public List<FoodListing> getFoodListingsByProvider(long providerId) throws SQLException {
        if (providerId <= 0) {
            return Collections.emptyList();
        }
        return foodListingDAO.getFoodListingsByProvider(providerId);
    }

    // Method 5: Get Available Food
    public List<FoodListing> getAvailableFoodListings() throws SQLException {
        return foodListingDAO.getAvailableFoodListings();
    }

    // Method 6: Update Food Listing
    public boolean updateFoodListing(FoodListing foodListing) throws SQLException {
        if (foodListing == null) {
            return false;
        }
        if (foodListing.getId() <= 0) {
            return false;
        }
        if (foodListing.getFoodName() == null || foodListing.getFoodName().trim().isEmpty()) {
            return false;
        }
        if (foodListing.getQuantity() <= 0) {
            return false;
        }
        if (foodListing.getUnit() == null || foodListing.getUnit().trim().isEmpty()) {
            return false;
        }
        if (foodListing.getExpiryTime() == null) {
            return false;
        }
        if (foodListing.getPickupAddress() == null || foodListing.getPickupAddress().trim().isEmpty()) {
            return false;
        }
        if (foodListing.getStatus() == null || foodListing.getStatus().trim().isEmpty()) {
            return false;
        }

        // Normalize simple String fields
        foodListing.setFoodName(foodListing.getFoodName().trim());
        foodListing.setUnit(foodListing.getUnit().trim());
        foodListing.setPickupAddress(foodListing.getPickupAddress().trim());
        foodListing.setStatus(foodListing.getStatus().trim());

        if (foodListing.getDescription() != null) {
            foodListing.setDescription(foodListing.getDescription().trim());
        }
        if (foodListing.getFoodType() != null) {
            foodListing.setFoodType(foodListing.getFoodType().trim());
        }

        return foodListingDAO.updateFoodListing(foodListing);
    }

    // Method 7: Update Status
    public boolean updateFoodListingStatus(long id, String status) throws SQLException {
        if (id <= 0) {
            return false;
        }
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        return foodListingDAO.updateFoodListingStatus(id, status.trim());
    }

    // Method 8: Delete Food Listing
    public boolean deleteFoodListing(long id) throws SQLException {
        if (id <= 0) {
            return false;
        }
        return foodListingDAO.deleteFoodListing(id);
    }
}
