package com.example.new_final_project.designpatterns.factory;

import com.example.new_final_project.model.Order;
import com.example.new_final_project.model.Payment;

public interface PaymentHandler {
    Payment pay(Order order, PaymentDetails details);
}