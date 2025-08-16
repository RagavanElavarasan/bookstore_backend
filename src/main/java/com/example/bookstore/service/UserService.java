package com.example.bookstore.service;

import com.example.bookstore.model.ERole;
import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.RoleRepository;
import com.example.bookstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String email, String password, Set<String> strRoles) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            // Handle potential duplicate roles by getting the first one
            Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_CUSTOMER is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if ("admin".equals(role)) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Error: ROLE_ADMIN is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Error: ROLE_CUSTOMER is not found."));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }
}