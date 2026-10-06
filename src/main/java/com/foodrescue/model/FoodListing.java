package com.foodrescue.model;

import java.sql.Timestamp;

public class FoodListing {
    private long id;
    private long providerId;
    private String foodName;
    private String description;
    private int quantity;
    private String unit;
    private String foodType;
    private Timestamp preparedAt;
    private Timestamp expiryTime;
    private String pickupAddress;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public FoodListing() {}

    public FoodListing(long id, long providerId, String foodName, String description, int quantity, String unit, String foodType, Timestamp preparedAt, Timestamp expiryTime, String pickupAddress, String status, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.providerId = providerId;
        this.foodName = foodName;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
        this.foodType = foodType;
        this.preparedAt = preparedAt;
        this.expiryTime = expiryTime;
        this.pickupAddress = pickupAddress;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProviderId() { return providerId; }
    public void setProviderId(long providerId) { this.providerId = providerId; }
    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }
    public Timestamp getPreparedAt() { return preparedAt; }
    public void setPreparedAt(Timestamp preparedAt) { this.preparedAt = preparedAt; }
    public Timestamp getExpiryTime() { return expiryTime; }
    public void setExpiryTime(Timestamp expiryTime) { this.expiryTime = expiryTime; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "FoodListing{" +
                "id=" + id +
                ", providerId=" + providerId +
                ", foodName='" + foodName + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", foodType='" + foodType + '\'' +
                ", preparedAt=" + preparedAt +
                ", expiryTime=" + expiryTime +
                ", pickupAddress='" + pickupAddress + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
