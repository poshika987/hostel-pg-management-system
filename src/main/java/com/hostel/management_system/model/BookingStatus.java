package com.hostel.management_system.model;

/**
 * Behavioral Pattern – State Pattern:
 * Represents the lifecycle states of a Booking:
 *   PENDING → APPROVED → (room becomes OCCUPIED)
 *   PENDING → REJECTED
 *   PENDING → CANCELLED (by student)
 */
public enum BookingStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}