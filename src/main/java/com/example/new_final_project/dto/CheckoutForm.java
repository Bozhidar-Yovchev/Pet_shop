package com.example.new_final_project.dto;

import com.example.new_final_project.config.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutForm {

    @NotNull(message = "Choose a delivery address")
    private Long addressId;

    @NotBlank(message = "Choose a payment method")
    private String paymentMethod = AppConstants.PaymentMethod.CASH_ON_DELIVERY;

    private String cardNumber;   // only used for validation, never stored

    private String promoCode;

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
}