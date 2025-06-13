package com.diyawanna.sup.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT utility class for token generation, validation, and extraction
 * 
 * This utility provides:
 * - JWT token generation with custom claims
 * - Token validation and expiration checking
 * - Claims extraction (username, expiration, etc.)
 * - Token refresh functionality
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    /**
     * Generate secret key from the configured secret string
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from JWT token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from JWT token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * Check if JWT token is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generate JWT token for user
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Generate JWT token with custom claims
     */
    public String generateToken(String username, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        return createToken(claims, username);
    }

    /**
     * Create JWT token with claims and subject
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validate JWT token against user details
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate JWT token (basic validation)
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if token can be refreshed (not expired beyond refresh window)
     */
    public Boolean canTokenBeRefreshed(String token) {
        try {
            final Date expiration = extractExpiration(token);
            // Allow refresh if token expired within last 24 hours
            Date refreshCutoff = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            return expiration.after(refreshCutoff);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Refresh JWT token
     */
    public String refreshToken(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            claims.setIssuedAt(new Date());
            claims.setExpiration(new Date(System.currentTimeMillis() + expiration));
            
            return Jwts.builder()
                    .claims(claims)
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Cannot refresh token", e);
        }
    }

    /**
     * Extract token from Authorization header
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(prefix)) {
            return authHeader.substring(prefix.length()).trim();
        }
        return null;
    }

    /**
     * Get token expiration time in milliseconds
     */
    public Long getExpirationTime() {
        return expiration;
    }

    /**
     * Get JWT header name
     */
    public String getHeader() {
        return header;
    }

    /**
     * Get JWT token prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Extract custom claim from token
     */
    public Object extractCustomClaim(String token, String claimName) {
        final Claims claims = extractAllClaims(token);
        return claims.get(claimName);
    }

    /**
     * Get remaining time until token expiration in milliseconds
     */
    public Long getRemainingTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0L;
        }
    }
}

