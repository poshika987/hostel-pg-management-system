package com.hostel.management_system.controller;

import com.hostel.management_system.model.*;
import com.hostel.management_system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC – Admin Controller.
 * SOLID – SRP: delegates all business logic to services.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private BookingService bookingService;
    @Autowired private AllocationService allocationService;
    @Autowired private RoomService roomService;
    @Autowired private RoomActionRequestService roomActionRequestService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pendingCount", bookingService.countPending());
        model.addAttribute("totalRooms", roomService.getAllRooms().size());
        model.addAttribute("activeAllocations", allocationService.countActive());
        model.addAttribute("allRooms", roomService.getAllRooms());
        return "admin-dashboard";
    }

    @GetMapping("/bookings")
    public String viewBookings(Model model) {
        model.addAttribute("pendingBookings", bookingService.getPendingBookings());
        model.addAttribute("allBookings", bookingService.getAllBookings());
        model.addAttribute("pendingRoomRequests", roomActionRequestService.getPendingRequests());
        model.addAttribute("allRoomRequests", roomActionRequestService.getAllRequests());
        return "admin-bookings";
    }

    @GetMapping("/rooms")
    public String viewRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("allocations", allocationService.getAllAllocations());
        return "admin-rooms";
    }

    @PostMapping("/rooms/{id}/rent")
    public String updateRent(@PathVariable Integer id,
                             @RequestParam double price,
                             RedirectAttributes ra) {
        roomService.updateRoomRent(id, price);
        ra.addFlashAttribute("successMessage", "Monthly rent updated for Room " + id + ".");
        return "redirect:/admin/rooms";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        bookingService.approveBooking(id);
        ra.addFlashAttribute("successMessage", "Booking approved and room allocated.");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id, RedirectAttributes ra) {
        bookingService.rejectBooking(id);
        ra.addFlashAttribute("successMessage", "Booking rejected successfully.");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/room-requests/{id}/approve")
    public String approveRoomRequest(@PathVariable Long id, RedirectAttributes ra) {
        roomActionRequestService.approve(id);
        ra.addFlashAttribute("successMessage", "Room request approved successfully.");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/room-requests/{id}/reject")
    public String rejectRoomRequest(@PathVariable Long id, RedirectAttributes ra) {
        roomActionRequestService.reject(id);
        ra.addFlashAttribute("successMessage", "Room request rejected successfully.");
        return "redirect:/admin/bookings";
    }
}
