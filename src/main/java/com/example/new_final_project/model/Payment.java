
package com.example.new_final_project.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentMethod;

    private String status;

    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    public Payment() {
    }

    public Payment(String paymentMethod, String status,
                   BigDecimal amount, Order order) {
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.amount = amount;
        this.order = order;
    }


    public Long getId() {return id;}

    public String getPaymentMethod() {return paymentMethod;}

    public void setPaymentMethod(String paymentMethod) {this.paymentMethod = paymentMethod;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public BigDecimal getAmount() {return amount;}

    public void setAmount(BigDecimal amount) {this.amount = amount;}

    public Order getOrder() {return order;}

    public void setOrder(Order order) {this.order = order;}
}
