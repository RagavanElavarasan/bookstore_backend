package com.example.bookstore.repository;

import com.example.bookstore.model.Order;
import com.example.bookstore.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    
    Optional<Order> findByIdAndUser(Long id, User user);
    
    List<Order> findByUserAndStatus(User user, String status);
    
    @Query("SELECT o FROM Order o WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findTopByUserOrderByOrderDateDesc(@Param("user") User user, Pageable pageable);
}