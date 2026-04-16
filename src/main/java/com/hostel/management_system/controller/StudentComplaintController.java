package com.hostel.management_system.controller;

import com.hostel.management_system.facade.ComplaintSystemFacade;
import com.hostel.management_system.model.*;
import com.hostel.management_system.service.NotificationService;
import com.hostel.management_system.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/student/complaints")
public class StudentComplaintController {

    private final ComplaintSystemFacade complaintSystemFacade;
    private final UserService userService;
    private final NotificationService notificationService;

    public StudentComplaintController(ComplaintSystemFacade complaintSystemFacade,
                                      UserService userService,
                                      NotificationService notificationService) {
        this.complaintSystemFacade = complaintSystemFacade;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String complaints(Authentication auth, Model model) {
        Student student = getStudent(auth);
        model.addAttribute("student", student);
        model.addAttribute("complaints", complaintSystemFacade.viewComplaintStatus(student));
        model.addAttribute("priorities", ComplaintPriority.values());
        model.addAttribute("notifications", notificationService.getNotifications(student));
        model.addAttribute("unreadNotifications", notificationService.countUnread(student));
        return "student-complaints";
    }

    @PostMapping
    public String raiseComplaint(@RequestParam String title,
                                 @RequestParam String category,
                                 @RequestParam String description,
                                 @RequestParam ComplaintPriority priority,
                                 @RequestParam(required = false) MultipartFile photo,
                                 Authentication auth,
                                 RedirectAttributes ra) {
        Student student = getStudent(auth);
        Complaint complaint = complaintSystemFacade.raiseComplaint(student, title, category, description, priority, photo);
        ra.addFlashAttribute("successMessage", "Complaint #" + complaint.getComplaintId() + " submitted successfully.");
        return "redirect:/student/complaints";
    }

    @PostMapping("/{id}/reopen")
    public String reopenComplaint(@PathVariable Long id,
                                  @RequestParam(required = false) String reason,
                                  Authentication auth,
                                  RedirectAttributes ra) {
        Student student = getStudent(auth);
        Complaint complaint = complaintSystemFacade.reopenComplaint(student, id, reason);
        ra.addFlashAttribute("successMessage", "Complaint #" + complaint.getComplaintId() + " reopened for review.");
        return "redirect:/student/complaints";
    }

    private Student getStudent(Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        if (!(user instanceof Student student)) {
            throw new RuntimeException("Not a student account.");
        }
        return student;
    }
}
