package com.example.new_final_project.designpatterns.observer;

import com.example.new_final_project.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;

// The subject: tells every registered listener when an order's status changes
@Component
public class OrderStatusPublisher {

    private final List<OrderStatusListener> listeners;

    // Spring passes in every OrderStatusListener bean
    public OrderStatusPublisher(List<OrderStatusListener> listeners) {
        this.listeners = listeners;
    }

    public void publish(Order order, String oldStatus, String newStatus) {
        for (OrderStatusListener listener : listeners) {
            listener.onStatusChanged(order, oldStatus, newStatus);
        }
    }
}