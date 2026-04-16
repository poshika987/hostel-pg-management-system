package com.hostel.management_system.controller;

import com.hostel.management_system.model.Invoice;
import com.hostel.management_system.model.Student;
import com.hostel.management_system.model.User;
import com.hostel.management_system.service.StudentPaymentOperations;
import com.hostel.management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC – Controller for Rent and Payments module.
 * SOLID – SRP: HTTP routing only; delegates to PaymentService.
 * SOLID – ISP: Student-facing endpoints separated from accountant endpoints.
 */
@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired private StudentPaymentOperations paymentService;
    @Autowired private UserService userService;

    // ── Helper ───────────────────────────────────────────────────────────────
    private Student getStudent(Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        if (!(user instanceof Student s)) throw new RuntimeException("Not a student account.");
        return s;
    }

    // ── Generate invoice for logged-in student ───────────────────────────────
    @PostMapping("/generate-invoice")
    public String generateInvoice(Authentication auth, RedirectAttributes ra) {
        Student student = getStudent(auth);
        paymentService.generateInvoice(student.getId());
        ra.addFlashAttribute("successMessage", "Invoice generated successfully.");
        return "redirect:/payments/invoice";
    }

    // ── Show invoice page ────────────────────────────────────────────────────
    @GetMapping("/invoice")
    public String showInvoice(Authentication auth, Model model) {
        Student student = getStudent(auth);
        model.addAttribute("student", student);
        model.addAttribute("invoices", paymentService.getInvoices(student.getId()));
        return "invoice";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam Long invoiceId,
                           Authentication auth,
                           Model model,
                           RedirectAttributes ra) {
        Student student = getStudent(auth);
        try {
            Invoice invoice = paymentService.getInvoiceForStudent(student.getId(), invoiceId);
            model.addAttribute("student", student);
            model.addAttribute("invoice", invoice);
            return "payment_checkout";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/payments/invoice";
        }
    }

    // ── Process payment ──────────────────────────────────────────────────────
    @PostMapping("/pay")
    public String pay(@RequestParam Long invoiceId,
                      @RequestParam String mode,
                      @RequestParam String paymentChannel,
                      @RequestParam String payerName,
                      @RequestParam(required = false) String upiId,
                      @RequestParam(required = false) String cardNumber,
                      @RequestParam(required = false) String expiry,
                      @RequestParam(required = false) String cvv,
                      Authentication auth,
                      RedirectAttributes ra) {
        Student student = getStudent(auth);
        try {
            var payment = paymentService.makePayment(
                    student.getId(),
                    invoiceId,
                    mode,
                    paymentChannel,
                    payerName,
                    upiId,
                    cardNumber,
                    expiry,
                    cvv
            );
            ra.addFlashAttribute("successMessage",
                    "Payment successful via " + payment.getPaymentChannel()
                            + ". Ref: " + payment.getReferenceId());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Payment failed: " + e.getMessage());
            ra.addFlashAttribute("checkoutInvoiceId", invoiceId);
            return "redirect:/payments/checkout?invoiceId=" + invoiceId;
        }
        return "redirect:/payments/history";
    }

    // ── View payment history (MINOR USE CASE) ────────────────────────────────
    @GetMapping("/history")
    public String history(Authentication auth, Model model) {
        Student student = getStudent(auth);
        model.addAttribute("student", student);
        model.addAttribute("payments", paymentService.getPaymentHistory(student.getId()));
        return "payment_history";
    }

    @PostMapping("/refund/{paymentId}")
    public String requestRefund(@PathVariable Long paymentId,
                                Authentication auth,
                                RedirectAttributes ra) {
        Student student = getStudent(auth);
        try {
            paymentService.requestRefund(student.getId(), paymentId);
            ra.addFlashAttribute("successMessage", "Refund request submitted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/payments/history";
    }
}
