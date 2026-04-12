package com.hostel.management_system.model.state;

import com.hostel.management_system.exception.InvalidBookingStateException;
import com.hostel.management_system.model.Booking;
import com.hostel.management_system.model.BookingStatus;

/**
 * Behavioral Pattern – State Pattern:
 * Manages valid lifecycle transitions for a Booking.
 *
 * Valid transitions:
 *   PENDING  → APPROVED
 *   PENDING  → REJECTED
 *   PENDING  → CANCELLED
 *
 * Invalid transitions throw InvalidBookingStateException,
 * enforcing domain rules and preventing illegal state mutations.
 *
 * SOLID – Single Responsibility (SRP):
 * This class owns ONLY the booking state-transition logic,
 * separate from persistence (BookingRepository) and HTTP (BookingController).
 */
public class BookingStateManager {

    private BookingStateManager() {}

    public static void approve(Booking booking) {
        requireState(booking, BookingStatus.PENDING, "approve");
        booking.setStatus(BookingStatus.APPROVED);
    }

    public static void reject(Booking booking) {
        requireState(booking, BookingStatus.PENDING, "reject");
        booking.setStatus(BookingStatus.REJECTED);
    }

    public static void cancel(Booking booking) {
        requireState(booking, BookingStatus.PENDING, "cancel");
        booking.setStatus(BookingStatus.CANCELLED);
    }

    // ── helpers ─────────────────────────────────────────────────────────
    private static void requireState(Booking booking,
                                     BookingStatus required,
                                     String action) {
        if (booking.getStatus() != required) {
            throw new InvalidBookingStateException(
                "Cannot " + action + " a booking in state: " + booking.getStatus());
        }
    }
}
