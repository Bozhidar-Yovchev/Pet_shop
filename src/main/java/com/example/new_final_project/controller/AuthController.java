package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.model.RegisterDto;
import com.example.new_final_project.model.Customer;
import com.example.new_final_project.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ---------- Register ----------

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDto") RegisterDto dto,
                           BindingResult result) {

        if (dto.getPassword() != null
                && !dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
        }
        if (dto.getUsername() != null && authService.usernameTaken(dto.getUsername().trim())) {
            result.rejectValue("username", "taken", "Username is already taken");
        }
        if (dto.getEmail() != null
                && authService.emailTaken(dto.getEmail().trim().toLowerCase())) {
            result.rejectValue("email", "taken", "Email is already registered");
        }
        if (result.hasErrors()) {
            return "register";
        }

        authService.register(dto);
        return "redirect:/login?registered";
    }

    // ---------- Login / Logout ----------

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {

        Optional<Customer> customer = authService.authenticate(username, password);
        if (customer.isEmpty()) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        request.changeSessionId();   // protects against session fixation
        HttpSession session = request.getSession();
        session.setAttribute(AppConstants.SessionKeys.CUSTOMER_ID, customer.get().getId());
        session.setAttribute(AppConstants.SessionKeys.CUSTOMER_NAME, customer.get().getFullName());
        session.setAttribute(AppConstants.SessionKeys.ROLE, customer.get().getRole());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}