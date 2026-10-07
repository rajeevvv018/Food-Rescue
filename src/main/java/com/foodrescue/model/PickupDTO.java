package com.foodrescue.model;

import java.sql.Timestamp;

public class PickupDTO {
    private long pickupId;
    private long claimId;
    private String foodName;
    private String providerName;
    private String providerAddress;
    private String ngoName;
    private String ngoAddress;
    private String status;
    private Timestamp pickupTime;
    private Timestamp expiryTime;
    private int quantity;
    private String unit;

    public PickupDTO() {}

    public long getPickupId() { return pickupId; }
    public void setPickupId(long pickupId) { this.pickupId = pickupId; }

    public long getClaimId() { return claimId; }
    public void setClaimId(long claimId) { this.claimId = claimId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getProviderAddress() { return providerAddress; }
    public void setProviderAddress(String providerAddress) { this.providerAddress = providerAddress; }

    public String getNgoName() { return ngoName; }
    public void setNgoName(String ngoName) { this.ngoName = ngoName; }

    public String getNgoAddress() { return ngoAddress; }
    public void setNgoAddress(String ngoAddress) { this.ngoAddress = ngoAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getPickupTime() { return pickupTime; }
    public void setPickupTime(Timestamp pickupTime) { this.pickupTime = pickupTime; }

    public Timestamp getExpiryTime() { return expiryTime; }
    public void setExpiryTime(Timestamp expiryTime) { this.expiryTime = expiryTime; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
