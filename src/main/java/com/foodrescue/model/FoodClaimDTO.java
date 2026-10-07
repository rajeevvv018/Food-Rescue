package com.foodrescue.model;

import java.sql.Timestamp;

public class FoodClaimDTO {
    private long claimId;
    private long foodId;
    private String foodName;
    private int claimedQuantity;
    private String unit;
    private String providerName;
    private String ngoName;
    private String ngoPhone;
    private String ngoEmail;
    private String pickupAddress;
    private String status;
    private Timestamp claimedAt;
    private Timestamp updatedAt;

    public FoodClaimDTO() {
    }

    public FoodClaimDTO(long claimId, long foodId, String foodName, int claimedQuantity,
                        String unit, String providerName, String pickupAddress,
                        String status, Timestamp claimedAt, Timestamp updatedAt) {
        this.claimId = claimId;
        this.foodId = foodId;
        this.foodName = foodName;
        this.claimedQuantity = claimedQuantity;
        this.unit = unit;
        this.providerName = providerName;
        this.pickupAddress = pickupAddress;
        this.status = status;
        this.claimedAt = claimedAt;
        this.updatedAt = updatedAt;
    }

    public FoodClaimDTO(long claimId, long foodId, String foodName, int claimedQuantity,
                        String unit, String providerName, String ngoName, String ngoPhone,
                        String ngoEmail, String pickupAddress, String status,
                        Timestamp claimedAt, Timestamp updatedAt) {
        this.claimId = claimId;
        this.foodId = foodId;
        this.foodName = foodName;
        this.claimedQuantity = claimedQuantity;
        this.unit = unit;
        this.providerName = providerName;
        this.ngoName = ngoName;
        this.ngoPhone = ngoPhone;
        this.ngoEmail = ngoEmail;
        this.pickupAddress = pickupAddress;
        this.status = status;
        this.claimedAt = claimedAt;
        this.updatedAt = updatedAt;
    }

    public long getClaimId() {
        return claimId;
    }

    public void setClaimId(long claimId) {
        this.claimId = claimId;
    }

    public long getFoodId() {
        return foodId;
    }

    public void setFoodId(long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getClaimedQuantity() {
        return claimedQuantity;
    }

    public void setClaimedQuantity(int claimedQuantity) {
        this.claimedQuantity = claimedQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }

    public String getNgoPhone() {
        return ngoPhone;
    }

    public void setNgoPhone(String ngoPhone) {
        this.ngoPhone = ngoPhone;
    }

    public String getNgoEmail() {
        return ngoEmail;
    }

    public void setNgoEmail(String ngoEmail) {
        this.ngoEmail = ngoEmail;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(Timestamp claimedAt) {
        this.claimedAt = claimedAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "FoodClaimDTO{" +
                "claimId=" + claimId +
                ", foodId=" + foodId +
                ", foodName='" + foodName + '\'' +
                ", claimedQuantity=" + claimedQuantity +
                ", unit='" + unit + '\'' +
                ", providerName='" + providerName + '\'' +
                ", ngoName='" + ngoName + '\'' +
                ", ngoPhone='" + ngoPhone + '\'' +
                ", ngoEmail='" + ngoEmail + '\'' +
                ", pickupAddress='" + pickupAddress + '\'' +
                ", status='" + status + '\'' +
                ", claimedAt=" + claimedAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
