package com.example.new_final_project.service;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Notification;
import com.example.new_final_project.Repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> findForCustomer(Long customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    // The number shown in the menu counts the in-app messages only
    @Transactional(readOnly = true)
    public long unreadCount(Long customerId) {
        return notificationRepository.countByCustomerIdAndChannelAndSeenFalse(
                customerId, AppConstants.Channel.IN_APP);
    }

    @Transactional
    public void markAllSeen(Long customerId) {
        notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .forEach(n -> n.setSeen(true));
    }
}