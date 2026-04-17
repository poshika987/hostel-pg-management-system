package com.hostel.management_system.controller;

import com.hostel.management_system.service.AccountantPaymentOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/accountant/payments")
public class AccountantPaymentController {

    private final AccountantPaymentOperations paymentOperations;

    public AccountantPaymentController(AccountantPaymentOperations paymentOperations) {
        this.paymentOperations = paymentOperations;
    }

    @GetMapping
    public String payments(Model model) {
        model.addAttribute("offlinePayments", paymentOperations.getPendingOfflinePayments());
        model.addAttribute("refundRequests", paymentOperations.getPendingRefundRequests());
        model.addAttribute("disputes", paymentOperations.getOpenDisputes());
        return "accountant-payments";
    }

    @PostMapping("/offline/{paymentId}/verify")
    public String verifyOfflinePayment(@PathVariable Long paymentId, RedirectAttributes ra) {
        try {
            paymentOperations.verifyOfflinePayment(paymentId);
            ra.addFlashAttribute("successMessage", "Offline payment verified successfully.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/accountant/payments";
    }

    @PostMapping("/refunds/{paymentId}/approve")
    public String approveRefund(@PathVariable Long paymentId, RedirectAttributes ra) {
        try {
            paymentOperations.approveRefund(paymentId);
            ra.addFlashAttribute("successMessage", "Refund approved successfully.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/accountant/payments";
    }

    @PostMapping("/disputes/{disputeId}/resolve")
    public String resolveDispute(@PathVariable Long disputeId,
                                 @RequestParam(required = false) String resolutionNote,
                                 RedirectAttributes ra) {
        try {
            paymentOperations.resolveDispute(disputeId, resolutionNote);
            ra.addFlashAttribute("successMessage", "Payment dispute resolved successfully.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/accountant/payments";
    }
}
