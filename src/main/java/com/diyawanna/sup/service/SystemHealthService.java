package com.diyawanna.sup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * System health monitoring service
 * 
 * This service provides comprehensive system health information including:
 * - Memory usage (RAM, Heap, Non-Heap)
 * - CPU information and load
 * - Disk space information
 * - JVM runtime information
 * - Database connectivity status
 * - Application uptime
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Service
public class SystemHealthService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AuthenticationAttemptService attemptService;

    /**
     * Get comprehensive system health information
     */
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        health.put("application", getApplicationInfo());
        health.put("system", getSystemInfo());
        health.put("memory", getMemoryInfo());
        health.put("cpu", getCpuInfo());
        health.put("disk", getDiskInfo());
        health.put("database", getDatabaseInfo());
        health.put("security", getSecurityInfo());
        health.put("jvm", getJvmInfo());
        
        return health;
    }

    /**
     * Get basic system health status
     */
    public Map<String, Object> getBasicHealth() {
        Map<String, Object> health = new HashMap<>();
        
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        health.put("application", "Diyawanna Sup Backend");
        health.put("version", "1.0.0");
        
        return health;
    }

    /**
     * Get application information
     */
    private Map<String, Object> getApplicationInfo() {
        Map<String, Object> app = new HashMap<>();
        
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        
        app.put("name", "Diyawanna Sup Backend");
        app.put("version", "1.0.0");
        app.put("uptime", formatUptime(runtimeBean.getUptime()));
        app.put("startTime", LocalDateTime.now().minusSeconds(runtimeBean.getUptime() / 1000)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return app;
    }

    /**
     * Get system information
     */
    private Map<String, Object> getSystemInfo() {
        Map<String, Object> system = new HashMap<>();
        
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        system.put("os", osBean.getName());
        system.put("version", osBean.getVersion());
        system.put("architecture", osBean.getArch());
        system.put("processors", osBean.getAvailableProcessors());
        
        // Try to get additional system info if available
        try {
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = 
                    (com.sun.management.OperatingSystemMXBean) osBean;
                
                system.put("totalPhysicalMemory", formatBytes(sunOsBean.getTotalPhysicalMemorySize()));
                system.put("freePhysicalMemory", formatBytes(sunOsBean.getFreePhysicalMemorySize()));
                system.put("totalSwapSpace", formatBytes(sunOsBean.getTotalSwapSpaceSize()));
                system.put("freeSwapSpace", formatBytes(sunOsBean.getFreeSwapSpaceSize()));
            }
        } catch (Exception e) {
            system.put("extendedInfo", "Not available");
        }
        
        return system;
    }

    /**
     * Get memory information
     */
    private Map<String, Object> getMemoryInfo() {
        Map<String, Object> memory = new HashMap<>();
        
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        Runtime runtime = Runtime.getRuntime();
        
        // JVM Memory
        Map<String, Object> jvmMemory = new HashMap<>();
        jvmMemory.put("total", formatBytes(runtime.totalMemory()));
        jvmMemory.put("free", formatBytes(runtime.freeMemory()));
        jvmMemory.put("used", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        jvmMemory.put("max", formatBytes(runtime.maxMemory()));
        
        // Heap Memory
        Map<String, Object> heapMemory = new HashMap<>();
        heapMemory.put("used", formatBytes(memoryBean.getHeapMemoryUsage().getUsed()));
        heapMemory.put("committed", formatBytes(memoryBean.getHeapMemoryUsage().getCommitted()));
        heapMemory.put("max", formatBytes(memoryBean.getHeapMemoryUsage().getMax()));
        
        // Non-Heap Memory
        Map<String, Object> nonHeapMemory = new HashMap<>();
        nonHeapMemory.put("used", formatBytes(memoryBean.getNonHeapMemoryUsage().getUsed()));
        nonHeapMemory.put("committed", formatBytes(memoryBean.getNonHeapMemoryUsage().getCommitted()));
        nonHeapMemory.put("max", formatBytes(memoryBean.getNonHeapMemoryUsage().getMax()));
        
        memory.put("jvm", jvmMemory);
        memory.put("heap", heapMemory);
        memory.put("nonHeap", nonHeapMemory);
        
        return memory;
    }

    /**
     * Get CPU information
     */
    private Map<String, Object> getCpuInfo() {
        Map<String, Object> cpu = new HashMap<>();
        
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        cpu.put("processors", osBean.getAvailableProcessors());
        cpu.put("systemLoadAverage", osBean.getSystemLoadAverage());
        
        // Try to get additional CPU info if available
        try {
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = 
                    (com.sun.management.OperatingSystemMXBean) osBean;
                
                cpu.put("processCpuLoad", String.format("%.2f%%", sunOsBean.getProcessCpuLoad() * 100));
                cpu.put("systemCpuLoad", String.format("%.2f%%", sunOsBean.getSystemCpuLoad() * 100));
                cpu.put("processCpuTime", formatNanoseconds(sunOsBean.getProcessCpuTime()));
            }
        } catch (Exception e) {
            cpu.put("extendedInfo", "Not available");
        }
        
        return cpu;
    }

    /**
     * Get disk information
     */
    private Map<String, Object> getDiskInfo() {
        Map<String, Object> disk = new HashMap<>();
        
        File root = new File("/");
        
        disk.put("totalSpace", formatBytes(root.getTotalSpace()));
        disk.put("freeSpace", formatBytes(root.getFreeSpace()));
        disk.put("usableSpace", formatBytes(root.getUsableSpace()));
        disk.put("usedSpace", formatBytes(root.getTotalSpace() - root.getFreeSpace()));
        disk.put("usagePercentage", String.format("%.2f%%", 
            ((double)(root.getTotalSpace() - root.getFreeSpace()) / root.getTotalSpace()) * 100));
        
        return disk;
    }

    /**
     * Get database connectivity information
     */
    private Map<String, Object> getDatabaseInfo() {
        Map<String, Object> database = new HashMap<>();
        
        try {
            // Test MongoDB connection
            mongoTemplate.getCollection("test").estimatedDocumentCount();
            database.put("status", "UP");
            database.put("type", "MongoDB");
            database.put("database", mongoTemplate.getDb().getName());
        } catch (Exception e) {
            database.put("status", "DOWN");
            database.put("error", e.getMessage());
        }
        
        return database;
    }

    /**
     * Get security information
     */
    private Map<String, Object> getSecurityInfo() {
        Map<String, Object> security = new HashMap<>();
        
        try {
            var config = attemptService.getConfiguration();
            security.put("rateLimitingEnabled", config.isEnabled());
            security.put("maxAttempts", config.getMaxAttempts());
            security.put("lockoutDuration", config.getLockoutDurationMinutes() + " minutes");
            security.put("currentTrackedIdentifiers", config.getCurrentTrackedIdentifiers());
        } catch (Exception e) {
            security.put("status", "Configuration not available");
        }
        
        return security;
    }

    /**
     * Get JVM information
     */
    private Map<String, Object> getJvmInfo() {
        Map<String, Object> jvm = new HashMap<>();
        
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        
        jvm.put("name", runtimeBean.getVmName());
        jvm.put("vendor", runtimeBean.getVmVendor());
        jvm.put("version", runtimeBean.getVmVersion());
        jvm.put("specVersion", runtimeBean.getSpecVersion());
        jvm.put("inputArguments", runtimeBean.getInputArguments());
        
        return jvm;
    }

    /**
     * Format bytes to human readable format
     */
    private String formatBytes(long bytes) {
        if (bytes < 0) return "N/A";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * Format uptime to human readable format
     */
    private String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%d days, %d hours, %d minutes", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%d hours, %d minutes", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d minutes, %d seconds", minutes, seconds % 60);
        } else {
            return String.format("%d seconds", seconds);
        }
    }

    /**
     * Format nanoseconds to human readable format
     */
    private String formatNanoseconds(long nanoseconds) {
        if (nanoseconds < 0) return "N/A";
        
        double seconds = nanoseconds / 1_000_000_000.0;
        return String.format("%.2f seconds", seconds);
    }
}

