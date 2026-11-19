
package com.example.new_final_project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shipment")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shippingAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    public Shipment() {
    }

    public Shipment(String shippingAddress, Order order) {
        this.shippingAddress = shippingAddress;
        this.order = order;
    }

    public Long getId() {return id;}

    public String getShippingAddress() {return shippingAddress;}

    public void setShippingAddress(String shippingAddress) {this.shippingAddress = shippingAddress;}

    public Order getOrder() {return order;}

    public void setOrder(Order order) {this.order = order;}
}
