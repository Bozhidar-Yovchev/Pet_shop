package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Order;
import com.example.new_final_project.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String history(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                          Model model) {
        model.addAttribute("orders", orderService.findByCustomer(customerId));
        return "orders";
    }

    @GetMapping("/{id}")
    public String details(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                          @PathVariable Long id,
                          Model model) {
        Order order = orderService.getOwned(customerId, id);
        model.addAttribute("order", order);
        model.addAttribute("canCancel", orderService.canCancel(order));
        return "order-details";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                         @PathVariable Long id,
                         RedirectAttributes redirect) {
        try {
            orderService.cancel(customerId, id);
            redirect.addFlashAttribute("message", "Order cancelled");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }
}