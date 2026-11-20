package com.example.new_final_project.Repository;

import com.example.new_final_project.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);
    List<Order> findByStatus(String status);
    List<Order> findByStatusOrderByOrderDateDesc(String status);
}