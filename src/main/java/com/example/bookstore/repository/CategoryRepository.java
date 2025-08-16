package com.example.bookstore.repository;

import com.example.bookstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Basic CRUD operations are provided by JpaRepository
    // Add custom query methods here if needed
}