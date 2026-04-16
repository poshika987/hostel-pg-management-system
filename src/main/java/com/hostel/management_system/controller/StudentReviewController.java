package com.hostel.management_system.controller;

import com.hostel.management_system.model.Student;
import com.hostel.management_system.model.User;
import com.hostel.management_system.service.ReviewService;
import com.hostel.management_system.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student/reviews")
public class StudentReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public StudentReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping
    public String reviews(Authentication auth, Model model) {
        Student student = getStudent(auth);
        model.addAttribute("student", student);
        model.addAttribute("reviews", reviewService.getReviewsFor(student));
        model.addAttribute("categories", List.of("Facilities", "Food", "Maintenance", "Cleanliness", "Staff", "Overall"));
        return "student-reviews";
    }

    @PostMapping
    public String submitReview(@RequestParam int rating,
                               @RequestParam String category,
                               @RequestParam(required = false) String comments,
                               Authentication auth,
                               RedirectAttributes ra) {
        try {
            reviewService.submitReview(getStudent(auth), rating, category, comments);
            ra.addFlashAttribute("successMessage", "Review submitted successfully.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/reviews";
    }

    private Student getStudent(Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        if (!(user instanceof Student student)) {
            throw new RuntimeException("Not a student account.");
        }
        return student;
    }
}
