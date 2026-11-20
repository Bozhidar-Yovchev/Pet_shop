package com.example.new_final_project.config;

import java.util.List;
import java.math.BigDecimal;

public final class AppConstants {

    private AppConstants() {}

    public static final class PaymentMethod {
        public static final String CARD = "CARD";
        public static final String CASH_ON_DELIVERY = "CASH_ON_DELIVERY";
    }

    public static final class Shipping {
        public static final BigDecimal COST = new BigDecimal("5.00");
    }
    public static final class Species {
        public static final List<String> ALL = List.of("DOG", "CAT", "BIRD", "FISH", "RODENT");
    }

    public static final class AgeGroup {
        public static final List<String> ALL = List.of("PUPPY", "KITTEN", "ADULT", "SENIOR", "ALL");
    }

    public static final class Role {
        public static final String ADMIN = "ADMIN";
        public static final String CUSTOMER = "CUSTOMER";
    }

    public static final class OrderStatus {
        public static final String PENDING = "PENDING";
        public static final String CONFIRMED = "CONFIRMED";
        public static final String SHIPPED = "SHIPPED";
        public static final String DELIVERED = "DELIVERED";
        public static final String CANCELLED = "CANCELLED";
        public static final List<String> ALL = List.of(PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED);
    }

    public static final class Channel {
        public static final String IN_APP = "IN_APP";
        public static final String EMAIL = "EMAIL";
        public static final String SMS = "SMS";
    }

    public static final class PaymentStatus {
        public static final String PENDING = "PENDING";
        public static final String PAID = "PAID";
        public static final String FAILED = "FAILED";
        public static final String REFUNDED = "REFUNDED";
    }

    public static final class DiscountType {
        public static final String PERCENTAGE = "PERCENTAGE";
        public static final String FIXED = "FIXED";
        public static final String FREE_SHIPPING = "FREE_SHIPPING";
    }

    public static final class SessionKeys {
        public static final String CUSTOMER_ID = "customerId";
        public static final String CUSTOMER_NAME = "customerName";
        public static final String ROLE = "role";
    }
}