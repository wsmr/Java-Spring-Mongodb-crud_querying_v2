package com.diyawanna.sup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for application monitoring
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    /**
     * Basic health check endpoint
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "Diyawanna Sup Backend");
        response.put("version", "1.0.0");
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
        response.put("documentation", "API documentation available at /swagger-ui/");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}

