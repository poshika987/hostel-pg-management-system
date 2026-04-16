package com.hostel.management_system.service;

import com.hostel.management_system.model.Invoice;
import com.hostel.management_system.model.Payment;

import java.util.List;

/**
 * ISP: student-facing payment operations only.
 * Student consumers do not depend on administrative actions such as refunds.
 */
public interface StudentPaymentOperations {
    Invoice generateInvoice(Long studentId);
    Invoice getInvoiceForStudent(Long studentId, Long invoiceId);
    Payment makePayment(Long studentId,
                        Long invoiceId,
                        String mode,
                        String paymentChannel,
                        String payerName,
                        String upiId,
                        String cardNumber,
                        String expiry,
                        String cvv);
    List<Payment> getPaymentHistory(Long studentId);
    List<Invoice> getInvoices(Long studentId);
    Payment requestRefund(Long studentId, Long paymentId);
}
