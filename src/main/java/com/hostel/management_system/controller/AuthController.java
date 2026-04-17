package com.hostel.management_system.controller;

import com.hostel.management_system.model.Student;
import com.hostel.management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired private UserService userService;

    @GetMapping("/")
    public String root() { return "redirect:/login"; }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Student student, RedirectAttributes ra) {
        try {
            userService.registerStudent(student);
            ra.addFlashAttribute("successMessage",
                    "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String login(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/redirect-dashboard";
        }
        return "login";
    }

    @GetMapping("/redirect-dashboard")
    public String redirectDashboard(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        String role = auth.getAuthorities().iterator().next().getAuthority();
        return switch (role) {
            case "ROLE_STUDENT"    -> "redirect:/student/dashboard";
            case "ROLE_ADMIN"      -> "redirect:/admin/dashboard";
            case "ROLE_WARDEN"     -> "redirect:/warden/dashboard";
            case "ROLE_ACCOUNTANT" -> "redirect:/accountant/dashboard";
            case "ROLE_MAINTENANCE" -> "redirect:/maintenance/dashboard";
            default -> "redirect:/login";
        };
    }
}
