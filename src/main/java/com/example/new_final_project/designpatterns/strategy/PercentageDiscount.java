package com.example.new_final_project.designpatterns.strategy;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Discount;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PercentageDiscount implements DiscountStrategy {

    @Override
    public String type() {
        return AppConstants.DiscountType.PERCENTAGE;
    }

    @Override
    public BigDecimal discountOn(BigDecimal amount, Discount discount) {
        return amount.multiply(discount.getValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}