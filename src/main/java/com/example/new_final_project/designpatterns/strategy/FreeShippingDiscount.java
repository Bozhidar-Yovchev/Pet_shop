package com.example.new_final_project.designpatterns.strategy;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Discount;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FreeShippingDiscount implements DiscountStrategy {

    @Override
    public String type() {
        return AppConstants.DiscountType.FREE_SHIPPING;
    }

    @Override
    public BigDecimal discountOn(BigDecimal amount, Discount discount) {
        return BigDecimal.ZERO;   // no money off the goods, shipping is waived instead
    }

    @Override
    public boolean waivesShipping() {
        return true;
    }
}