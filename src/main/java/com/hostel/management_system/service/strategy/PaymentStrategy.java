package com.hostel.management_system.service.strategy;

/**
 * BEHAVIORAL PATTERN – Strategy
 * Allows switching between OnlinePaymentStrategy and OfflinePaymentStrategy
 * at runtime without changing PaymentService logic.
 */
public interface PaymentStrategy {
    boolean pay(double amount);
}
