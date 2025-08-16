package com.example.bookstore.controller;

import com.example.bookstore.model.Cart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.CartService;
import com.example.bookstore.security.services.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {
    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Cart> getCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartService.getCartItems(user);
    }

    @PostMapping
    public Cart addToCart(@AuthenticationPrincipal UserDetailsImpl userDetails, 
                         @RequestParam Long bookId, 
                         @RequestParam(defaultValue = "1") int quantity) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartService.addToCart(user, bookId, quantity);
    }

    @PutMapping
    public void updateCartItem(@AuthenticationPrincipal UserDetailsImpl userDetails,
                              @RequestParam Long bookId,
                              @RequestParam int quantity) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartService.updateCartItem(user, bookId, quantity);
    }

    @DeleteMapping
    public void removeFromCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long bookId) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartService.removeFromCart(user, bookId);
    }

    @DeleteMapping("/clear")
    public void clearCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartService.clearCart(user);
    }
}