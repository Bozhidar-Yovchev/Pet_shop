package com.example.new_final_project.designpatterns.factory;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Order;
import com.example.new_final_project.model.Payment;

// Simulated card payment: no real bank, nothing is stored
public class CardPaymentHandler implements PaymentHandler {

    @Override
    public Payment pay(Order order, PaymentDetails details) {
        String number = details.cardNumber() == null ? "" : details.cardNumber().replaceAll("\\s", "");
        if (!number.matches("\\d{16}")) {
            throw new IllegalArgumentException("Card number must have 16 digits");
        }
        if (number.endsWith("0000")) {   // lets us test a failed payment
            throw new IllegalStateException("Payment declined by the bank (simulated)");
        }
        return new Payment(AppConstants.PaymentMethod.CARD,
                AppConstants.PaymentStatus.PAID, order.getTotalAmount(), order);
    }
}