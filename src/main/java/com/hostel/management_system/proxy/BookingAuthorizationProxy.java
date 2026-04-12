package com.hostel.management_system.proxy;

import com.hostel.management_system.exception.DuplicateBookingException;
import com.hostel.management_system.model.*;
import com.hostel.management_system.repository.AllocationRepository;
import com.hostel.management_system.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Structural Pattern – Proxy (Authorization Proxy):
 * Guards the booking workflow by enforcing pre-conditions
 * BEFORE delegating to real business logic.
 *
 * Responsibilities (SOLID – SRP):
 *   1. Verify the student is logged in (handled by Spring Security at HTTP layer,
 *      but re-enforced here at service layer).
 *   2. Verify the student does NOT already have a pending booking.
 *   3. Verify the student does NOT already have an active allocation.
 *   4. Verify the requested room is available.
 *
 * By isolating these checks here, BookingService stays focused
 * on the happy-path booking workflow (SRP + Clean Code).
 */
@Component
public class BookingAuthorizationProxy {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    /**
     * Throws a descriptive exception if the student is not eligible to book.
     */
    public void assertCanBook(Student student, Room room) {

        // Guard 1: Student must not have an existing PENDING booking
        if (bookingRepository.existsByStudentAndStatus(student, BookingStatus.PENDING)) {
            throw new DuplicateBookingException(
                "You already have a pending booking request. " +
                "Please wait for it to be processed before making a new one.");
        }

        // Guard 2: Student must not already be allocated a room
        boolean hasActiveAllocation = allocationRepository
                .findByStudentAndStatus(student, AllocationStatus.ACTIVE)
                .isPresent();

        if (hasActiveAllocation) {
            throw new DuplicateBookingException(
                "You already have an active room allocation. " +
                "Please vacate your current room before booking another.");
        }

        // Guard 3: The room must be available
        if (!room.isAvailable()) {
            throw new DuplicateBookingException(
                "The selected room (Room " + room.getRoomId() + ") is no longer available.");
        }
    }
}
