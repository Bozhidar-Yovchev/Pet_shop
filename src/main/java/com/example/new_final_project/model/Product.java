
package com.example.new_final_project.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private String animalSpecies;
    private String ageGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", unique = true)
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    // JPA requires a no-argument constructor
    public Product() {
    }

    // Constructor with basic fields
    public Product(String name, String description, BigDecimal price,
                   String animalSpecies, String ageGroup,
                   Category category, Inventory inventory, Discount discount) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.animalSpecies = animalSpecies;
        this.ageGroup = ageGroup;
        this.category = category;
        this.inventory = inventory;
        this.discount = discount;
    }

    // Getters and Setters

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public BigDecimal getPrice() {return price;}

    public void setPrice(BigDecimal price) {this.price = price;}

    public String getAnimalSpecies() {return animalSpecies;}

    public void setAnimalSpecies(String animalSpecies) {this.animalSpecies = animalSpecies;}

    public String getAgeGroup() {return ageGroup;}

    public void setAgeGroup(String ageGroup) {this.ageGroup = ageGroup;}

    public Category getCategory() {return category;}

    public void setCategory(Category category) {this.category = category;}

    public Inventory getInventory() {return inventory;}

    public void setInventory(Inventory inventory) {this.inventory = inventory;}

    public Discount getDiscount() {return discount;}

    public void setDiscount(Discount discount) {this.discount = discount;}

    public List<ProductImage> getImages() {return images;}

    public void setImages(List<ProductImage> images) {this.images = images;}

    // Helper methods for the bidirectional ProductImage relationship

    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }

}
