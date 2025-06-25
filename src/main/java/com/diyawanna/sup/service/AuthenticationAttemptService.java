package com.diyawanna.sup.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service to handle authentication attempt limiting
 * 
 * This service tracks failed authentication attempts and implements
 * rate limiting to prevent brute force attacks.
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Service
public class AuthenticationAttemptService {

    @Value("${auth.rate-limiting.enabled:true}")
    private boolean rateLimitingEnabled;

    @Value("${auth.rate-limiting.max-attempts:5}")
    private int maxAttempts;

    @Value("${auth.rate-limiting.lockout-duration-minutes:15}")
    private int lockoutDurationMinutes;

    @Value("${auth.rate-limiting.cleanup-interval-minutes:60}")
    private int cleanupIntervalMinutes;

    // Store failed attempts: key = username/IP, value = AttemptInfo
    private final ConcurrentMap<String, AttemptInfo> attemptCache = new ConcurrentHashMap<>();
    
    // Last cleanup time
    private LocalDateTime lastCleanup = LocalDateTime.now();

    /**
     * Record a failed authentication attempt
     */
    public void recordFailedAttempt(String identifier) {
        if (!rateLimitingEnabled) {
            return;
        }

        cleanupExpiredEntries();
        
        attemptCache.compute(identifier, (key, existing) -> {
            if (existing == null) {
                return new AttemptInfo(1, LocalDateTime.now());
            } else {
                // Reset if enough time has passed since last attempt
                if (ChronoUnit.MINUTES.between(existing.lastAttempt, LocalDateTime.now()) >= lockoutDurationMinutes) {
                    return new AttemptInfo(1, LocalDateTime.now());
                } else {
                    return new AttemptInfo(existing.attempts + 1, LocalDateTime.now());
                }
            }
        });
    }

    /**
     * Record a successful authentication (clears failed attempts)
     */
    public void recordSuccessfulAttempt(String identifier) {
        if (!rateLimitingEnabled) {
            return;
        }
        
        attemptCache.remove(identifier);
    }

    /**
     * Check if the identifier is currently blocked
     */
    public boolean isBlocked(String identifier) {
        if (!rateLimitingEnabled) {
            return false;
        }

        cleanupExpiredEntries();
        
        AttemptInfo info = attemptCache.get(identifier);
        if (info == null) {
            return false;
        }

        // Check if lockout period has expired
        if (ChronoUnit.MINUTES.between(info.lastAttempt, LocalDateTime.now()) >= lockoutDurationMinutes) {
            attemptCache.remove(identifier);
            return false;
        }

        return info.attempts >= maxAttempts;
    }

    /**
     * Get remaining lockout time in seconds
     */
    public long getRemainingLockoutTime(String identifier) {
        if (!rateLimitingEnabled) {
            return 0;
        }

        AttemptInfo info = attemptCache.get(identifier);
        if (info == null || info.attempts < maxAttempts) {
            return 0;
        }

        long minutesSinceLastAttempt = ChronoUnit.MINUTES.between(info.lastAttempt, LocalDateTime.now());
        long remainingMinutes = lockoutDurationMinutes - minutesSinceLastAttempt;
        
        return remainingMinutes > 0 ? remainingMinutes * 60 : 0;
    }

    /**
     * Get current attempt count for identifier
     */
    public int getAttemptCount(String identifier) {
        if (!rateLimitingEnabled) {
            return 0;
        }

        AttemptInfo info = attemptCache.get(identifier);
        return info != null ? info.attempts : 0;
    }

    /**
     * Clear all attempts (admin function)
     */
    public void clearAllAttempts() {
        attemptCache.clear();
    }

    /**
     * Clear attempts for specific identifier (admin function)
     */
    public void clearAttempts(String identifier) {
        attemptCache.remove(identifier);
    }

    /**
     * Get rate limiting configuration status
     */
    public RateLimitingConfig getConfiguration() {
        return new RateLimitingConfig(
            rateLimitingEnabled,
            maxAttempts,
            lockoutDurationMinutes,
            attemptCache.size()
        );
    }

    /**
     * Clean up expired entries to prevent memory leaks
     */
    private void cleanupExpiredEntries() {
        LocalDateTime now = LocalDateTime.now();
        
        // Only cleanup if enough time has passed
        if (ChronoUnit.MINUTES.between(lastCleanup, now) < cleanupIntervalMinutes) {
            return;
        }

        attemptCache.entrySet().removeIf(entry -> {
            AttemptInfo info = entry.getValue();
            return ChronoUnit.MINUTES.between(info.lastAttempt, now) >= lockoutDurationMinutes;
        });

        lastCleanup = now;
    }

    /**
     * Inner class to store attempt information
     */
    private static class AttemptInfo {
        final int attempts;
        final LocalDateTime lastAttempt;

        AttemptInfo(int attempts, LocalDateTime lastAttempt) {
            this.attempts = attempts;
            this.lastAttempt = lastAttempt;
        }
    }

    /**
     * Configuration information class
     */
    public static class RateLimitingConfig {
        private final boolean enabled;
        private final int maxAttempts;
        private final int lockoutDurationMinutes;
        private final int currentTrackedIdentifiers;

        public RateLimitingConfig(boolean enabled, int maxAttempts, int lockoutDurationMinutes, int currentTrackedIdentifiers) {
            this.enabled = enabled;
            this.maxAttempts = maxAttempts;
            this.lockoutDurationMinutes = lockoutDurationMinutes;
            this.currentTrackedIdentifiers = currentTrackedIdentifiers;
        }

        // Getters
        public boolean isEnabled() { return enabled; }
        public int getMaxAttempts() { return maxAttempts; }
        public int getLockoutDurationMinutes() { return lockoutDurationMinutes; }
        public int getCurrentTrackedIdentifiers() { return currentTrackedIdentifiers; }
    }
}

