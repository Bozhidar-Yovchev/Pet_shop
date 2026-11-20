package com.example.new_final_project.controller;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.designpatterns.facade.CheckoutFacade;
import com.example.new_final_project.dto.CartView;
import com.example.new_final_project.dto.CheckoutForm;
import com.example.new_final_project.dto.OrderPricing;
import com.example.new_final_project.model.Discount;
import com.example.new_final_project.model.Order;
import com.example.new_final_project.service.AddressService;
import com.example.new_final_project.service.CartService;
import com.example.new_final_project.service.PricingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartService cartService;
    private final AddressService addressService;
    private final PricingService pricingService;
    private final CheckoutFacade checkoutFacade;

    public CheckoutController(CartService cartService,
                              AddressService addressService,
                              PricingService pricingService,
                              CheckoutFacade checkoutFacade) {
        this.cartService = cartService;
        this.addressService = addressService;
        this.pricingService = pricingService;
        this.checkoutFacade = checkoutFacade;
    }

    @GetMapping
    public String view(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                       Model model,
                       RedirectAttributes redirect) {
        CartView cart = cartService.getView(customerId);
        if (cart.lines().isEmpty()) {
            redirect.addFlashAttribute("error", "Your cart is empty");
            return "redirect:/cart";
        }
        CheckoutForm form = new CheckoutForm();
        prepare(model, customerId, cart, form);
        model.addAttribute("checkoutForm", form);
        return "checkout";
    }

    // "Apply code" button: recalculates the totals and keeps the other choices
    @PostMapping("/promo")
    public String applyPromo(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                             @ModelAttribute("checkoutForm") CheckoutForm form,
                             Model model,
                             RedirectAttributes redirect) {
        CartView cart = cartService.getView(customerId);
        if (cart.lines().isEmpty()) {
            redirect.addFlashAttribute("error", "Your cart is empty");
            return "redirect:/cart";
        }
        prepare(model, customerId, cart, form);
        return "checkout";
    }

    @PostMapping
    public String place(@SessionAttribute(AppConstants.SessionKeys.CUSTOMER_ID) Long customerId,
                        @Valid @ModelAttribute("checkoutForm") CheckoutForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirect) {

        if (!result.hasErrors()) {
            try {
                Order order = checkoutFacade.checkout(customerId, form);
                return "redirect:/orders/" + order.getId() + "?placed";
            } catch (IllegalStateException | IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        CartView cart = cartService.getView(customerId);
        if (cart.lines().isEmpty()) {
            redirect.addFlashAttribute("error", "Your cart is empty");
            return "redirect:/cart";
        }
        prepare(model, customerId, cart, form);
        return "checkout";
    }

    // Fills everything the checkout page needs
    private void prepare(Model model, Long customerId, CartView cart, CheckoutForm form) {
        OrderPricing pricing;
        String promoMessage = null;
        String promoError = null;
        try {
            Discount promo = pricingService.findPromo(form.getPromoCode());
            pricing = pricingService.priceOrder(cart.subtotal(), promo);
            if (promo != null) {
                promoMessage = "Code " + promo.getCode() + " applied";
                form.setPromoCode(promo.getCode());
            }
        } catch (IllegalArgumentException e) {
            promoError = e.getMessage();
            form.setPromoCode(null);
            pricing = pricingService.priceOrder(cart.subtotal(), null);
        }
        form.setCardNumber(null);   // never send the card number back to the page

        model.addAttribute("cart", cart);
        model.addAttribute("pricing", pricing);
        model.addAttribute("promoMessage", promoMessage);
        model.addAttribute("promoError", promoError);
        model.addAttribute("addresses", addressService.findByCustomer(customerId));
    }
}