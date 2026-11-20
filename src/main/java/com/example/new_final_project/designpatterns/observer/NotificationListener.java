package com.example.new_final_project.designpatterns.observer;

import com.example.new_final_project.designpatterns.decorator.Notifier;
import com.example.new_final_project.model.Order;
import org.springframework.stereotype.Component;

// Observer 2: tells the customer about every status change
@Component
public class NotificationListener implements OrderStatusListener {

    private final Notifier notifier;

    public NotificationListener(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void onStatusChanged(Order order, String oldStatus, String newStatus) {
        String message = "Order #" + order.getId() + " changed from " + oldStatus + " to " + newStatus;
        notifier.send(order.getCustomer(), order, message);
    }
}