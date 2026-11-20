package com.example.new_final_project.designpatterns.strategy;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DiscountStrategyRegistry {

    private final Map<String, DiscountStrategy> strategies = new HashMap<>();

    // Spring passes in every DiscountStrategy bean
    public DiscountStrategyRegistry(List<DiscountStrategy> all) {
        all.forEach(s -> strategies.put(s.type(), s));
    }

    public DiscountStrategy get(String type) {
        DiscountStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown discount type: " + type);
        }
        return strategy;
    }
}