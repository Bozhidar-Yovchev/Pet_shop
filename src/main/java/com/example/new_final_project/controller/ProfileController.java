package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.dto.PasswordForm;
import com.example.new_final_project.dto.ProfileForm;
import com.example.new_final_project.model.Customer;
import com.example.new_final_project.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final CustomerService customerService;

    public ProfileController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/profile")
    public String view(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                       Model model) {
        Customer customer = customerService.getById(customerId);
        ProfileForm form = new ProfileForm();
        form.setFullName(customer.getFullName());
        form.setEmail(customer.getEmail());
        model.addAttribute("profileForm", form);
        model.addAttribute("passwordForm", new PasswordForm());
        model.addAttribute("username", customer.getUsername());
        return "profile";
    }

    @PostMapping("/profile")
    public String update(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                         @Valid @ModelAttribute("profileForm") ProfileForm form,
                         BindingResult result,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirect) {

        if (!result.hasErrors() && customerService.emailTakenByOther(form.getEmail(), customerId)) {
            result.rejectValue("email", "taken", "Email is already registered");
        }
        if (result.hasErrors()) {
            model.addAttribute("passwordForm", new PasswordForm());
            model.addAttribute("username", customerService.getById(customerId).getUsername());
            return "profile";
        }

        Customer saved = customerService.updateProfile(customerId, form);
        session.setAttribute(AppConstants.SessionKeys.CUSTOMER_NAME, saved.getFullName());
        redirect.addFlashAttribute("message", "Profile updated");
        return "redirect:/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                                 @Valid @ModelAttribute("passwordForm") PasswordForm form,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirect) {

        if (!result.hasErrors()) {
            if (!form.getNewPassword().equals(form.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
            } else if (!customerService.passwordMatches(customerId, form.getCurrentPassword())) {
                result.rejectValue("currentPassword", "wrong", "Current password is wrong");
            }
        }
        if (result.hasErrors()) {
            Customer customer = customerService.getById(customerId);
            ProfileForm profile = new ProfileForm();
            profile.setFullName(customer.getFullName());
            profile.setEmail(customer.getEmail());
            model.addAttribute("profileForm", profile);
            model.addAttribute("username", customer.getUsername());
            return "profile";
        }

        customerService.changePassword(customerId, form.getNewPassword());
        redirect.addFlashAttribute("message", "Password changed");
        return "redirect:/profile";
    }
}