package com.hostel.management_system.model;

import jakarta.persistence.*;

/**
 * GRASP – Information Expert:
 * Room owns its own occupancy/availability state and exposes
 * isAvailable(), occupy(), and release() so that no other class
 * needs to reach in and mutate occupiedCount directly.
 *
 * SOLID – SRP: Room is only responsible for room-domain data.
 * RoomService handles persistence; BookingStateManager handles booking lifecycle.
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    private Integer roomId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int occupiedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    private double price;  // monthly rent

    // ── GRASP: Information Expert ──────────────────────────────────────

    /** Room knows whether it can accept another occupant. */
    public boolean isAvailable() {
        return status == RoomStatus.AVAILABLE && occupiedCount < capacity;
    }

    /** Available slots for display. */
    public int getAvailableSlots() {
        return Math.max(0, capacity - occupiedCount);
    }

    /**
     * Increment occupancy; update status when full.
     * Called by RoomService after a booking is approved.
     */
    public void occupy() {
        if (!isAvailable()) {
            throw new IllegalStateException("Room " + roomId + " is not available.");
        }
        this.occupiedCount++;
        if (this.occupiedCount >= this.capacity) {
            this.status = RoomStatus.FULL;
        }
    }

    /**
     * Decrement occupancy; reopen status when a slot frees up.
     * Called by RoomService when a student vacates.
     */
    public void release() {
        if (this.occupiedCount > 0) {
            this.occupiedCount--;
        }
        if (this.status == RoomStatus.FULL && this.occupiedCount < this.capacity) {
            this.status = RoomStatus.AVAILABLE;
        }
    }

    // ── Getters / Setters ──────────────────────────────────────────────

    public Integer getRoomId()            { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }

    public String getType()               { return type; }
    public void setType(String type)      { this.type = type; }

    public int getCapacity()              { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getOccupiedCount()                    { return occupiedCount; }
    public void setOccupiedCount(int occupiedCount)  { this.occupiedCount = occupiedCount; }

    public RoomStatus getStatus()                    { return status; }
    public void setStatus(RoomStatus status)         { this.status = status; }

    public double getPrice()              { return price; }
    public void setPrice(double price)    { this.price = price; }
}
