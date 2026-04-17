package com.hostel.management_system.service;

import com.hostel.management_system.exception.ResourceNotFoundException;
import com.hostel.management_system.model.*;
import com.hostel.management_system.repository.InvoiceRepository;
import com.hostel.management_system.repository.PaymentDisputeRepository;
import com.hostel.management_system.repository.PaymentRepository;
import com.hostel.management_system.service.strategy.OfflinePaymentStrategy;
import com.hostel.management_system.service.strategy.OnlinePaymentStrategy;
import com.hostel.management_system.service.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SOLID – SRP: manages invoice generation, payment recording, history retrieval.
 * BEHAVIORAL PATTERN – Strategy: selects OnlinePaymentStrategy or OfflinePaymentStrategy at runtime.
 * GRASP – Protected Variations: PaymentGatewayAdapter shields this service from gateway API changes.
 */
@Service
public class PaymentService implements StudentPaymentOperations, AccountantPaymentOperations {

    @Autowired private InvoiceRepository invoiceRepo;
    @Autowired private PaymentRepository paymentRepo;
    @Autowired private PaymentDisputeRepository disputeRepo;
    @Autowired private RentService rentService;
    @Autowired private OnlinePaymentStrategy onlineStrategy;
    @Autowired private OfflinePaymentStrategy offlineStrategy;

    // ── Generate invoice ─────────────────────────────────────────────────────
    @Transactional
    public Invoice generateInvoice(Long studentId) {
        double rent = rentService.calculateRent(studentId);

        Invoice invoice = new Invoice();
        invoice.setStudentId(studentId);
        invoice.setPolicyId(1);
        invoice.setAmount(rent);
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setStatus(InvoiceStatus.PENDING);

        return invoiceRepo.save(invoice);
    }

    public Invoice getInvoiceForStudent(Long studentId, Long invoiceId) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));

        if (!studentId.equals(invoice.getStudentId())) {
            throw new ResourceNotFoundException("Invoice does not belong to the logged-in student.");
        }

        return invoice;
    }

    // ── Make payment using Strategy pattern ──────────────────────────────────
    @Transactional
    public Payment makePayment(Long studentId,
                               Long invoiceId,
                               String mode,
                               String paymentChannel,
                               String payerName,
                               String upiId,
                               String cardNumber,
                               String expiry,
                               String cvv) {
        double currentRent = rentService.calculateRent(studentId);
        Invoice invoice = getInvoiceForStudent(studentId, invoiceId);

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("This invoice is already paid.");
        }
        if (invoice.getAmount() != currentRent) {
            invoice.setAmount(currentRent);
        }

        double amount = syncInvoiceAmount(invoice, currentRent);

        // BEHAVIORAL PATTERN – Strategy: select at runtime
        validatePaymentInput(mode, paymentChannel, payerName, upiId, cardNumber, expiry, cvv);
        boolean offline = "OFFLINE".equalsIgnoreCase(mode);
        PaymentStrategy strategy = offline ? offlineStrategy : onlineStrategy;
        boolean success = strategy.pay(amount);

        // Record payment
        Payment payment = new Payment();
        payment.setStudentId(studentId);
        payment.setInvoiceId(invoice.getInvoiceId());
        payment.setAmount(amount);
        payment.setPaymentMode(mode.toUpperCase());
        payment.setPaymentChannel(paymentChannel.toUpperCase());
        payment.setReferenceId(PaymentProcessorSingleton.getInstance().nextReference(mode, paymentChannel));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(success ? (offline ? PaymentStatus.PENDING_VERIFICATION : PaymentStatus.SUCCESSFUL) : PaymentStatus.FAILED);
        paymentRepo.save(payment);

        // Online payments settle immediately. Offline cash needs accountant verification.
        if (success && !offline) {
            invoice.setStatus(InvoiceStatus.PAID);
        }
        invoiceRepo.save(invoice);

        if (!success) throw new RuntimeException("Payment failed. Please try again.");

        return payment;
    }

    // ── View payment history (minor use case) ────────────────────────────────
    public List<Payment> getPaymentHistory(Long studentId) {
        return paymentRepo.findAllByStudentIdOrderByPaymentDateDesc(studentId);
    }

    // ── Get all invoices for a student ───────────────────────────────────────
    public List<Invoice> getInvoices(Long studentId) {
        List<Invoice> invoices = invoiceRepo.findByStudentId(studentId);
        var currentRent = rentService.getAllocatedRoomRent(studentId);

        if (currentRent.isEmpty()) {
            return invoices;
        }

        invoices.stream()
                .filter(invoice -> invoice.getStatus() != InvoiceStatus.PAID)
                .forEach(invoice -> syncInvoiceAmount(invoice, currentRent.get()));

        return invoiceRepo.saveAll(invoices);
    }

    @Override
    @Transactional
    public Payment requestRefund(Long studentId, Long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        if (!studentId.equals(payment.getStudentId())) {
            throw new RuntimeException("You can only request refunds for your own payments.");
        }
        if (payment.getStatus() != PaymentStatus.SUCCESSFUL) {
            throw new RuntimeException("Only successful payments can be marked for refund.");
        }

        payment.setStatus(PaymentStatus.REFUND_REQUESTED);
        return paymentRepo.save(payment);
    }

    @Override
    @Transactional
    public PaymentDispute raiseDispute(Long studentId, Long paymentId, String reason) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        if (!studentId.equals(payment.getStudentId())) {
            throw new RuntimeException("You can dispute only your own payments.");
        }
        if (reason == null || reason.isBlank()) {
            throw new RuntimeException("Dispute reason is required.");
        }
        Student student = new Student();
        student.setId(studentId);
        PaymentDispute dispute = new PaymentDispute();
        dispute.setPayment(payment);
        dispute.setStudent(student);
        dispute.setReason(reason.trim());
        return disputeRepo.save(dispute);
    }

    @Override
    public byte[] generateReceipt(Long studentId, Long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        if (!studentId.equals(payment.getStudentId())) {
            throw new RuntimeException("You can download receipts only for your own payments.");
        }
        if (payment.getStatus() != PaymentStatus.SUCCESSFUL) {
            throw new RuntimeException("Receipt is available only for successful payments.");
        }
        String receipt = """
                HOSTEL/PG MANAGEMENT SYSTEM
                Payment Receipt

                Payment ID: PMT-%s
                Invoice ID: INV-%s
                Student ID: %s
                Amount: %.2f
                Mode: %s / %s
                Reference: %s
                Paid At: %s
                Status: %s
                """.formatted(payment.getPaymentId(), payment.getInvoiceId(), payment.getStudentId(),
                payment.getAmount(), payment.getPaymentMode(), payment.getPaymentChannel(),
                payment.getReferenceId(), payment.getPaymentDate(), payment.getStatus());
        return receipt.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    @Transactional
    public Payment approveRefund(Long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.REFUND_REQUESTED) {
            throw new RuntimeException("Only refund-requested payments can be approved.");
        }

        payment.setStatus(PaymentStatus.REFUNDED);

        invoiceRepo.findById(payment.getInvoiceId()).ifPresent(invoice -> {
            invoice.setStatus(InvoiceStatus.PENDING);
            invoiceRepo.save(invoice);
        });

        return paymentRepo.save(payment);
    }

    @Override
    @Transactional
    public Payment verifyOfflinePayment(Long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        if (!"OFFLINE".equalsIgnoreCase(payment.getPaymentMode())) {
            throw new RuntimeException("Only offline payments need verification.");
        }
        if (payment.getStatus() != PaymentStatus.PENDING_VERIFICATION) {
            throw new RuntimeException("Only pending offline payments can be verified.");
        }

        payment.setStatus(PaymentStatus.SUCCESSFUL);
        invoiceRepo.findById(payment.getInvoiceId()).ifPresent(invoice -> {
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepo.save(invoice);
        });
        return paymentRepo.save(payment);
    }

    @Override
    public List<Payment> getPendingRefundRequests() {
        return paymentRepo.findAllByStatusOrderByPaymentDateDesc(PaymentStatus.REFUND_REQUESTED);
    }

    @Override
    public List<Payment> getPendingOfflinePayments() {
        return paymentRepo.findAllByStatusAndPaymentModeOrderByPaymentDateDesc(PaymentStatus.PENDING_VERIFICATION, "OFFLINE");
    }

    @Override
    public List<PaymentDispute> getOpenDisputes() {
        return disputeRepo.findByStatusOrderByCreatedAtDesc(PaymentDisputeStatus.OPEN);
    }

    @Override
    @Transactional
    public PaymentDispute resolveDispute(Long disputeId, String resolutionNote) {
        PaymentDispute dispute = disputeRepo.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment dispute not found: " + disputeId));
        dispute.setStatus(PaymentDisputeStatus.RESOLVED);
        dispute.setResolutionNote(resolutionNote == null ? "" : resolutionNote.trim());
        dispute.setResolvedAt(LocalDateTime.now());
        return disputeRepo.save(dispute);
    }

    private void validatePaymentInput(String mode,
                                      String paymentChannel,
                                      String payerName,
                                      String upiId,
                                      String cardNumber,
                                      String expiry,
                                      String cvv) {
        if (payerName == null || payerName.isBlank()) {
            throw new RuntimeException("Payer name is required.");
        }

        if ("ONLINE".equalsIgnoreCase(mode)) {
            if ("UPI".equalsIgnoreCase(paymentChannel)) {
                if (upiId == null || !upiId.matches("^[A-Za-z0-9._-]{2,}@[A-Za-z]{2,}$")) {
                    throw new RuntimeException("Enter a valid UPI ID such as name@bank.");
                }
                return;
            }

            if ("CARD".equalsIgnoreCase(paymentChannel)) {
                String normalizedCard = cardNumber == null ? "" : cardNumber.replaceAll("\\s+", "");
                if (!normalizedCard.matches("\\d{16}")) {
                    throw new RuntimeException("Card number must be 16 digits.");
                }
                if (expiry == null || !expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                    throw new RuntimeException("Expiry must be in MM/YY format.");
                }
                if (cvv == null || !cvv.matches("\\d{3}")) {
                    throw new RuntimeException("CVV must be 3 digits.");
                }
                return;
            }

            throw new RuntimeException("Choose a valid online payment method.");
        }

        if ("OFFLINE".equalsIgnoreCase(mode)) {
            if (!"CASH".equalsIgnoreCase(paymentChannel)) {
                throw new RuntimeException("Offline payment currently supports cash only.");
            }
            return;
        }

        throw new RuntimeException("Unsupported payment mode.");
    }

    private double syncInvoiceAmount(Invoice invoice, double baseRent) {
        double amount = baseRent + rentService.calculateLateFee(invoice.getDueDate());
        invoice.setAmount(amount);

        if (invoice.getStatus() != InvoiceStatus.PAID) {
            invoice.setStatus(amount > baseRent ? InvoiceStatus.OVERDUE : InvoiceStatus.PENDING);
        }

        return amount;
    }
}
