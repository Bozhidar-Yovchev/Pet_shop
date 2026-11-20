package com.example.new_final_project.designpatterns.decorator;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Customer;
import com.example.new_final_project.model.Notification;
import com.example.new_final_project.model.Order;
import com.example.new_final_project.Repository.NotificationRepository;

public class InAppNotifier implements Notifier {

    private final NotificationRepository notificationRepository;

    public InAppNotifier(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void send(Customer customer, Order order, String message) {
        notificationRepository.save(
                new Notification(message, AppConstants.Channel.IN_APP, customer, order));
    }
}