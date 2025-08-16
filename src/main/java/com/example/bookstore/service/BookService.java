package com.example.bookstore.service;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Page<Book> searchBooks(String query, Pageable pageable) {
        return bookRepository.searchBooks(query, pageable);
    }

    public Page<Book> searchBooksByTitle(String title, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public Page<Book> searchBooksByAuthor(String author, Pageable pageable) {
        return bookRepository.findByAuthorContainingIgnoreCase(author, pageable);
    }

    public Page<Book> getBooksByCategory(Long categoryId, Pageable pageable) {
        return bookRepository.findByCategoryId(categoryId, pageable);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public Book addBook(Book book, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        book.setCategory(category);
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id);
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setPrice(bookDetails.getPrice());
        book.setStock(bookDetails.getStock());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }
}