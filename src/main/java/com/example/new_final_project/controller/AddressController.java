package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.dto.AddressForm;
import com.example.new_final_project.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public String list(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                       Model model) {
        model.addAttribute("addresses", addressService.findByCustomer(customerId));
        return "addresses";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("addressForm", new AddressForm());
        model.addAttribute("formAction", "/addresses");
        model.addAttribute("title", "New address");
        return "address-form";
    }

    @PostMapping
    public String create(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                         @Valid @ModelAttribute("addressForm") AddressForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("formAction", "/addresses");
            model.addAttribute("title", "New address");
            return "address-form";
        }
        addressService.create(customerId, form);
        redirect.addFlashAttribute("message", "Address added");
        return "redirect:/addresses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                           @PathVariable Long id,
                           Model model) {
        model.addAttribute("addressForm", addressService.toForm(addressService.getOwned(customerId, id)));
        model.addAttribute("formAction", "/addresses/" + id);
        model.addAttribute("title", "Edit address");
        return "address-form";
    }

    @PostMapping("/{id}")
    public String update(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                         @PathVariable Long id,
                         @Valid @ModelAttribute("addressForm") AddressForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("formAction", "/addresses/" + id);
            model.addAttribute("title", "Edit address");
            return "address-form";
        }
        addressService.update(customerId, id, form);
        redirect.addFlashAttribute("message", "Address updated");
        return "redirect:/addresses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                         @PathVariable Long id,
                         RedirectAttributes redirect) {
        addressService.delete(customerId, id);
        redirect.addFlashAttribute("message", "Address deleted");
        return "redirect:/addresses";
    }
}