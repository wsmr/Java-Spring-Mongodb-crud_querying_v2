package com.diyawanna.sup.controller;

import com.diyawanna.sup.service.SystemHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for application monitoring
 * 
 * This controller provides multiple health check endpoints:
 * - Basic health check for load balancers
 * - Detailed system health with comprehensive metrics
 * - Application information and status
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    @Autowired
    private SystemHealthService systemHealthService;

    /**
     * Basic health check endpoint (for load balancers)
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = systemHealthService.getBasicHealth();
        return ResponseEntity.ok(response);
    }

    /**
     * Detailed system health check endpoint
     * GET /api/health/detailed
     */
    @GetMapping("/health/detailed")
    public ResponseEntity<?> detailedHealth() {
        try {
            Map<String, Object> response = systemHealthService.getSystemHealth();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "DOWN");
            errorResponse.put("error", "Failed to retrieve system health");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(503).body(errorResponse);
        }
    }

    /**
     * System status endpoint (lightweight)
     * GET /api/status
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Diyawanna Sup Backend");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * Application information endpoint
     * GET /api/info
     */
    @GetMapping("/info")
    public ResponseEntity<?> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Diyawanna Sup Backend");
        response.put("version", "1.0.0");
        response.put("description", "Spring Boot backend service for Diyawanna Sup application");
        response.put("features", new String[]{
            "JWT Authentication",
            "MongoDB Integration", 
            "Rate Limiting",
            "Comprehensive Health Monitoring",
            "CRUD Operations",
            "Dynamic Query Processing",
            "Caching Support"
        });
        response.put("endpoints", new String[]{
            "/api/auth/*",
            "/api/users/*",
            "/api/universities/*",
            "/api/faculties/*",
            "/api/carts/*",
            "/api/queries/*",
            "/api/health/*"
        });
        response.put("documentation", "API documentation available at /swagger-ui/");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * Root endpoint
     * GET /api/
     */
    @GetMapping("/")
    public ResponseEntity<?> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Diyawanna Sup Backend API");
        response.put("version", "1.0.0");
        response.put("status", "UP");
        response.put("endpoints", Map.of(
            "health", "/api/health",
            "detailedHealth", "/api/health/detailed",
            "info", "/api/info",
            "status", "/api/status",
            "authentication", "/api/auth/*",
            "users", "/api/users/*",
            "documentation", "/swagger-ui/"
        ));
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * Ping endpoint (minimal response for monitoring)
     * GET /api/ping
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}

