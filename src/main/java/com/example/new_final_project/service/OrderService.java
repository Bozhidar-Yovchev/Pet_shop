package com.example.new_final_project.service;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.designpatterns.observer.OrderStatusPublisher;
import com.example.new_final_project.model.Order;
import com.example.new_final_project.model.Payment;
import com.example.new_final_project.Repository.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private static final String PENDING = AppConstants.OrderStatus.PENDING;
    private static final String CONFIRMED = AppConstants.OrderStatus.CONFIRMED;
    private static final String SHIPPED = AppConstants.OrderStatus.SHIPPED;
    private static final String DELIVERED = AppConstants.OrderStatus.DELIVERED;
    private static final String CANCELLED = AppConstants.OrderStatus.CANCELLED;

    // Which status may follow which
    private static final Map<String, List<String>> TRANSITIONS = Map.of(
            PENDING, List.of(CONFIRMED, CANCELLED),
            CONFIRMED, List.of(SHIPPED, CANCELLED),
            SHIPPED, List.of(DELIVERED),
            DELIVERED, List.of(),
            CANCELLED, List.of());

    private final OrderRepository orderRepository;
    private final OrderStatusPublisher statusPublisher;

    public OrderService(OrderRepository orderRepository, OrderStatusPublisher statusPublisher) {
        this.orderRepository = orderRepository;
        this.statusPublisher = statusPublisher;
    }

    // ---------- Reading ----------

    @Transactional(readOnly = true)
    public List<Order> findByCustomer(Long customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
    }

    @Transactional(readOnly = true)
    public List<Order> findAll(String status) {
        if (status == null || status.isBlank()) {
            return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
        }
        return orderRepository.findByStatusOrderByOrderDateDesc(status);
    }

    @Transactional(readOnly = true)
    public Order getById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    // Other customers' orders are reported as "not found"
    @Transactional(readOnly = true)
    public Order getOwned(Long customerId, Long orderId) {
        Order order = getById(orderId);
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new NotFoundException("Order not found");
        }
        return order;
    }

    public List<String> nextStatuses(String current) {
        return TRANSITIONS.getOrDefault(current, List.of());
    }

    public boolean canCancel(Order order) {
        return nextStatuses(order.getStatus()).contains(CANCELLED);
    }

    // ---------- Changing the status ----------

    @Transactional
    public void cancel(Long customerId, Long orderId) {
        Order order = getOwned(customerId, orderId);
        if (!canCancel(order)) {
            throw new IllegalStateException("This order can no longer be cancelled");
        }
        applyStatus(order, CANCELLED);
    }

    @Transactional
    public void changeStatus(Long orderId, String newStatus) {
        applyStatus(getById(orderId), newStatus);
    }

    private void applyStatus(Order order, String newStatus) {
        String oldStatus = order.getStatus();
        if (!nextStatuses(oldStatus).contains(newStatus)) {
            throw new IllegalStateException("Cannot change status from " + oldStatus + " to " + newStatus);
        }
        order.setStatus(newStatus);

        // Keep the payment in step with the order
        Payment payment = order.getPayment();
        if (payment != null) {
            if (DELIVERED.equals(newStatus)
                    && AppConstants.PaymentStatus.PENDING.equals(payment.getStatus())) {
                payment.setStatus(AppConstants.PaymentStatus.PAID);          // cash collected
            } else if (CANCELLED.equals(newStatus)
                    && AppConstants.PaymentStatus.PAID.equals(payment.getStatus())) {
                payment.setStatus(AppConstants.PaymentStatus.REFUNDED);
            }
        }

        // Observer pattern: stock restore and notifications happen here
        statusPublisher.publish(order, oldStatus, newStatus);
    }
}