package com.product.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JWTUtil {

    @Value("${jwt.signature}")
    private String jwtSignature;

    private SecretKey secret_key;

    // ✅ INITIALIZE SECRET KEY
    @PostConstruct
    public void assignKey() {
        secret_key = Keys.hmacShaKeyFor(jwtSignature.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ CREATE TOKEN
    public String createJwtToken(String username, List<String> roles) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (60 * 60 * 1000))) // 1 hour
                .signWith(secret_key)
                .compact();
    }

    // ✅ EXTRACT CLAIMS (SAFE)
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secret_key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✅ EXTRACT USERNAME
    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            return null; // ❗ prevents crash
        }
    }

    // ✅ CHECK EXPIRY
    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // ✅ VALIDATE TOKEN
    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return (extractedUsername != null &&
                    extractedUsername.equals(username) &&
                    !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}