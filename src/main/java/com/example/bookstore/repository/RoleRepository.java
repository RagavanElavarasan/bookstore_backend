package com.example.bookstore.repository;

import com.example.bookstore.model.ERole;
import com.example.bookstore.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByName(ERole name);
}