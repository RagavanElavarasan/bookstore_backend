package com.example.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHashTest {

    @Test
    void printBcryptForAdmin() {
        String raw = "admin";
        String hash = new BCryptPasswordEncoder().encode(raw);
        System.out.println("BCrypt(admin)=" + hash);
    }
} 