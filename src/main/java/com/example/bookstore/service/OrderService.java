package com.example.bookstore.service;

import com.example.bookstore.model.*;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.OrderItemRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, 
                       BookRepository bookRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public Order checkout(User user, String shippingAddress, String paymentMethod) {
        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Calculate total amount and check stock
        double totalAmount = 0;
        for (Cart cartItem : cartItems) {
            Book book = cartItem.getBook();
            if (book.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for book: " + book.getTitle());
            }
            totalAmount += book.getPrice() * cartItem.getQuantity();
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order = orderRepository.save(order);

        // Create order items and update book stock
        for (Cart cartItem : cartItems) {
            Book book = cartItem.getBook();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(book.getPrice());
            orderItemRepository.save(orderItem);
            
            // Update book stock
            book.setStock(book.getStock() - cartItem.getQuantity());
            bookRepository.save(book);
        }

        // Clear cart
        cartRepository.deleteByUser(user);

        return order;
    }

    @Transactional
    public Order createOrder(User user) {
        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Calculate total amount and check stock
        double totalAmount = 0;
        for (Cart cartItem : cartItems) {
            Book book = cartItem.getBook();
            if (book.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for book: " + book.getTitle());
            }
            totalAmount += book.getPrice() * cartItem.getQuantity();
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order = orderRepository.save(order);

        // Create order items and update book stock
        for (Cart cartItem : cartItems) {
            Book book = cartItem.getBook();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(book.getPrice());
            orderItemRepository.save(orderItem);
            
            // Update book stock
            book.setStock(book.getStock() - cartItem.getQuantity());
            bookRepository.save(book);
        }

        // Clear cart
        cartRepository.deleteByUser(user);

        return order;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    public Order getOrderById(Long orderId, User user) {
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    public List<Order> getOrdersByStatus(User user, String status) {
        return orderRepository.findByUserAndStatus(user, status);
    }

    @Transactional
    public Order cancelOrder(Long orderId, User user) {
        Order order = getOrderById(orderId, user);
        
        if (!"PENDING".equals(order.getStatus()) && !"CONFIRMED".equals(order.getStatus())) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        // Restore book stock
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem item : orderItems) {
            Book book = item.getBook();
            book.setStock(book.getStock() + item.getQuantity());
            bookRepository.save(book);
        }

        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, User user, String newStatus) {
        Order order = getOrderById(orderId, user);
        
        // Validate status transition
        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new RuntimeException("Invalid status transition from " + order.getStatus() + " to " + newStatus);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public List<Order> getRecentOrders(User user, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findTopByUserOrderByOrderDateDesc(user, pageable);
    }

    public Map<String, Object> getOrderSummary(User user) {
        List<Order> allOrders = getUserOrders(user);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalOrders", allOrders.size());
        summary.put("totalSpent", allOrders.stream().mapToDouble(Order::getTotalAmount).sum());
        summary.put("pendingOrders", allOrders.stream().filter(o -> "PENDING".equals(o.getStatus())).count());
        summary.put("completedOrders", allOrders.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count());
        summary.put("cancelledOrders", allOrders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count());
        
        return summary;
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case "PENDING":
                return "CONFIRMED".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "CONFIRMED":
                return "SHIPPED".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "SHIPPED":
                return "DELIVERED".equals(newStatus);
            case "DELIVERED":
                return "COMPLETED".equals(newStatus);
            case "CANCELLED":
            case "COMPLETED":
                return false; // Terminal states
            default:
                return false;
        }
    }
}