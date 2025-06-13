package com.diyawanna.sup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Main Spring Boot application class for Diyawanna Sup Backend
 * 
 * This application provides:
 * - RESTful APIs for CRUD operations
 * - JWT-based authentication and authorization
 * - MongoDB Atlas integration
 * - Dynamic query processing
 * - Performance optimizations with caching
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableMongoAuditing
public class DiyawannaSupBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiyawannaSupBackendApplication.class, args);
    }
}

