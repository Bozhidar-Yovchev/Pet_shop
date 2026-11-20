package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public String details(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                          @PathVariable Long id,
                          Model model) {
        model.addAttribute("order", orderService.getOwned(customerId, id));
        return "order-details";
    }
}