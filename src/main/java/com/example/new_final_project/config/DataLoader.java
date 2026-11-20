package com.example.new_final_project.config;

import com.example.new_final_project.model.*;
import com.example.new_final_project.Repository.CategoryRepository;
import com.example.new_final_project.Repository.DiscountRepository;
import com.example.new_final_project.Repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.new_final_project.config.PasswordHasher;
import com.example.new_final_project.Repository.CustomerRepository;
import com.example.new_final_project.config.PasswordHasher;


import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;


    public DataLoader(CategoryRepository categoryRepository,
                      ProductRepository productRepository,
                      DiscountRepository discountRepository,
                      CustomerRepository customerRepository,
                      PasswordHasher passwordHasher) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }
        customerRepository.save(new Customer("Administrator", "admin", "admin@petshop.com",
                passwordHasher.encode("admin123"), AppConstants.Role.ADMIN, "REGULAR"));
        customerRepository.save(new Customer("Test Client", "client", "client@petshop.com",
                passwordHasher.encode("client123"), AppConstants.Role.CUSTOMER, "REGULAR"));

        // Categories and subcategories
        Category food = categoryRepository.save(new Category("Food", null));
        Category dryFood = categoryRepository.save(new Category("Dry food", food));
        Category wetFood = categoryRepository.save(new Category("Wet food", food));
        Category toys = categoryRepository.save(new Category("Toys", null));
        Category accessories = categoryRepository.save(new Category("Accessories", null));
        Category leashes = categoryRepository.save(new Category("Leashes and collars", accessories));

        // Discounts: product discount (no code) and promo codes
        Discount spring = discountRepository.save(new Discount(
                "Spring sale -10%", null, AppConstants.DiscountType.PERCENTAGE,
                new BigDecimal("10"), null, true));
        discountRepository.save(new Discount(
                "Welcome 15%", "WELCOME15", AppConstants.DiscountType.PERCENTAGE,
                new BigDecimal("15"), null, true));
        discountRepository.save(new Discount(
                "Save 5 EUR", "SAVE5", AppConstants.DiscountType.FIXED,
                new BigDecimal("5"), new BigDecimal("30"), true));
        discountRepository.save(new Discount(
                "Free shipping over 50", "FREESHIP", AppConstants.DiscountType.FREE_SHIPPING,
                BigDecimal.ZERO, new BigDecimal("50"), true));

        // Products (the Inventory and the image are saved through cascade)
        createProduct("Royal Dog Adult 15kg", "Dry food for adult dogs", "54.90",
                "DOG", "ADULT", dryFood, 25, spring, "https://placehold.co/400x300?text=Dog+Food");
        createProduct("Puppy Starter 3kg", "Dry food for puppies", "19.90",
                "DOG", "PUPPY", dryFood, 40, null, "https://placehold.co/400x300?text=Puppy+Food");
        createProduct("Cat Pouch Salmon 85g", "Wet food for adult cats", "1.50",
                "CAT", "ADULT", wetFood, 200, null, "https://placehold.co/400x300?text=Cat+Pouch");
        createProduct("Kitten Wet Mix 12x85g", "Wet food for kittens", "14.90",
                "CAT", "KITTEN", wetFood, 60, spring, "https://placehold.co/400x300?text=Kitten+Mix");
        createProduct("Rubber Ball", "Durable ball for dogs", "4.50",
                "DOG", "ALL", toys, 80, null, "https://placehold.co/400x300?text=Ball");
        createProduct("Feather Wand", "Interactive cat toy", "6.20",
                "CAT", "ALL", toys, 5, null, "https://placehold.co/400x300?text=Wand");
        createProduct("Leather Dog Leash 2m", "Strong leather leash", "17.00",
                "DOG", "ADULT", leashes, 30, null, "https://placehold.co/400x300?text=Leash");
        createProduct("Parrot Seed Mix 1kg", "Seed mix for parrots", "8.90",
                "BIRD", "ADULT", dryFood, 0, null, "https://placehold.co/400x300?text=Seeds");
    }

    private void createProduct(String name, String description, String price,
                               String species, String ageGroup, Category category,
                               int quantity, Discount discount, String imageUrl) {
        Product product = new Product(name, description, new BigDecimal(price),
                species, ageGroup, category, new Inventory(null, quantity), discount);
        product.addImage(new ProductImage(imageUrl, product));
        productRepository.save(product);
    }
}