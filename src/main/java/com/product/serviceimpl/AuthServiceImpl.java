package com.product.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.product.entity.User;
import com.product.repository.UserRepository;
import com.product.service.AuthService;
import com.product.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    // ✅ LOGIN METHOD (FIXED)
    @Override
    public String authUserNamePasswordService(String username, String password) {

        // 🔍 Fetch user
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Invalid user name"));

        // 🔥 DEBUG (check console once)
        System.out.println("Entered Password: " + password);
        System.out.println("DB Password: " + user.getPassword());
        System.out.println("Password Match: " + passwordEncoder.matches(password, user.getPassword()));

        // ✅ FIX: Proper password check
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid user name or password";
        }

        // ✅ Generate JWT Token
        String token = jwtUtil.createJwtToken(
                user.getEmail(),
                List.of(user.getRole())
        );

        return token;
    }

    // ✅ LOGOUT METHOD
    @Override
    public String logOutService(HttpServletRequest request) {
        return "Logged out successfully";
    }
}