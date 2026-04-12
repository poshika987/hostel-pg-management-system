package com.hostel.management_system.controller;

import com.hostel.management_system.model.*;
import com.hostel.management_system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC – Controller layer for student-facing features.
 * SOLID – SRP: handles HTTP routing only; delegates business logic to services.
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private RoomService roomService;
    @Autowired private BookingService bookingService;
    @Autowired private AllocationService allocationService;
    @Autowired private UserService userService;

    // ── Helper ───────────────────────────────────────────────────────────
    private Student getStudent(Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        if (!(user instanceof Student s)) throw new RuntimeException("Not a student account.");
        return s;
    }

    // ── Dashboard ────────────────────────────────────────────────────────
    // @GetMapping("/dashboard")
    // public String dashboard(Authentication auth, Model model) {
    //     Student student = getStudent(auth);
    //     model.addAttribute("student", student);
    //     model.addAttribute("bookings", bookingService.getBookingsByStudent(student));
    //     allocationService.getActiveAllocation(student)
    //             .ifPresent(a -> model.addAttribute("allocation", a));
    //     return "student-dashboard";
    // }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Student student = getStudent(auth);

        var bookings = bookingService.getBookingsByStudent(student);

        long pendingCount = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING)
                .count();

        model.addAttribute("student", student);
        model.addAttribute("bookings", bookings);
        model.addAttribute("pendingCount", pendingCount);

        allocationService.getActiveAllocation(student)
                .ifPresent(a -> model.addAttribute("allocation", a));

        return "student-dashboard";
    }

    // ── Browse all rooms ─────────────────────────────────────────────────
    @GetMapping("/rooms")
    public String viewRooms(Authentication auth, Model model) {
        Student student = getStudent(auth);
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("student", student);

        boolean hasPending = bookingService.getBookingsByStudent(student).stream()
                .anyMatch(b -> b.getStatus() == BookingStatus.PENDING);
        boolean hasActiveAllocation = allocationService.getActiveAllocation(student).isPresent();

        model.addAttribute("hasPendingBooking", hasPending);
        model.addAttribute("hasActiveAllocation", hasActiveAllocation);
        allocationService.getActiveAllocation(student)
                .ifPresent(a -> model.addAttribute("currentAllocation", a));
        return "rooms";
    }

    // ── Book a room (POST – state-changing) ──────────────────────────────
    @PostMapping("/book")
    public String bookRoom(@RequestParam Integer roomId,
                           Authentication auth,
                           RedirectAttributes ra) {
        Student student = getStudent(auth);
        Room room = roomService.getRoomById(roomId);
        bookingService.createBooking(student, room);
        ra.addFlashAttribute("successMessage",
                "Booking request submitted for Room " + roomId + ". Awaiting admin approval.");
        return "redirect:/student/rooms";
    }

    // ── Cancel pending booking (POST) ────────────────────────────────────
    @PostMapping("/cancel-booking/{id}")
    public String cancelBooking(@PathVariable Long id,
                                Authentication auth,
                                RedirectAttributes ra) {
        Student student = getStudent(auth);
        bookingService.cancelBooking(id, student);
        ra.addFlashAttribute("successMessage", "Booking request cancelled.");
        return "redirect:/student/dashboard";
    }

    // ── Vacate room (POST – state-changing) ──────────────────────────────
    @PostMapping("/vacate")
    public String vacate(Authentication auth, RedirectAttributes ra) {
        Student student = getStudent(auth);
        allocationService.vacateRoom(student);
        ra.addFlashAttribute("successMessage", "You have successfully vacated your room.");
        return "redirect:/student/dashboard";
    }
}
