package com.example.new_final_project.Repository;

import com.example.new_final_project.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}