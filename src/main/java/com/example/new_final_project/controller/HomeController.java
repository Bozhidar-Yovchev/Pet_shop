package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Product;
import com.example.new_final_project.service.CategoryService;
import com.example.new_final_project.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public HomeController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Product> all = productService.search(null, null, null, null, true);

        model.addAttribute("deals", all.stream()
                .filter(productService::hasDiscount)
                .limit(4).toList());
        model.addAttribute("newest", all.stream()
                .sorted(Comparator.comparing(Product::getId).reversed())
                .limit(4).toList());
        model.addAttribute("mainCategories", categoryService.findMain());
        model.addAttribute("speciesList", AppConstants.Species.ALL);
        return "index";
    }
}