package com.cursojava.curso.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    @Value("${jwt.secret:mySecretKey}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private Long expiration; // 1 hour in milliseconds

    @Value("${jwt.issuer:curso-app}")
    private String issuer;

    @Value("${jwt.ttl-millis:3600000}")
    private Long ttlMillis;

    /**
     * Generate a secret key for JWT signing
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate JWT token for a user
     */
    public String generateToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttlMillis))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract email from JWT token
     */
    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Extract user ID from JWT token
     */
    public Long getUserIdFromToken(String token) {
        return getClaimsFromToken(token).get("userId", Long.class);
    }

    /**
     * Extract expiration date from JWT token
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    /**
     * Extract issuer from JWT token
     */
    public String getIssuerFromToken(String token) {
        return getClaimsFromToken(token).getIssuer();
    }

    /**
     * Extract all claims from JWT token
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if JWT token is expired
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Validate JWT token with issuer check
     */
    public boolean validateToken(String token, String email) {
        try {
            String tokenEmail = getEmailFromToken(token);
            String tokenIssuer = getIssuerFromToken(token);
            return (email.equals(tokenEmail) &&
                    issuer.equals(tokenIssuer) &&
                    !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate JWT token without email check but with issuer validation
     */
    public boolean validateToken(String token) {
        try {
            String tokenIssuer = getIssuerFromToken(token);
            return (issuer.equals(tokenIssuer) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract user ID from JWT token (this was the missing getKey method)
     */
    public String getKey(String token) {
        // Remove "Bearer " prefix if present
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Extract user ID from token and return as String
        Long userId = getUserIdFromToken(token);
        return userId != null ? userId.toString() : null;
    }

    /**
     * Extract clean token from Authorization header
     */
    public String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return authorizationHeader;
    }

    /**
     * Validate authorization header and return user ID if valid
     */
    public String validateAuthorizationHeader(String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                System.out.println("Invalid authorization header format: " + authorizationHeader);
                return null;
            }

            String token = extractToken(authorizationHeader);
            System.out.println("Extracted token: " + token.substring(0, Math.min(20, token.length())) + "...");

            if (validateToken(token)) {
                String userId = getKey(token); // Use the cleaned token
                System.out.println("Token validated successfully for user: " + userId);
                return userId;
            } else {
                System.out.println("Token validation failed");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error validating token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
