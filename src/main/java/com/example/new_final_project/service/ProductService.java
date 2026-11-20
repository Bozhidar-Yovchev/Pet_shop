package com.example.new_final_project.service;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.dto.ProductForm;
import com.example.new_final_project.model.*;
import com.example.new_final_project.Repository.CategoryRepository;
import com.example.new_final_project.Repository.DiscountRepository;
import com.example.new_final_project.Repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final DiscountRepository discountRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          DiscountRepository discountRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.discountRepository = discountRepository;
    }

    // ---------- Reading ----------

    @Transactional(readOnly = true)
    public List<Product> search(String q, String species, String ageGroup,
                                Long categoryId, boolean onlyActive) {

        // Filtering by a main category also includes its subcategories
        List<Long> categoryIds = categoryId == null ? null : categoryAndChildren(categoryId);

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (onlyActive) {
                predicates.add(cb.isTrue(root.get("active")));
            }
            if (q != null && !q.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + q.trim().toLowerCase() + "%"));
            }
            if (species != null && !species.isBlank()) {
                predicates.add(cb.equal(root.get("animalSpecies"), species));
            }
            if (ageGroup != null && !ageGroup.isBlank()) {
                predicates.add(cb.equal(root.get("ageGroup"), ageGroup));
            }
            if (categoryIds != null) {
                predicates.add(root.get("category").get("id").in(categoryIds));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return productRepository.findAll(spec, Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Transactional(readOnly = true)
    public List<Discount> findProductDiscounts() {
        return discountRepository.findByCodeIsNull();
    }

    // ---------- Price (called from templates too) ----------

    public boolean hasDiscount(Product product) {
        return product.getDiscount() != null && product.getDiscount().isActive();
    }

    public BigDecimal finalPrice(Product product) {
        BigDecimal price = product.getPrice();
        if (!hasDiscount(product)) {
            return price;
        }
        Discount discount = product.getDiscount();
        BigDecimal result = switch (discount.getDiscountType()) {
            case AppConstants.DiscountType.PERCENTAGE -> price.subtract(
                    price.multiply(discount.getValue())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            case AppConstants.DiscountType.FIXED -> price.subtract(discount.getValue());
            default -> price;
        };
        return result.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    // ---------- Admin: create / update / (de)activate ----------

    @Transactional(readOnly = true)
    public ProductForm toForm(Product product) {
        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setDescription(product.getDescription());
        form.setPrice(product.getPrice());
        form.setAnimalSpecies(product.getAnimalSpecies());
        form.setAgeGroup(product.getAgeGroup());
        form.setActive(product.isActive());
        form.setCategoryId(product.getCategory() == null ? null : product.getCategory().getId());
        form.setDiscountId(product.getDiscount() == null ? null : product.getDiscount().getId());
        form.setQuantity(product.getInventory() == null ? 0 : product.getInventory().getQuantity());
        form.setImageUrl(product.getImages().isEmpty() ? "" : product.getImages().get(0).getImageUrl());
        return form;
    }

    @Transactional
    public Product create(ProductForm form) {
        Product product = new Product();
        apply(product, form);
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductForm form) {
        Product product = getById(id);
        apply(product, form);
        return productRepository.save(product);
    }

    @Transactional
    public void setActive(Long id, boolean active) {
        getById(id).setActive(active);
    }

    // ---------- Helpers ----------

    private void apply(Product product, ProductForm form) {
        product.setName(form.getName().trim());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice().setScale(2, RoundingMode.HALF_UP));
        product.setAnimalSpecies(form.getAnimalSpecies());
        product.setAgeGroup(form.getAgeGroup());
        product.setActive(form.isActive());

        product.setCategory(categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found")));

        product.setDiscount(form.getDiscountId() == null ? null
                : discountRepository.findById(form.getDiscountId())
                .orElseThrow(() -> new NotFoundException("Discount not found")));

        if (product.getInventory() == null) {
            product.setInventory(new Inventory(null, form.getQuantity()));
        } else {
            product.getInventory().setQuantity(form.getQuantity());
        }

        // One image per product for now
        String url = form.getImageUrl() == null ? "" : form.getImageUrl().trim();
        if (url.isEmpty()) {
            product.getImages().clear();
        } else if (product.getImages().isEmpty()) {
            product.addImage(new ProductImage(url, product));
        } else {
            product.getImages().get(0).setImageUrl(url);
        }
    }

    private List<Long> categoryAndChildren(Long categoryId) {
        List<Long> ids = new ArrayList<>();
        ids.add(categoryId);
        categoryRepository.findByParentId(categoryId).forEach(c -> ids.add(c.getId()));
        return ids;
    }
}