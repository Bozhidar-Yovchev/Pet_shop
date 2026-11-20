package com.example.new_final_project.service;

import com.example.new_final_project.dto.CartView;
import com.example.new_final_project.dto.CartView.CartLine;
import com.example.new_final_project.model.Cart;
import com.example.new_final_project.model.CartItem;
import com.example.new_final_project.model.Customer;
import com.example.new_final_project.model.Product;
import com.example.new_final_project.Repository.CartRepository;
import com.example.new_final_project.Repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository,
                       CustomerRepository customerRepository,
                       ProductService productService) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.productService = productService;
    }

    // ---------- Reading ----------

    @Transactional(readOnly = true)
    public CartView getView(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId).orElse(null);
        if (cart == null || cart.getItems().isEmpty()) {
            return new CartView(List.of(), BigDecimal.ZERO, false);
        }

        List<CartLine> lines = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        boolean allowed = true;

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int stock = stockOf(product);
            BigDecimal unit = productService.finalPrice(product);
            BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(item.getQuantity()));

            String problem = null;
            if (!product.isActive()) {
                problem = "This product is no longer available";
            } else if (item.getQuantity() > stock) {
                problem = stock == 0 ? "Out of stock" : "Only " + stock + " in stock";
            }
            if (problem != null) {
                allowed = false;
            }

            subtotal = subtotal.add(lineTotal);
            lines.add(new CartLine(item.getId(), product, item.getQuantity(),
                    unit, lineTotal, stock, problem));
        }
        return new CartView(lines, subtotal, allowed);
    }

    // ---------- Changing the cart ----------

    @Transactional
    public void addItem(Long customerId, Long productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        Product product = productService.getById(productId);
        if (!product.isActive()) {
            throw new IllegalStateException("This product is not available");
        }

        Cart cart = getOrCreateCart(customerId);
        CartItem existing = findItemByProduct(cart, productId);
        int inCart = existing == null ? 0 : existing.getQuantity();
        int wanted = inCart + quantity;
        int stock = stockOf(product);

        if (wanted > stock) {
            String message = stock == 0
                    ? "This product is out of stock"
                    : "Only " + stock + " in stock"
                    + (inCart > 0 ? " (you already have " + inCart + " in your cart)" : "");
            throw new IllegalStateException(message);
        }

        if (existing == null) {
            cart.addItem(new CartItem(wanted, product));
        } else {
            existing.setQuantity(wanted);
        }
    }

    @Transactional
    public void updateQuantity(Long customerId, Long itemId, int quantity) {
        Cart cart = getExistingCart(customerId);
        CartItem item = findItem(cart, itemId);

        if (quantity <= 0) {
            cart.removeItem(item);
            return;
        }
        int stock = stockOf(item.getProduct());
        if (quantity > stock) {
            throw new IllegalStateException("Only " + stock + " in stock for "
                    + item.getProduct().getName());
        }
        item.setQuantity(quantity);
    }

    @Transactional
    public void removeItem(Long customerId, Long itemId) {
        Cart cart = getExistingCart(customerId);
        cart.removeItem(findItem(cart, itemId));
    }

    // ---------- Helpers ----------

    private Cart getOrCreateCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId).orElseGet(() -> {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new NotFoundException("Customer not found"));
            Cart cart = new Cart();
            cart.setCustomer(customer);
            return cartRepository.save(cart);
        });
    }

    private Cart getExistingCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));
    }

    // Looks only inside this customer's cart, so other people's items are never found
    private CartItem findItem(Cart cart, Long itemId) {
        return cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
    }

    private CartItem findItemByProduct(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    private int stockOf(Product product) {
        return product.getInventory() == null || product.getInventory().getQuantity() == null
                ? 0 : product.getInventory().getQuantity();
    }
}