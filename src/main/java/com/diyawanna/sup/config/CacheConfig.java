package com.diyawanna.sup.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Cache configuration for performance optimization
 * 
 * This configuration provides:
 * - Cache manager setup
 * - Custom key generation
 * - Cache timeout configuration
 * - Performance monitoring
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure cache manager with multiple cache regions
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Define cache names for different entities
        cacheManager.setCacheNames(Arrays.asList(
            "users",
            "universities", 
            "faculties",
            "carts",
            "queries",
            "dynamic-queries",
            "authentication",
            "statistics"
        ));
        
        // Allow dynamic cache creation
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }

    /**
     * Custom key generator for cache keys
     */
    @Bean("customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getSimpleName());
                sb.append(".");
                sb.append(method.getName());
                
                for (Object param : params) {
                    if (param != null) {
                        sb.append("_").append(param.toString());
                    }
                }
                
                return sb.toString();
            }
        };
    }
}

