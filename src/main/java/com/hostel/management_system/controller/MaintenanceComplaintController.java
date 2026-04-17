package com.hostel.management_system.controller;

import com.hostel.management_system.facade.ComplaintSystemFacade;
import com.hostel.management_system.model.*;
import com.hostel.management_system.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceComplaintController {

    private final ComplaintSystemFacade complaintSystemFacade;
    private final UserService userService;

    public MaintenanceComplaintController(ComplaintSystemFacade complaintSystemFacade, UserService userService) {
        this.complaintSystemFacade = complaintSystemFacade;
        this.userService = userService;
    }

    @GetMapping({"/dashboard", "/complaints"})
    public String complaints(Authentication auth, Model model) {
        MaintenanceStaff staff = getMaintenanceStaff(auth);
        List<Complaint> complaints = complaintSystemFacade.viewAssignedComplaints(staff);
        model.addAttribute("staff", staff);
        model.addAttribute("complaints", complaints);
        model.addAttribute("openComplaintCount", complaints.stream().filter(c -> c.getStatus() != ComplaintStatus.CLOSED).count());
        model.addAttribute("statuses", List.of(ComplaintStatus.IN_PROGRESS, ComplaintStatus.CLOSED));
        return "maintenance-complaints";
    }

    @PostMapping("/complaints/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ComplaintStatus status,
                               @RequestParam(required = false) String note,
                               Authentication auth,
                               RedirectAttributes ra) {
        try {
            complaintSystemFacade.updateRepairStatusByMaintenanceStaff(id, getMaintenanceStaff(auth), status, note);
            ra.addFlashAttribute("successMessage", "Complaint status updated and student notified.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/maintenance/complaints";
    }

    private MaintenanceStaff getMaintenanceStaff(Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        if (!(user instanceof MaintenanceUser maintenanceUser)) {
            throw new RuntimeException("Not a maintenance staff account.");
        }
        return maintenanceUser.getStaff();
    }
}
