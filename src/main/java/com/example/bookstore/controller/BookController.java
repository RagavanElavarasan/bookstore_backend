package com.example.bookstore.controller;

import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Page<Book> getAllBooks(@PageableDefault(size = 10) Pageable pageable) {
        return bookService.getAllBooks(pageable);
    }

    @GetMapping("/search")
    public Page<Book> searchBooks(@RequestParam String query, @PageableDefault(size = 10) Pageable pageable) {
        return bookService.searchBooks(query, pageable);
    }

    @GetMapping("/search/title")
    public Page<Book> searchBooksByTitle(@RequestParam String title, @PageableDefault(size = 10) Pageable pageable) {
        return bookService.searchBooksByTitle(title, pageable);
    }

    @GetMapping("/search/author")
    public Page<Book> searchBooksByAuthor(@RequestParam String author, @PageableDefault(size = 10) Pageable pageable) {
        return bookService.searchBooksByAuthor(author, pageable);
    }

    @GetMapping("/category/{categoryId}")
    public Page<Book> getBooksByCategory(@PathVariable Long categoryId, @PageableDefault(size = 10) Pageable pageable) {
        return bookService.getBooksByCategory(categoryId, pageable);
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Book addBook(@RequestBody Book book, @RequestParam Long categoryId) {
        return bookService.addBook(book, categoryId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Book updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return bookService.updateBook(id, bookDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}