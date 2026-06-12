package com.product.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product.entity.User;
import com.product.repository.UserRepository;

@RestController
public class ProfileController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/api/v1.0/profile")
    public Map<String, Object> getProfile(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Map<String, Object> profile = new HashMap<>();

        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole());

        return profile;
    }
}