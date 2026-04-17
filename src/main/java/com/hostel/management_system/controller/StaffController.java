package com.hostel.management_system.controller;

import com.hostel.management_system.facade.ComplaintSystemFacade;
import com.hostel.management_system.service.AccountantPaymentOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffController {

    private final ComplaintSystemFacade complaintSystemFacade;
    private final AccountantPaymentOperations accountantPaymentOperations;

    public StaffController(ComplaintSystemFacade complaintSystemFacade,
                           AccountantPaymentOperations accountantPaymentOperations) {
        this.complaintSystemFacade = complaintSystemFacade;
        this.accountantPaymentOperations = accountantPaymentOperations;
    }

    @GetMapping("/warden/dashboard")
    public String wardenDashboard(Model model) {
        model.addAttribute("openComplaintCount", complaintSystemFacade.countOpenComplaints());
        model.addAttribute("recentComplaints", complaintSystemFacade.viewAllComplaints().stream().limit(5).toList());
        return "warden-dashboard";
    }

    @GetMapping("/accountant/dashboard")
    public String accountantDashboard(Model model) {
        model.addAttribute("offlinePayments", accountantPaymentOperations.getPendingOfflinePayments());
        model.addAttribute("refundRequests", accountantPaymentOperations.getPendingRefundRequests());
        model.addAttribute("disputes", accountantPaymentOperations.getOpenDisputes());
        return "accountant-dashboard";
    }
}
