package com.example.new_final_project.service;

import com.example.new_final_project.model.Category;
import com.example.new_final_project.Repository.CategoryRepository;
import com.example.new_final_project.Repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public List<Category> findMain() {
        return categoryRepository.findByParentIsNull();
    }

    @Transactional(readOnly = true)
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Transactional
    public Category create(String name, Long parentId) {
        Category category = new Category();
        apply(category, name, parentId);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, String name, Long parentId) {
        Category category = getById(id);
        apply(category, name, parentId);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getById(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("Category has products and cannot be deleted");
        }
        if (categoryRepository.existsByParentId(id)) {
            throw new IllegalStateException("Category has subcategories and cannot be deleted");
        }
        categoryRepository.delete(category);
    }

    private void apply(Category category, String name, Long parentId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        Category parent = null;
        if (parentId != null) {
            if (parentId.equals(category.getId())) {
                throw new IllegalArgumentException("A category cannot be its own parent");
            }
            parent = getById(parentId);
            if (parent.getParent() != null) {
                throw new IllegalArgumentException("Subcategories can only be one level deep");
            }
            if (category.getId() != null && categoryRepository.existsByParentId(category.getId())) {
                throw new IllegalArgumentException(
                        "This category has subcategories, so it cannot become a subcategory");
            }
        }
        category.setName(name.trim());
        category.setParent(parent);
    }
}