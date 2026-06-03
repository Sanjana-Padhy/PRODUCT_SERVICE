package com.product.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.product.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    // 🔥 LAZY FIX → breaks circular dependency
    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    // ===============================
    // 🔐 MAIN FILTER
    // ===============================
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // ===============================
        // 🔹 EXTRACT TOKEN
        // ===============================
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            jwt = authHeader.substring(7).trim();

            try {
                // 🔥 prevent invalid token crash
                if (jwt.isEmpty() || jwt.contains(" ")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                username = jwtUtil.extractUsername(jwt);

            } catch (Exception e) {
                System.out.println("❌ Invalid JWT: " + e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
        }

        // ===============================
        // 🔹 VALIDATE & AUTHENTICATE
        // ===============================
        if (username != null &&
            SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            try {
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            } catch (Exception e) {
                System.out.println("❌ Token validation failed: " + e.getMessage());
            }
        }

        // ===============================
        // 🔹 CONTINUE REQUEST
        // ===============================
        filterChain.doFilter(request, response);
    }

    // ===============================
    // 🔥 SKIP FILTER FOR PUBLIC APIs
    // ===============================
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {

        String path = request.getRequestURI();

        return path.startsWith("/api/v3/auth/login")
                || path.startsWith("/api/v2/user")
                || path.startsWith("/login.html")
                || path.startsWith("/register.html")
                || path.startsWith("/otp.html")
                || path.startsWith("/style.css")
                || path.startsWith("/script.js");
    }
}