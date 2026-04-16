package com.hostel.management_system.service;

import com.hostel.management_system.model.Payment;

import java.util.List;

/**
 * ISP: administrative payment actions are separated from student operations.
 */
public interface AccountantPaymentOperations {
    Payment approveRefund(Long paymentId);
    List<Payment> getPendingRefundRequests();
}
