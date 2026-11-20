package com.example.new_final_project.designpatterns.strategy;

import com.example.new_final_project.model.Discount;

import java.math.BigDecimal;

public interface DiscountStrategy {

    String type();


    BigDecimal discountOn(BigDecimal amount, Discount discount);

    default boolean waivesShipping() {
        return false;
    }
}