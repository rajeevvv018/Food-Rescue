package com.foodrescue.model;

import java.sql.Timestamp;

public class Pickup {
    private long id;
    private long claimId;
    private long volunteerId;
    private Timestamp pickupTime;
    private String status;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Pickup() {}

    public Pickup(long id, long claimId, long volunteerId, Timestamp pickupTime, String status, String notes, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.claimId = claimId;
        this.volunteerId = volunteerId;
        this.pickupTime = pickupTime;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getClaimId() { return claimId; }
    public void setClaimId(long claimId) { this.claimId = claimId; }
    public long getVolunteerId() { return volunteerId; }
    public void setVolunteerId(long volunteerId) { this.volunteerId = volunteerId; }
    public Timestamp getPickupTime() { return pickupTime; }
    public void setPickupTime(Timestamp pickupTime) { this.pickupTime = pickupTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Pickup{" +
                "id=" + id +
                ", claimId=" + claimId +
                ", volunteerId=" + volunteerId +
                ", pickupTime=" + pickupTime +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
