package com.example.new_final_project.designpatterns.observer;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Order;
import com.example.new_final_project.model.OrderItem;
import com.example.new_final_project.Repository.InventoryRepository;
import org.springframework.stereotype.Component;

// Observer 1: a cancelled order gives its stock back
@Component
public class StockRestoreListener implements OrderStatusListener {

    private final InventoryRepository inventoryRepository;

    public StockRestoreListener(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void onStatusChanged(Order order, String oldStatus, String newStatus) {
        if (!AppConstants.OrderStatus.CANCELLED.equals(newStatus)) {
            return;
        }
        for (OrderItem item : order.getItems()) {
            inventoryRepository.increase(item.getProduct().getInventory().getId(), item.getQuantity());
        }
    }
}