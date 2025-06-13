package com.diyawanna.sup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.Index;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

import com.diyawanna.sup.entity.User;
import com.diyawanna.sup.entity.University;
import com.diyawanna.sup.entity.Faculty;
import com.diyawanna.sup.entity.Cart;
import com.diyawanna.sup.entity.Query;

import jakarta.annotation.PostConstruct;

import java.util.concurrent.TimeUnit;

/**
 * MongoDB configuration class
 * 
 * This configuration class:
 * - Sets up MongoDB Atlas connection
 * - Configures connection pooling
 * - Creates database indexes for performance optimization
 * - Configures auditing
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.options.max-connection-pool-size:20}")
    private int maxConnectionPoolSize;

    @Value("${spring.data.mongodb.options.min-connection-pool-size:5}")
    private int minConnectionPoolSize;

    @Value("${spring.data.mongodb.options.max-connection-idle-time:30000}")
    private int maxConnectionIdleTime;

    @Value("${spring.data.mongodb.options.max-connection-life-time:120000}")
    private int maxConnectionLifeTime;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> 
                    builder.maxSize(maxConnectionPoolSize)
                           .minSize(minConnectionPoolSize)
                           .maxConnectionIdleTime(maxConnectionIdleTime, TimeUnit.MILLISECONDS)
                           .maxConnectionLifeTime(maxConnectionLifeTime, TimeUnit.MILLISECONDS))
                .build();
        
        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), getDatabaseName());
        
        // Configure to ignore _class field in documents
        MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();
        converter.setTypeMapper(null);
        
        return mongoTemplate;
    }

    /**
     * Create database indexes for performance optimization
     */
    @PostConstruct
    public void createIndexes() {
        try {
            MongoTemplate mongoTemplate = mongoTemplate();
            
            // User collection indexes
            IndexOperations userIndexOps = mongoTemplate.indexOps(User.class);
            userIndexOps.ensureIndex(new Index().on("username", org.springframework.data.domain.Sort.Direction.ASC).unique());
            userIndexOps.ensureIndex(new Index().on("email", org.springframework.data.domain.Sort.Direction.ASC));
            userIndexOps.ensureIndex(new Index().on("active", org.springframework.data.domain.Sort.Direction.ASC));
            userIndexOps.ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.DESC));
            
            // University collection indexes
            IndexOperations universityIndexOps = mongoTemplate.indexOps(University.class);
            universityIndexOps.ensureIndex(new Index().on("name", org.springframework.data.domain.Sort.Direction.ASC).unique());
            universityIndexOps.ensureIndex(new Index().on("active", org.springframework.data.domain.Sort.Direction.ASC));
            universityIndexOps.ensureIndex(new Index().on("location", org.springframework.data.domain.Sort.Direction.ASC));
            
            // Faculty collection indexes
            IndexOperations facultyIndexOps = mongoTemplate.indexOps(Faculty.class);
            facultyIndexOps.ensureIndex(new Index().on("name", org.springframework.data.domain.Sort.Direction.ASC));
            facultyIndexOps.ensureIndex(new Index().on("universityId", org.springframework.data.domain.Sort.Direction.ASC));
            facultyIndexOps.ensureIndex(new Index().on("active", org.springframework.data.domain.Sort.Direction.ASC));
            
            // Cart collection indexes
            IndexOperations cartIndexOps = mongoTemplate.indexOps(Cart.class);
            cartIndexOps.ensureIndex(new Index().on("userId", org.springframework.data.domain.Sort.Direction.ASC));
            cartIndexOps.ensureIndex(new Index().on("status", org.springframework.data.domain.Sort.Direction.ASC));
            cartIndexOps.ensureIndex(new Index().on("active", org.springframework.data.domain.Sort.Direction.ASC));
            cartIndexOps.ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.DESC));
            
            // Query collection indexes
            IndexOperations queryIndexOps = mongoTemplate.indexOps(Query.class);
            queryIndexOps.ensureIndex(new Index().on("name", org.springframework.data.domain.Sort.Direction.ASC).unique());
            queryIndexOps.ensureIndex(new Index().on("category", org.springframework.data.domain.Sort.Direction.ASC));
            queryIndexOps.ensureIndex(new Index().on("queryType", org.springframework.data.domain.Sort.Direction.ASC));
            queryIndexOps.ensureIndex(new Index().on("active", org.springframework.data.domain.Sort.Direction.ASC));
            queryIndexOps.ensureIndex(new Index().on("cacheable", org.springframework.data.domain.Sort.Direction.ASC));
            
            // Compound indexes for better query performance
            userIndexOps.ensureIndex(new Index().on("active", org.springframework.data.domain.Sort.Direction.ASC)
                                                .on("createdAt", org.springframework.data.domain.Sort.Direction.DESC));
            
            cartIndexOps.ensureIndex(new Index().on("userId", org.springframework.data.domain.Sort.Direction.ASC)
                                               .on("status", org.springframework.data.domain.Sort.Direction.ASC));
            
            facultyIndexOps.ensureIndex(new Index().on("universityId", org.springframework.data.domain.Sort.Direction.ASC)
                                                  .on("active", org.springframework.data.domain.Sort.Direction.ASC));
            
        } catch (Exception e) {
            // Log error but don't fail application startup
            System.err.println("Error creating MongoDB indexes: " + e.getMessage());
        }
    }
}

