package com.hostel.management_system.service;

import com.hostel.management_system.model.Payment;
import com.hostel.management_system.model.PaymentDispute;

import java.util.List;

/**
 * ISP: administrative payment actions are separated from student operations.
 */
public interface AccountantPaymentOperations {
    Payment approveRefund(Long paymentId);
    Payment verifyOfflinePayment(Long paymentId);
    List<Payment> getPendingOfflinePayments();
    List<Payment> getPendingRefundRequests();
    List<PaymentDispute> getOpenDisputes();
    PaymentDispute resolveDispute(Long disputeId, String resolutionNote);
}
