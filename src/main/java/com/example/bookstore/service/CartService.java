package com.example.bookstore.service;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Cart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;

    public CartService(CartRepository cartRepository, BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
    }

    public List<Cart> getCartItems(User user) {
        return cartRepository.findByUser(user);
    }

    @Transactional
    public Cart addToCart(User user, Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        if (book.getStock() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        Cart existingCartItem = cartRepository.findByUserAndBookId(user, bookId).orElse(null);
        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            return cartRepository.save(existingCartItem);
        } else {
            Cart cartItem = new Cart();
            cartItem.setUser(user);
            cartItem.setBook(book);
            cartItem.setQuantity(quantity);
            return cartRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeFromCart(User user, Long bookId) {
        cartRepository.deleteByUserAndBookId(user, bookId);
    }

    @Transactional
    public void updateCartItem(User user, Long bookId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(user, bookId);
            return;
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        if (book.getStock() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        Cart cartItem = cartRepository.findByUserAndBookId(user, bookId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }
}