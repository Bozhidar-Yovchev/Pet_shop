package com.example.new_final_project.designpatterns.decorator;

import com.example.new_final_project.model.Customer;
import com.example.new_final_project.model.Order;

public interface Notifier {
    void send(Customer customer, Order order, String message);
}