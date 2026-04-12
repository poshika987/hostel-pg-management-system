package com.hostel.management_system.service;

import com.hostel.management_system.exception.ResourceNotFoundException;
import com.hostel.management_system.model.*;
import com.hostel.management_system.model.state.BookingStateManager;
import com.hostel.management_system.proxy.BookingAuthorizationProxy;
import com.hostel.management_system.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SOLID – SRP: Manages booking lifecycle only.
 * Delegates authorization checks to BookingAuthorizationProxy (Structural – Proxy).
 * Delegates state transitions to BookingStateManager (Behavioral – State Pattern).
 */
@Service
public class BookingService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private BookingAuthorizationProxy authProxy;
    @Autowired private AllocationService allocationService;

    @Transactional
    public Booking createBooking(Student student, Room room) {
        authProxy.assertCanBook(student, room);

        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setRoom(room);
        booking.setStatus(BookingStatus.PENDING);
        booking.setRequestDate(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @Transactional
    public void approveBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        BookingStateManager.approve(booking);
        bookingRepository.save(booking);
        allocationService.allocateRoom(booking.getStudent(), booking.getRoom());
    }

    @Transactional
    public void rejectBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        BookingStateManager.reject(booking);
        bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Student student) {
        Booking booking = getBookingById(bookingId);
        if (!booking.getStudent().getId().equals(student.getId())) {
            throw new SecurityException("You can only cancel your own booking.");
        }
        BookingStateManager.cancel(booking);
        bookingRepository.save(booking);
    }

    public List<Booking> getPendingBookings() {
        return bookingRepository.findByStatus(BookingStatus.PENDING);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByStudent(Student student) {
        return bookingRepository.findByStudent(student);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }

    public long countPending() {
        return bookingRepository.findByStatus(BookingStatus.PENDING).size();
    }
}
