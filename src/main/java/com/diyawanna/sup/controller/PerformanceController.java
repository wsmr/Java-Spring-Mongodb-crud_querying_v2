package com.diyawanna.sup.controller;

import com.diyawanna.sup.service.PerformanceMonitoringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Performance monitoring controller
 * 
 * This controller provides:
 * - Performance metrics endpoints
 * - Cache management operations
 * - System health monitoring
 * - Administrative operations
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "*")
public class PerformanceController {

    @Autowired
    private PerformanceMonitoringService performanceService;

    /**
     * Get comprehensive performance metrics
     * GET /api/performance/metrics
     */
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPerformanceMetrics() {
        try {
            Map<String, Object> metrics = performanceService.getPerformanceMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve performance metrics");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get cache hit ratio for specific cache
     * GET /api/performance/cache/{cacheName}/hit-ratio
     */
    @GetMapping("/cache/{cacheName}/hit-ratio")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCacheHitRatio(@PathVariable String cacheName) {
        try {
            double hitRatio = performanceService.getCacheHitRatio(cacheName);
            Map<String, Object> response = new HashMap<>();
            response.put("cacheName", cacheName);
            response.put("hitRatio", hitRatio);
            response.put("hitRatioPercent", hitRatio * 100);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve cache hit ratio");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Clear all caches
     * DELETE /api/performance/cache/all
     */
    @DeleteMapping("/cache/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> clearAllCaches() {
        try {
            performanceService.clearAllCaches();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "All caches cleared successfully");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to clear caches");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Clear specific cache
     * DELETE /api/performance/cache/{cacheName}
     */
    @DeleteMapping("/cache/{cacheName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> clearCache(@PathVariable String cacheName) {
        try {
            performanceService.clearCache(cacheName);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cache cleared successfully");
            response.put("cacheName", cacheName);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to clear cache");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Warm up caches
     * POST /api/performance/cache/warmup
     */
    @PostMapping("/cache/warmup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> warmUpCaches() {
        try {
            performanceService.warmUpCaches();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cache warm-up initiated");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to warm up caches");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get system health status
     * GET /api/performance/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> getSystemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            
            // Basic health indicators
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            double memoryUsage = (double) usedMemory / runtime.maxMemory();
            
            health.put("status", memoryUsage < 0.9 ? "HEALTHY" : "WARNING");
            health.put("memoryUsagePercent", memoryUsage * 100);
            health.put("timestamp", LocalDateTime.now());
            health.put("uptime", "Available via JMX");
            
            if (memoryUsage < 0.9) {
                return ResponseEntity.ok(health);
            } else {
                return ResponseEntity.status(503).body(health);
            }
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("error", "Health check failed");
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(503).body(error);
        }
    }

    /**
     * Force garbage collection (use with caution)
     * POST /api/performance/gc
     */
    @PostMapping("/gc")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> forceGarbageCollection() {
        try {
            long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.gc();
            Thread.sleep(100); // Give GC time to run
            long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Garbage collection requested");
            response.put("memoryBeforeMB", beforeMemory / (1024 * 1024));
            response.put("memoryAfterMB", afterMemory / (1024 * 1024));
            response.put("memoryFreedMB", (beforeMemory - afterMemory) / (1024 * 1024));
            response.put("timestamp", LocalDateTime.now());
            response.put("warning", "Forced GC should be used sparingly in production");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to perform garbage collection");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

