
package com.example.new_final_project.model;

import jakarta.persistence.*;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public CartItem() {
    }

    public CartItem(Integer quantity, Product product) {
        this.quantity = quantity;
        this.product = product;
    }

    // Getters and Setters

    public Long getId() {return id;}

    public Integer getQuantity() {return quantity;}

    public void setQuantity(Integer quantity) {this.quantity = quantity;}

    public Cart getCart() {return cart;}

    public void setCart(Cart cart) {this.cart = cart;}

    public Product getProduct() {return product;}

    public void setProduct(Product product) {this.product = product;}
}
