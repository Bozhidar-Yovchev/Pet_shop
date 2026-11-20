package com.example.new_final_project.designpatterns.decorator;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Customer;
import com.example.new_final_project.model.Notification;
import com.example.new_final_project.model.Order;
import com.example.new_final_project.Repository.NotificationRepository;

// Adds a simulated email on top of whatever the wrapped notifier does
public class EmailNotifierDecorator extends NotifierDecorator {

    private final NotificationRepository notificationRepository;

    public EmailNotifierDecorator(Notifier delegate, NotificationRepository notificationRepository) {
        super(delegate);
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void send(Customer customer, Order order, String message) {
        super.send(customer, order, message);
        String text = "Email to " + customer.getEmail() + ": " + message;
        System.out.println("[SIMULATED EMAIL] " + text);
        notificationRepository.save(new Notification(text, AppConstants.Channel.EMAIL, customer, order));
    }
}