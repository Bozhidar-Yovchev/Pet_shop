package com.example.new_final_project.designpatterns.facade;

import com.example.new_final_project.designpatterns.builder.OrderBuilder;
import com.example.new_final_project.designpatterns.factory.PaymentDetails;
import com.example.new_final_project.designpatterns.factory.PaymentHandler;
import com.example.new_final_project.designpatterns.factory.PaymentHandlerFactory;
import com.example.new_final_project.dto.CartView;
import com.example.new_final_project.dto.CartView.CartLine;
import com.example.new_final_project.dto.CheckoutForm;
import com.example.new_final_project.dto.OrderPricing;
import com.example.new_final_project.model.*;
import com.example.new_final_project.Repository.*;
import com.example.new_final_project.service.AddressService;
import com.example.new_final_project.service.CartService;
import com.example.new_final_project.service.NotFoundException;
import com.example.new_final_project.service.PricingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// One call runs the whole checkout. If any step fails, everything is rolled back.
@Service
public class CheckoutFacade {

    private final CartService cartService;
    private final AddressService addressService;
    private final PricingService pricingService;
    private final PaymentHandlerFactory paymentHandlerFactory;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final InventoryRepository inventoryRepository;

    public CheckoutFacade(CartService cartService,
                          AddressService addressService,
                          PricingService pricingService,
                          PaymentHandlerFactory paymentHandlerFactory,
                          CustomerRepository customerRepository,
                          CartRepository cartRepository,
                          OrderRepository orderRepository,
                          PaymentRepository paymentRepository,
                          InventoryRepository inventoryRepository) {
        this.cartService = cartService;
        this.addressService = addressService;
        this.pricingService = pricingService;
        this.paymentHandlerFactory = paymentHandlerFactory;
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public Order checkout(Long customerId, CheckoutForm form) {

        // 1. Cart and stock check
        CartView cart = cartService.getView(customerId);
        if (cart.lines().isEmpty()) {
            throw new IllegalStateException("Your cart is empty");
        }
        if (!cart.checkoutAllowed()) {
            String problem = cart.lines().stream()
                    .filter(l -> l.problem() != null)
                    .map(l -> l.product().getName() + ": " + l.problem())
                    .findFirst().orElse("Please check your cart");
            throw new IllegalStateException(problem);
        }

        // 2. Customer and delivery address (must belong to this customer)
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        Address address = addressService.getOwned(customerId, form.getAddressId());
        String addressText = address.getStreet() + ", " + address.getPostalCode() + " " + address.getCity();

        // 3. Promo code and totals (Strategy)
        Discount promo = pricingService.findPromo(form.getPromoCode());
        OrderPricing pricing = pricingService.priceOrder(cart.subtotal(), promo);

        // 4. Build the order (Builder)
        OrderBuilder builder = new OrderBuilder()
                .customer(customer)
                .shippingAddress(addressText)
                .discount(pricing.discountAmount(), promo == null ? null : promo.getCode())
                .shippingCost(pricing.shippingCost());
        for (CartLine line : cart.lines()) {
            builder.addItem(line.product(), line.quantity(), line.unitPrice());
        }
        Order order = orderRepository.save(builder.build());

        // 5. Decrease stock (atomic, fails if somebody bought it first)
        for (OrderItem item : order.getItems()) {
            int updated = inventoryRepository.decreaseIfAvailable(
                    item.getProduct().getInventory().getId(), item.getQuantity());
            if (updated == 0) {
                throw new IllegalStateException("Not enough stock for " + item.getProduct().getName());
            }
        }

        // 6. Payment (Factory Method)
        PaymentHandler handler = paymentHandlerFactory.create(form.getPaymentMethod());
        Payment payment = handler.pay(order, new PaymentDetails(form.getCardNumber()));
        paymentRepository.save(payment);
        order.setPayment(payment);

        // 7. Empty the cart
        cartRepository.findByCustomerId(customerId).ifPresent(c -> c.getItems().clear());

        return order;
    }
}