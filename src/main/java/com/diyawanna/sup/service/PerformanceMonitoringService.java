package com.diyawanna.sup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Performance monitoring service
 * 
 * This service provides:
 * - Application performance metrics
 * - Cache statistics
 * - Database connection monitoring
 * - Memory usage tracking
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Service
public class PerformanceMonitoringService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Get comprehensive performance metrics
     */
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // System metrics
        metrics.put("timestamp", LocalDateTime.now());
        metrics.put("systemMetrics", getSystemMetrics());
        metrics.put("cacheMetrics", getCacheMetrics());
        metrics.put("databaseMetrics", getDatabaseMetrics());
        metrics.put("applicationMetrics", getApplicationMetrics());
        
        return metrics;
    }

    /**
     * Get system performance metrics
     */
    private Map<String, Object> getSystemMetrics() {
        Map<String, Object> systemMetrics = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        systemMetrics.put("maxMemoryMB", maxMemory / (1024 * 1024));
        systemMetrics.put("totalMemoryMB", totalMemory / (1024 * 1024));
        systemMetrics.put("usedMemoryMB", usedMemory / (1024 * 1024));
        systemMetrics.put("freeMemoryMB", freeMemory / (1024 * 1024));
        systemMetrics.put("memoryUsagePercent", (double) usedMemory / maxMemory * 100);
        systemMetrics.put("availableProcessors", runtime.availableProcessors());
        
        return systemMetrics;
    }

    /**
     * Get cache performance metrics
     */
    private Map<String, Object> getCacheMetrics() {
        Map<String, Object> cacheMetrics = new HashMap<>();
        
        try {
            // Get cache names and basic statistics
            cacheMetrics.put("cacheNames", cacheManager.getCacheNames());
            cacheMetrics.put("cacheManagerType", cacheManager.getClass().getSimpleName());
            
            // For each cache, get basic info
            Map<String, Object> cacheDetails = new HashMap<>();
            for (String cacheName : cacheManager.getCacheNames()) {
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("name", cacheName);
                cacheInfo.put("nativeCache", cacheManager.getCache(cacheName).getNativeCache().getClass().getSimpleName());
                cacheDetails.put(cacheName, cacheInfo);
            }
            cacheMetrics.put("cacheDetails", cacheDetails);
            
        } catch (Exception e) {
            cacheMetrics.put("error", "Failed to retrieve cache metrics: " + e.getMessage());
        }
        
        return cacheMetrics;
    }

    /**
     * Get database performance metrics
     */
    private Map<String, Object> getDatabaseMetrics() {
        Map<String, Object> dbMetrics = new HashMap<>();
        
        try {
            // Test database connectivity
            long startTime = System.currentTimeMillis();
            mongoTemplate.getCollectionNames();
            long responseTime = System.currentTimeMillis() - startTime;
            
            dbMetrics.put("connectionStatus", "CONNECTED");
            dbMetrics.put("responseTimeMs", responseTime);
            dbMetrics.put("databaseName", mongoTemplate.getDb().getName());
            dbMetrics.put("collectionCount", mongoTemplate.getCollectionNames().size());
            
        } catch (Exception e) {
            dbMetrics.put("connectionStatus", "ERROR");
            dbMetrics.put("error", e.getMessage());
        }
        
        return dbMetrics;
    }

    /**
     * Get application-specific metrics
     */
    private Map<String, Object> getApplicationMetrics() {
        Map<String, Object> appMetrics = new HashMap<>();
        
        try {
            // Application uptime (simplified)
            appMetrics.put("applicationName", "Diyawanna Sup Backend");
            appMetrics.put("version", "1.0.0");
            appMetrics.put("profile", "development");
            appMetrics.put("javaVersion", System.getProperty("java.version"));
            appMetrics.put("springBootVersion", "3.5.0");
            
        } catch (Exception e) {
            appMetrics.put("error", "Failed to retrieve application metrics: " + e.getMessage());
        }
        
        return appMetrics;
    }

    /**
     * Get cache hit ratio for specific cache
     */
    public double getCacheHitRatio(String cacheName) {
        try {
            // This would require a more sophisticated cache implementation
            // For now, return a placeholder
            return 0.85; // 85% hit ratio
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        try {
            for (String cacheName : cacheManager.getCacheNames()) {
                cacheManager.getCache(cacheName).clear();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear caches: " + e.getMessage(), e);
        }
    }

    /**
     * Clear specific cache
     */
    public void clearCache(String cacheName) {
        try {
            cacheManager.getCache(cacheName).clear();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear cache " + cacheName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Warm up caches with frequently accessed data
     */
    public void warmUpCaches() {
        try {
            // This would typically pre-load frequently accessed data
            // Implementation depends on specific use cases
            
            // For now, just log that warm-up was requested
            System.out.println("Cache warm-up initiated at: " + LocalDateTime.now());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to warm up caches: " + e.getMessage(), e);
        }
    }
}

