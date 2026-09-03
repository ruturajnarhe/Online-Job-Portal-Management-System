package com.jsp.ojpms.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT token
    public String generateToken(String email, String role) {

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                    new Date(System.currentTimeMillis() + expirationMs)
                )
                .signWith(getSigningKey())
                .compact();
    }

    // Extract email from JWT
    public String extractEmail(String token) {

        return getClaims(token)
                .getSubject();
    }

    // Extract role from JWT
    public String extractRole(String token) {

        return getClaims(token)
                .get("role", String.class);
    }

    // Check whether JWT is expired
    public boolean isTokenExpired(String token) {

        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // Validate JWT
    public boolean isTokenValid(String token, String email) {

        String tokenEmail = extractEmail(token);

        return tokenEmail.equals(email)
                && !isTokenExpired(token);
    }

    // Get all claims
    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}