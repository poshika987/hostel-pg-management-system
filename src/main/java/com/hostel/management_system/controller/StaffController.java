package com.hostel.management_system.controller;

import com.hostel.management_system.facade.ComplaintSystemFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffController {

    private final ComplaintSystemFacade complaintSystemFacade;

    public StaffController(ComplaintSystemFacade complaintSystemFacade) {
        this.complaintSystemFacade = complaintSystemFacade;
    }

    @GetMapping("/warden/dashboard")
    public String wardenDashboard(Model model) {
        model.addAttribute("openComplaintCount", complaintSystemFacade.countOpenComplaints());
        model.addAttribute("recentComplaints", complaintSystemFacade.viewAllComplaints().stream().limit(5).toList());
        return "warden-dashboard";
    }

    @GetMapping("/accountant/dashboard")
    public String accountantDashboard() {
        return "accountant-dashboard";
    }
}
