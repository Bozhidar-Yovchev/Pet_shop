package com.example.new_final_project.designpatterns.factory;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Order;
import com.example.new_final_project.model.Payment;

public class CashOnDeliveryHandler implements PaymentHandler {

    @Override
    public Payment pay(Order order, PaymentDetails details) {
        return new Payment(AppConstants.PaymentMethod.CASH_ON_DELIVERY,
                AppConstants.PaymentStatus.PENDING, order.getTotalAmount(), order);
    }
}