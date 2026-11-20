package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String list(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                       Model model) {
        model.addAttribute("notifications", notificationService.findForCustomer(customerId));
        return "notifications";
    }

    @PostMapping("/read")
    public String markRead(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId) {
        notificationService.markAllSeen(customerId);
        return "redirect:/notifications";
    }
}