package com.example.new_final_project.dto;

import java.math.BigDecimal;

public record OrderPricing(BigDecimal subtotal, BigDecimal discountAmount,
                           BigDecimal shippingCost, BigDecimal total) {
}