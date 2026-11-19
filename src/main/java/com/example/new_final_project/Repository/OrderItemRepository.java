package com.example.new_final_project.Repository;

import com.example.new_final_project.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;



public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}