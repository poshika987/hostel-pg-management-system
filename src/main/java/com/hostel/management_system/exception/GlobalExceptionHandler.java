package com.hostel.management_system.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * SOLID – Single Responsibility (SRP):
 * Centralises all exception-to-user-message mapping so that
 * controllers stay clean and free of try-catch boilerplate.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateBookingException.class)
    public String handleDuplicate(DuplicateBookingException ex,
                                  RedirectAttributes ra,
                                  HttpServletRequest request) {
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:" + referer(request, "/student/rooms");
    }

    @ExceptionHandler(InvalidBookingStateException.class)
    public String handleInvalidState(InvalidBookingStateException ex,
                                     RedirectAttributes ra,
                                     HttpServletRequest request) {
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:" + referer(request, "/admin/bookings");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex,
                                 RedirectAttributes ra,
                                 HttpServletRequest request) {
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:" + referer(request, "/");
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleGeneric(RuntimeException ex,
                                RedirectAttributes ra,
                                HttpServletRequest request) {
        ra.addFlashAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
        return "redirect:" + referer(request, "/");
    }

    private String referer(HttpServletRequest request, String fallback) {
        String ref = request.getHeader("Referer");
        return (ref != null && !ref.isBlank()) ? ref : fallback;
    }
}
