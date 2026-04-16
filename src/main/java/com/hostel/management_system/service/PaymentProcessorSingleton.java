package com.hostel.management_system.service;

import java.util.UUID;

/**
 * Creational Pattern – Singleton:
 * one shared payment-reference generator for financial records.
 */
public final class PaymentProcessorSingleton {

    private static final PaymentProcessorSingleton INSTANCE = new PaymentProcessorSingleton();

    private PaymentProcessorSingleton() {}

    public static PaymentProcessorSingleton getInstance() {
        return INSTANCE;
    }

    public String nextReference(String mode, String paymentChannel) {
        String prefix = ("ONLINE".equalsIgnoreCase(mode) ? "TXN" : "RCPT")
                + "-" + paymentChannel.toUpperCase();
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
