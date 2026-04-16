package com.hostel.management_system.controller;

import com.hostel.management_system.model.MaintenanceStaff;
import com.hostel.management_system.service.MaintenanceStaffService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/staff")
public class AdminStaffController {

    private final MaintenanceStaffService staffService;

    public AdminStaffController(MaintenanceStaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String staff(Model model) {
        if (!model.containsAttribute("staffForm")) {
            model.addAttribute("staffForm", new MaintenanceStaff());
        }
        addStaffModel(model);
        return "admin-staff";
    }

    @PostMapping
    public String addStaff(@Valid @ModelAttribute("staffForm") MaintenanceStaff staff,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            addStaffModel(model);
            return "admin-staff";
        }
        staffService.save(staff);
        ra.addFlashAttribute("successMessage", "Staff record added successfully.");
        return "redirect:/admin/staff";
    }

    @PostMapping("/{staffId}")
    public String updateStaff(@PathVariable Long staffId,
                              @Valid @ModelAttribute("staffForm") MaintenanceStaff staff,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes ra) {
        staff.setStaffId(staffId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("editingStaffId", staffId);
            addStaffModel(model);
            return "admin-staff";
        }
        staffService.save(staff);
        ra.addFlashAttribute("successMessage", "Staff record updated successfully.");
        return "redirect:/admin/staff";
    }

    @PostMapping("/{staffId}/delete")
    public String deleteStaff(@PathVariable Long staffId, RedirectAttributes ra) {
        try {
            staffService.deleteStaff(staffId);
            ra.addFlashAttribute("successMessage", "Staff record deleted successfully.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/staff";
    }

    private void addStaffModel(Model model) {
        var staffMembers = staffService.getAllStaff();
        Map<Long, Long> activeTasks = staffMembers.stream()
                .collect(Collectors.toMap(MaintenanceStaff::getStaffId, staffService::countActiveTasks));
        model.addAttribute("staffMembers", staffMembers);
        model.addAttribute("activeTasks", activeTasks);
    }
}
