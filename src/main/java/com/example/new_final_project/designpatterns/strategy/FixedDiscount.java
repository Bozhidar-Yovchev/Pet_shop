package com.example.new_final_project.designpatterns.strategy;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Discount;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedDiscount implements DiscountStrategy {

    @Override
    public String type() {
        return AppConstants.DiscountType.FIXED;
    }

    @Override
    public BigDecimal discountOn(BigDecimal amount, Discount discount) {
        return discount.getValue().min(amount);   // never more than the amount
    }
}