package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.Order;
import com.example.new_final_project.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String status, Model model) {
        model.addAttribute("orders", orderService.findAll(status));
        model.addAttribute("statuses", AppConstants.OrderStatus.ALL);
        model.addAttribute("selected", status);
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Order order = orderService.getById(id);
        model.addAttribute("order", order);
        model.addAttribute("nextStatuses", orderService.nextStatuses(order.getStatus()));
        return "admin/order-details";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam String status,
                               RedirectAttributes redirect) {
        try {
            orderService.changeStatus(id, status);
            redirect.addFlashAttribute("message", "Status changed to " + status);
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
}