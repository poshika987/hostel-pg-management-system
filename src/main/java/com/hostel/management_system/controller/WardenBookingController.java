package com.hostel.management_system.controller;

import com.hostel.management_system.service.BookingService;
import com.hostel.management_system.service.RoomActionRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/warden/bookings")
public class WardenBookingController {

    private final BookingService bookingService;
    private final RoomActionRequestService roomActionRequestService;

    public WardenBookingController(BookingService bookingService, RoomActionRequestService roomActionRequestService) {
        this.bookingService = bookingService;
        this.roomActionRequestService = roomActionRequestService;
    }

    @GetMapping
    public String bookings(Model model) {
        model.addAttribute("pendingBookings", bookingService.getPendingBookings());
        model.addAttribute("allBookings", bookingService.getAllBookings());
        model.addAttribute("pendingRoomRequests", roomActionRequestService.getPendingRequests());
        model.addAttribute("allRoomRequests", roomActionRequestService.getAllRequests());
        model.addAttribute("actionBase", "/warden/bookings");
        return "warden-bookings";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        bookingService.approveBooking(id);
        ra.addFlashAttribute("successMessage", "Booking approved and room allocated.");
        return "redirect:/warden/bookings";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id, RedirectAttributes ra) {
        bookingService.rejectBooking(id);
        ra.addFlashAttribute("successMessage", "Booking rejected successfully.");
        return "redirect:/warden/bookings";
    }

    @PostMapping("/room-requests/{id}/approve")
    public String approveRoomRequest(@PathVariable Long id, RedirectAttributes ra) {
        roomActionRequestService.approve(id);
        ra.addFlashAttribute("successMessage", "Room request approved successfully.");
        return "redirect:/warden/bookings";
    }

    @PostMapping("/room-requests/{id}/reject")
    public String rejectRoomRequest(@PathVariable Long id, RedirectAttributes ra) {
        roomActionRequestService.reject(id);
        ra.addFlashAttribute("successMessage", "Room request rejected successfully.");
        return "redirect:/warden/bookings";
    }
}
