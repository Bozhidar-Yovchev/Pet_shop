package com.example.new_final_project.designpatterns.factory;

import com.example.new_final_project.config.AppConstants;
import org.springframework.stereotype.Component;

@Component
public class PaymentHandlerFactory {

    public PaymentHandler create(String method) {
        if (method == null) {
            throw new IllegalArgumentException("Choose a payment method");
        }
        return switch (method) {
            case AppConstants.PaymentMethod.CARD -> new CardPaymentHandler();
            case AppConstants.PaymentMethod.CASH_ON_DELIVERY -> new CashOnDeliveryHandler();
            default -> throw new IllegalArgumentException("Unknown payment method");
        };
    }
}