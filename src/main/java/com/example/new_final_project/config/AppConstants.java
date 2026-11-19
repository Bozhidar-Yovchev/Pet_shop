package com.example.new_final_project.config;

public final class AppConstants {

    private AppConstants() {}

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