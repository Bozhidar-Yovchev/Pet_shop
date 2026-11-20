package com.example.new_final_project.service;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.designpatterns.strategy.DiscountStrategy;
import com.example.new_final_project.designpatterns.strategy.DiscountStrategyRegistry;
import com.example.new_final_project.dto.OrderPricing;
import com.example.new_final_project.model.Discount;
import com.example.new_final_project.model.Product;
import com.example.new_final_project.Repository.DiscountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {

    private final DiscountStrategyRegistry registry;
    private final DiscountRepository discountRepository;

    public PricingService(DiscountStrategyRegistry registry, DiscountRepository discountRepository) {
        this.registry = registry;
        this.discountRepository = discountRepository;
    }

    // ---------- Product price ----------

    public boolean hasDiscount(Product product) {
        return product.getDiscount() != null && product.getDiscount().isActive();
    }

    public BigDecimal finalPrice(Product product) {
        BigDecimal price = product.getPrice();
        if (!hasDiscount(product)) {
            return price;
        }
        Discount discount = product.getDiscount();
        BigDecimal off = registry.get(discount.getDiscountType()).discountOn(price, discount);
        return price.subtract(off).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    // ---------- Promo code and order totals ----------

    // Returns null when no code was entered
    @Transactional(readOnly = true)
    public Discount findPromo(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        Discount discount = discountRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid promo code"));
        if (!discount.isActive()) {
            throw new IllegalArgumentException("This promo code is no longer active");
        }
        return discount;
    }

    public OrderPricing priceOrder(BigDecimal subtotal, Discount promo) {
        BigDecimal shipping = AppConstants.Shipping.COST;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (promo != null) {
            BigDecimal min = promo.getMinOrderAmount();
            if (min != null && subtotal.compareTo(min) < 0) {
                throw new IllegalArgumentException("Code " + promo.getCode()
                        + " needs an order of at least " + min + " €");
            }
            DiscountStrategy strategy = registry.get(promo.getDiscountType());
            discountAmount = strategy.discountOn(subtotal, promo).min(subtotal);
            if (strategy.waivesShipping()) {
                shipping = BigDecimal.ZERO;
            }
        }

        discountAmount = discountAmount.setScale(2, RoundingMode.HALF_UP);
        shipping = shipping.setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(discountAmount).add(shipping);
        return new OrderPricing(subtotal, discountAmount, shipping, total);
    }
}