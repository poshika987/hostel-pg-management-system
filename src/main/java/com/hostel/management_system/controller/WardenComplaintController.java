package com.hostel.management_system.controller;

import com.hostel.management_system.facade.ComplaintSystemFacade;
import com.hostel.management_system.model.ComplaintStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping("/warden/complaints")
public class WardenComplaintController {

    private final ComplaintSystemFacade complaintSystemFacade;

    public WardenComplaintController(ComplaintSystemFacade complaintSystemFacade) {
        this.complaintSystemFacade = complaintSystemFacade;
    }

    @GetMapping
    public String complaints(Model model) {
        model.addAttribute("complaints", complaintSystemFacade.viewAllComplaints());
        model.addAttribute("staffMembers", complaintSystemFacade.getMaintenanceStaff());
        model.addAttribute("statuses", Arrays.stream(ComplaintStatus.values())
                .filter(status -> status != ComplaintStatus.REOPENED)
                .toList());
        model.addAttribute("openComplaintCount", complaintSystemFacade.countOpenComplaints());
        return "warden-complaints";
    }

    @PostMapping("/{id}/assign")
    public String assign(@PathVariable Long id,
                         @RequestParam Long staffId,
                         RedirectAttributes ra) {
        complaintSystemFacade.assignTaskToMaintenanceStaff(id, staffId);
        ra.addFlashAttribute("successMessage", "Complaint assigned to maintenance staff.");
        return "redirect:/warden/complaints";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ComplaintStatus status,
                               @RequestParam(required = false) String note,
                               RedirectAttributes ra) {
        if (status == ComplaintStatus.REOPENED) {
            throw new IllegalStateException("Only a student can reopen a complaint.");
        }
        complaintSystemFacade.updateRepairStatus(id, status, note);
        ra.addFlashAttribute("successMessage", "Complaint status updated and student notified.");
        return "redirect:/warden/complaints";
    }
}
