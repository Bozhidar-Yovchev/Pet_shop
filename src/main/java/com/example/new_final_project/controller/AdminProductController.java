package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.dto.ProductForm;
import com.example.new_final_project.model.Category;
import com.example.new_final_project.model.Discount;
import com.example.new_final_project.service.CategoryService;
import com.example.new_final_project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // Available in every view of this controller
    @ModelAttribute("categories")
    public List<Category> categories() {
        return categoryService.findAll();
    }

    @ModelAttribute("discounts")
    public List<Discount> discounts() {
        return productService.findProductDiscounts();
    }

    @ModelAttribute("speciesList")
    public List<String> speciesList() {
        return AppConstants.Species.ALL;
    }

    @ModelAttribute("ageGroups")
    public List<String> ageGroups() {
        return AppConstants.AgeGroup.ALL;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.search(null, null, null, null, false));
        return "admin/products";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("formAction", "/admin/products");
        return "admin/product-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("productForm") ProductForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("formAction", "/admin/products");
            return "admin/product-form";
        }
        productService.create(form);
        redirect.addFlashAttribute("message", "Product created");
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("productForm", productService.toForm(productService.getById(id)));
        model.addAttribute("formAction", "/admin/products/" + id);
        return "admin/product-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("productForm") ProductForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("formAction", "/admin/products/" + id);
            return "admin/product-form";
        }
        productService.update(id, form);
        redirect.addFlashAttribute("message", "Product updated");
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirect) {
        productService.setActive(id, false);
        redirect.addFlashAttribute("message", "Product deactivated");
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/activate")
    public String activate(@PathVariable Long id, RedirectAttributes redirect) {
        productService.setActive(id, true);
        redirect.addFlashAttribute("message", "Product activated");
        return "redirect:/admin/products";
    }
}