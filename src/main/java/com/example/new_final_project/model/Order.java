
package com.example.new_final_project.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate;

    private String status;

    private BigDecimal subtotal;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Payment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Shipment shipment;

    public Order() {
    }


    public Long getId() {return id;}

    public LocalDateTime getOrderDate() {return orderDate;}

    public void setOrderDate(LocalDateTime orderDate) {this.orderDate = orderDate;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public BigDecimal getSubtotal() {return subtotal;}

    public void setSubtotal(BigDecimal subtotal) {this.subtotal = subtotal;}

    public BigDecimal getDiscountAmount() {return discountAmount;}

    public void setDiscountAmount(BigDecimal discountAmount) {this.discountAmount = discountAmount;}

    public BigDecimal getTotalAmount() {return totalAmount;}

    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}

    public Customer getCustomer() {return customer;}

    public void setCustomer(Customer customer) {this.customer = customer;}

    public List<OrderItem> getItems() {return items;}

    public void setItems(List<OrderItem> items) {this.items = items;}

    public Payment getPayment() {return payment;}

    public void setPayment(Payment payment) {
        this.payment = payment;

        if (payment != null) {
            payment.setOrder(this);
        }
    }

    public Shipment getShipment() {return shipment;}

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;

        if (shipment != null) {
            shipment.setOrder(this);
        }
    }


    public void addItem(OrderItem item)
    {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item)
    {
        if (items.remove(item))
        {
            item.setOrder(null);
        }

    }
}
