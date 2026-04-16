package com.hostel.management_system.service.strategy;

import org.springframework.stereotype.Component;

/**
 * BEHAVIORAL PATTERN – Strategy (Concrete)
 * Represents cash/offline payment verified by accountant.
 */
@Component
public class OfflinePaymentStrategy implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        // Accountant has physically verified; always succeeds
        return true;
    }
}
