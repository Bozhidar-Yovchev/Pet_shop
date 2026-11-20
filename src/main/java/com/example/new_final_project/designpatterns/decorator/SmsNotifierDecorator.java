package com.example.new_final_project.designpatterns.decorator;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Customer;
import com.example.new_final_project.model.Notification;
import com.example.new_final_project.model.Order;
import com.example.new_final_project.Repository.NotificationRepository;

// Adds a simulated SMS (Customer has no phone number, so the name is used)
public class SmsNotifierDecorator extends NotifierDecorator {

    private final NotificationRepository notificationRepository;

    public SmsNotifierDecorator(Notifier delegate, NotificationRepository notificationRepository) {
        super(delegate);
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void send(Customer customer, Order order, String message) {
        super.send(customer, order, message);
        String text = "SMS to " + customer.getFullName() + ": " + message;
        System.out.println("[SIMULATED SMS] " + text);
        notificationRepository.save(new Notification(text, AppConstants.Channel.SMS, customer, order));
    }
}