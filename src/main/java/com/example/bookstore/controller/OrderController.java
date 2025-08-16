package com.example.bookstore.controller;

import com.example.bookstore.model.Order;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.OrderService;
import com.example.bookstore.security.services.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@AuthenticationPrincipal UserDetailsImpl userDetails, 
                                    @RequestBody Map<String, Object> checkoutRequest) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Extract checkout details
            String shippingAddress = (String) checkoutRequest.get("shippingAddress");
            String paymentMethod = (String) checkoutRequest.get("paymentMethod");
            
            Order order = orderService.checkout(user, shippingAddress, paymentMethod);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Order order = orderService.createOrder(user);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Order> orders = orderService.getUserOrders(user);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@AuthenticationPrincipal UserDetailsImpl userDetails, 
                                        @PathVariable Long orderId) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Order order = orderService.getOrderById(orderId, user);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@AuthenticationPrincipal UserDetailsImpl userDetails, 
                                             @PathVariable String status) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Order> orders = orderService.getOrdersByStatus(user, status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@AuthenticationPrincipal UserDetailsImpl userDetails, 
                                       @PathVariable Long orderId) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Order order = orderService.cancelOrder(orderId, user);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@AuthenticationPrincipal UserDetailsImpl userDetails, 
                                             @PathVariable Long orderId, 
                                             @RequestBody Map<String, String> statusUpdate) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String newStatus = statusUpdate.get("status");
            Order order = orderService.updateOrderStatus(orderId, user, newStatus);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentOrders(@AuthenticationPrincipal UserDetailsImpl userDetails, 
                                           @RequestParam(defaultValue = "5") int limit) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Order> orders = orderService.getRecentOrders(user, limit);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getOrderSummary(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Map<String, Object> summary = orderService.getOrderSummary(user);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}