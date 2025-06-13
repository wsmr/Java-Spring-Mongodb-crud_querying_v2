package com.diyawanna.sup.controller;

import com.diyawanna.sup.dto.LoginRequest;
import com.diyawanna.sup.dto.LoginResponse;
import com.diyawanna.sup.dto.RegisterRequest;
import com.diyawanna.sup.entity.User;
import com.diyawanna.sup.service.AuthenticationService;
import com.diyawanna.sup.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller for login, registration, and token management
 * 
 * This controller provides:
 * - User login with JWT token generation
 * - User registration
 * - Token refresh
 * - Token validation
 * - Password change
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * User login endpoint
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authenticationService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * User registration endpoint
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = authenticationService.register(registerRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("name", user.getName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Token refresh endpoint
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader(jwtUtil.getHeader());
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token required");
                error.put("message", "Authorization header with valid token is required");
                return ResponseEntity.badRequest().body(error);
            }

            LoginResponse response = authenticationService.refreshToken(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token refresh failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Token validation endpoint
     * GET /api/auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader(jwtUtil.getHeader());
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token required");
                error.put("message", "Authorization header with valid token is required");
                return ResponseEntity.badRequest().body(error);
            }

            User user = authenticationService.validateTokenAndGetUser(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("remainingTime", jwtUtil.getRemainingTime(token));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("valid", false);
            error.put("error", "Token validation failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Change password endpoint
     * POST /api/auth/change-password
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, 
                                          HttpServletRequest httpRequest) {
        try {
            String authHeader = httpRequest.getHeader(jwtUtil.getHeader());
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token required");
                error.put("message", "Authorization header with valid token is required");
                return ResponseEntity.badRequest().body(error);
            }

            String username = jwtUtil.extractUsername(token);
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid request");
                error.put("message", "Both oldPassword and newPassword are required");
                return ResponseEntity.badRequest().body(error);
            }

            authenticationService.changePassword(username, oldPassword, newPassword);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Password change failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get current user info endpoint
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader(jwtUtil.getHeader());
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token required");
                error.put("message", "Authorization header with valid token is required");
                return ResponseEntity.badRequest().body(error);
            }

            User user = authenticationService.validateTokenAndGetUser(token);
            
            // Remove sensitive information
            user.setPassword(null);
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get user info");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Health check endpoint for authentication service
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Authentication Service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}

