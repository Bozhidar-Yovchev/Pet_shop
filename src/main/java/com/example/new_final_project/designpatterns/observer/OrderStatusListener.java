package com.example.new_final_project.designpatterns.observer;

import com.example.new_final_project.model.Order;

public interface OrderStatusListener {
    void onStatusChanged(Order order, String oldStatus, String newStatus);
}