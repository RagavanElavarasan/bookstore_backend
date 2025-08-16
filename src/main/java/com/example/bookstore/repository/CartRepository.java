package com.example.bookstore.repository;

import com.example.bookstore.model.Cart;
import com.example.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    void deleteByUserAndBookId(User user, Long bookId);
    Optional<Cart> findByUserAndBookId(User user, Long bookId);
    void deleteByUser(User user);
}