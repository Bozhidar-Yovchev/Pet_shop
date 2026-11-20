package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String view(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                       Model model) {
        model.addAttribute("cart", cartService.getView(customerId));
        return "cart";
    }

    @PostMapping("/add")
    public String add(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                      @RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      RedirectAttributes redirect) {
        try {
            cartService.addItem(customerId, productId, quantity);
            redirect.addFlashAttribute("message", "Added to cart");
            return "redirect:/cart";
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/" + productId;
        }
    }

    @PostMapping("/items/{itemId}/update")
    public String update(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                         @PathVariable Long itemId,
                         @RequestParam int quantity,
                         RedirectAttributes redirect) {
        try {
            cartService.updateQuantity(customerId, itemId, quantity);
            redirect.addFlashAttribute("message", "Cart updated");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/items/{itemId}/remove")
    public String remove(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                         @PathVariable Long itemId,
                         RedirectAttributes redirect) {
        cartService.removeItem(customerId, itemId);
        redirect.addFlashAttribute("message", "Item removed");
        return "redirect:/cart";
    }
}