package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Product;
import com.example.new_final_project.service.CategoryService;
import com.example.new_final_project.service.NotFoundException;
import com.example.new_final_project.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class CatalogController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public CatalogController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String species,
                       @RequestParam(required = false) String ageGroup,
                       @RequestParam(required = false) Long categoryId,
                       Model model) {

        model.addAttribute("products", productService.search(q, species, ageGroup, categoryId, true));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("speciesList", AppConstants.Species.ALL);
        model.addAttribute("ageGroups", AppConstants.AgeGroup.ALL);

        // keep the chosen filters selected in the form
        model.addAttribute("q", q);
        model.addAttribute("species", species);
        model.addAttribute("ageGroup", ageGroup);
        model.addAttribute("categoryId", categoryId);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String details(@PathVariable Long id,
                          @SessionAttribute(name = AppConstants.SessionKeys.ROLE, required = false) String role,
                          Model model) {

        Product product = productService.getById(id);
        boolean admin = AppConstants.Role.ADMIN.equals(role);
        if (!product.isActive() && !admin) {
            throw new NotFoundException("Product not found");
        }
        model.addAttribute("product", product);
        return "product-details";
    }
}