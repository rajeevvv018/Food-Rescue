package com.foodrescue.model;

import java.sql.Timestamp;

public class FoodClaim {
    private long id;
    private long foodId;
    private long ngoId;
    private int claimedQuantity;
    private String status;
    private Timestamp claimedAt;
    private Timestamp updatedAt;

    public FoodClaim() {}

    public FoodClaim(long id, long foodId, long ngoId, int claimedQuantity, String status, Timestamp claimedAt, Timestamp updatedAt) {
        this.id = id;
        this.foodId = foodId;
        this.ngoId = ngoId;
        this.claimedQuantity = claimedQuantity;
        this.status = status;
        this.claimedAt = claimedAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getFoodId() { return foodId; }
    public void setFoodId(long foodId) { this.foodId = foodId; }
    public long getNgoId() { return ngoId; }
    public void setNgoId(long ngoId) { this.ngoId = ngoId; }
    public int getClaimedQuantity() { return claimedQuantity; }
    public void setClaimedQuantity(int claimedQuantity) { this.claimedQuantity = claimedQuantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getClaimedAt() { return claimedAt; }
    public void setClaimedAt(Timestamp claimedAt) { this.claimedAt = claimedAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "FoodClaim{" +
                "id=" + id +
                ", foodId=" + foodId +
                ", ngoId=" + ngoId +
                ", claimedQuantity=" + claimedQuantity +
                ", status='" + status + '\'' +
                ", claimedAt=" + claimedAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
