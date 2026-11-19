package com.example.new_final_project.Repository;

import com.example.new_final_project.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    long countByCustomerIdAndSeenFalse(Long customerId);
}
