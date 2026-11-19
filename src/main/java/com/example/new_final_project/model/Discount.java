package com.example.new_final_project.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String code;

    private String discountType;


    @Column(name = "discount_value")
    private BigDecimal value;

    private BigDecimal minOrderAmount;
    private boolean active;

    public Discount() {
    }

    public Discount(String name, String code, String discountType,
                    BigDecimal value, BigDecimal minOrderAmount, boolean active) {
        this.name = name;
        this.code = code;
        this.discountType = discountType;
        this.value = value;
        this.minOrderAmount = minOrderAmount;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public BigDecimal getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}