package com.diyawanna.sup.controller;

import com.diyawanna.sup.service.AuthenticationAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Admin controller for system management and configuration
 * 
 * This controller provides administrative endpoints for:
 * - Managing authentication rate limiting
 * - Clearing authentication attempts
 * - System configuration management
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AuthenticationAttemptService attemptService;

    /**
     * Get authentication rate limiting configuration
     * GET /api/admin/auth/config
     */
    @GetMapping("/auth/config")
    public ResponseEntity<?> getAuthConfig() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var config = attemptService.getConfiguration();
            response.put("success", true);
            response.put("config", Map.of(
                "enabled", config.isEnabled(),
                "maxAttempts", config.getMaxAttempts(),
                "lockoutDurationMinutes", config.getLockoutDurationMinutes(),
                "currentTrackedIdentifiers", config.getCurrentTrackedIdentifiers()
            ));
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to retrieve authentication configuration");
            response.put("message", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Clear all authentication attempts
     * POST /api/admin/auth/clear-all
     */
    @PostMapping("/auth/clear-all")
    public ResponseEntity<?> clearAllAttempts() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            attemptService.clearAllAttempts();
            response.put("success", true);
            response.put("message", "All authentication attempts cleared successfully");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to clear authentication attempts");
            response.put("message", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Clear authentication attempts for specific identifier
     * POST /api/admin/auth/clear/{identifier}
     */
    @PostMapping("/auth/clear/{identifier}")
    public ResponseEntity<?> clearAttempts(@PathVariable String identifier) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            attemptService.clearAttempts(identifier);
            response.put("success", true);
            response.put("message", "Authentication attempts cleared for identifier: " + identifier);
            response.put("identifier", identifier);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to clear authentication attempts");
            response.put("message", e.getMessage());
            response.put("identifier", identifier);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get authentication attempt count for specific identifier
     * GET /api/admin/auth/attempts/{identifier}
     */
    @GetMapping("/auth/attempts/{identifier}")
    public ResponseEntity<?> getAttemptCount(@PathVariable String identifier) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int attemptCount = attemptService.getAttemptCount(identifier);
            boolean isBlocked = attemptService.isBlocked(identifier);
            long remainingLockoutTime = attemptService.getRemainingLockoutTime(identifier);
            
            response.put("success", true);
            response.put("identifier", identifier);
            response.put("attemptCount", attemptCount);
            response.put("isBlocked", isBlocked);
            response.put("remainingLockoutTime", remainingLockoutTime);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to retrieve attempt information");
            response.put("message", e.getMessage());
            response.put("identifier", identifier);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get system configuration information
     * GET /api/admin/config
     */
    @GetMapping("/config")
    public ResponseEntity<?> getSystemConfig() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> config = new HashMap<>();
            
            // Authentication configuration
            var authConfig = attemptService.getConfiguration();
            config.put("authentication", Map.of(
                "rateLimitingEnabled", authConfig.isEnabled(),
                "maxAttempts", authConfig.getMaxAttempts(),
                "lockoutDurationMinutes", authConfig.getLockoutDurationMinutes(),
                "currentTrackedIdentifiers", authConfig.getCurrentTrackedIdentifiers()
            ));
            
            // Application configuration
            config.put("application", Map.of(
                "name", "Diyawanna Sup Backend",
                "version", "1.0.0",
                "environment", System.getProperty("spring.profiles.active", "production")
            ));
            
            response.put("success", true);
            response.put("config", config);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to retrieve system configuration");
            response.put("message", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}

