package com.hostel.management_system.adapter;

import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayAdapter {

    public boolean processPayment(double amount) {
        System.out.println("Processing online payment: " + amount);
        return true; // simulate success
    }
}