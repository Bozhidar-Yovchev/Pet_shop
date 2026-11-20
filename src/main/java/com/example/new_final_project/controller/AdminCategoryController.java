package com.example.new_final_project.controller;

import com.example.new_final_project.model.Category;
import com.example.new_final_project.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/categories";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        return form(model, "/admin/categories", "New category", "", null, null, null);
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(required = false) Long parentId,
                         Model model,
                         RedirectAttributes redirect) {
        try {
            categoryService.create(name, parentId);
            redirect.addFlashAttribute("message", "Category created");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            return form(model, "/admin/categories", "New category", name, parentId, null, e.getMessage());
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getById(id);
        Long parentId = category.getParent() == null ? null : category.getParent().getId();
        return form(model, "/admin/categories/" + id, "Edit category",
                category.getName(), parentId, id, null);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam(required = false) Long parentId,
                         Model model,
                         RedirectAttributes redirect) {
        try {
            categoryService.update(id, name, parentId);
            redirect.addFlashAttribute("message", "Category updated");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            return form(model, "/admin/categories/" + id, "Edit category", name, parentId, id, e.getMessage());
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            categoryService.delete(id);
            redirect.addFlashAttribute("message", "Category deleted");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    private String form(Model model, String action, String title, String name,
                        Long parentId, Long selfId, String error) {
        model.addAttribute("formAction", action);
        model.addAttribute("title", title);
        model.addAttribute("name", name);
        model.addAttribute("parentId", parentId);
        model.addAttribute("error", error);
        model.addAttribute("mainCategories", categoryService.findMain().stream()
                .filter(c -> !c.getId().equals(selfId))
                .toList());
        return "admin/category-form";
    }
}