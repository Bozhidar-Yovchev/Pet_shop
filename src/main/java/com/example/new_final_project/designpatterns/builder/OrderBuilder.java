package com.example.new_final_project.designpatterns.builder;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {

    private Customer customer;
    private String shippingAddress;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private String promoCode;
    private BigDecimal shippingCost = BigDecimal.ZERO;
    private final List<OrderItem> items = new ArrayList<>();

    public OrderBuilder customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public OrderBuilder shippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
        return this;
    }

    public OrderBuilder discount(BigDecimal discountAmount, String promoCode) {
        this.discountAmount = discountAmount;
        this.promoCode = promoCode;
        return this;
    }

    public OrderBuilder shippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
        return this;
    }

    // The unit price is frozen here, later price changes do not affect the order
    public OrderBuilder addItem(Product product, int quantity, BigDecimal unitPrice) {
        items.add(new OrderItem(quantity, unitPrice, product));
        return this;
    }

    public Order build() {
        if (customer == null) {
            throw new IllegalStateException("An order needs a customer");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("An order needs at least one item");
        }
        if (shippingAddress == null || shippingAddress.isBlank()) {
            throw new IllegalStateException("An order needs a shipping address");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(AppConstants.OrderStatus.PENDING);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : items) {
            order.addItem(item);
            subtotal = subtotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmount);
        order.setPromoCode(promoCode);
        order.setShippingCost(shippingCost);
        order.setTotalAmount(subtotal.subtract(discountAmount).add(shippingCost).max(BigDecimal.ZERO));
        order.setShipment(new Shipment(shippingAddress, order));
        return order;
    }
}