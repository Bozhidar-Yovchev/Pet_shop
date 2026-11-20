package com.example.new_final_project.dto;

import com.example.new_final_project.model.Product;

import java.math.BigDecimal;
import java.util.List;

public record CartView(List<CartLine> lines, BigDecimal subtotal, boolean checkoutAllowed) {

    public record CartLine(Long itemId,
                           Product product,
                           int quantity,
                           BigDecimal unitPrice,
                           BigDecimal lineTotal,
                           int stock,
                           String problem) {   // null when the line is fine
    }
}