package com.hostel.management_system.exception;

/**
 * Thrown when a Booking state-transition is invalid.
 * Used by BookingStateManager (State Pattern).
 */
public class InvalidBookingStateException extends RuntimeException {
    public InvalidBookingStateException(String message) {
        super(message);
    }
}
