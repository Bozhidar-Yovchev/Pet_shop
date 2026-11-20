package com.example.new_final_project.service;

import com.example.new_final_project.model.Order;
import com.example.new_final_project.Repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Other customers' orders are reported as "not found"
    @Transactional(readOnly = true)
    public Order getOwned(Long customerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new NotFoundException("Order not found");
        }
        return order;
    }
}