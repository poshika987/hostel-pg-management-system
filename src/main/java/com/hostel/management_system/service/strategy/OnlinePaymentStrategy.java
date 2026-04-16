package com.hostel.management_system.service.strategy;

import com.hostel.management_system.adapter.PaymentGatewayAdapter;
import org.springframework.stereotype.Component;

/**
 * BEHAVIORAL PATTERN – Strategy (Concrete)
 * Routes payment through the external gateway adapter.
 */
@Component
public class OnlinePaymentStrategy implements PaymentStrategy {

    private final PaymentGatewayAdapter adapter;

    public OnlinePaymentStrategy(PaymentGatewayAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean pay(double amount) {
        return adapter.processPayment(amount);
    }
}
