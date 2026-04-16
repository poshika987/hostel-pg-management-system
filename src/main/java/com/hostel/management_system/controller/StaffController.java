package com.hostel.management_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffController {

    @GetMapping("/warden/dashboard")
    public String wardenDashboard() {
        return "warden-dashboard";
    }

    @GetMapping("/accountant/dashboard")
    public String accountantDashboard() {
        return "accountant-dashboard";
    }
}
