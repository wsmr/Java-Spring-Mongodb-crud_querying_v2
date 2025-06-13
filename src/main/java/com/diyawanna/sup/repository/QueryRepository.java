package com.diyawanna.sup.repository;

import com.diyawanna.sup.entity.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Query repository interface for MongoDB operations
 * 
 * This repository provides:
 * - Basic CRUD operations
 * - Custom query methods for dynamic query management
 * - Category and type-based queries
 * - Caching-related queries
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Repository
public interface QueryRepository extends MongoRepository<Query, String> {

    /**
     * Find query by name
     */
    Optional<Query> findByName(String name);

    /**
     * Check if query name exists
     */
    boolean existsByName(String name);

    /**
     * Find all active queries
     */
    List<Query> findByActiveTrue();

    /**
     * Find queries by category
     */
    List<Query> findByCategoryAndActiveTrue(String category);

    /**
     * Find queries by type
     */
    List<Query> findByQueryTypeAndActiveTrue(String queryType);

    /**
     * Find queries by collection
     */
    List<Query> findByCollectionAndActiveTrue(String collection);

    /**
     * Find cacheable queries
     */
    List<Query> findByCacheableAndActiveTrue(boolean cacheable);

    /**
     * Find queries by category and type
     */
    List<Query> findByCategoryAndQueryTypeAndActiveTrue(String category, String queryType);

    /**
     * Find queries by collection and type
     */
    List<Query> findByCollectionAndQueryTypeAndActiveTrue(String collection, String queryType);

    /**
     * Custom query to find queries by name pattern
     */
    @org.springframework.data.mongodb.repository.Query("{'name': {$regex: ?0, $options: 'i'}, 'active': true}")
    List<Query> findByNameContainingIgnoreCase(String name);

    /**
     * Custom query to find queries by description pattern
     */
    @org.springframework.data.mongodb.repository.Query("{'description': {$regex: ?0, $options: 'i'}, 'active': true}")
    List<Query> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Find queries created by specific user
     */
    List<Query> findByCreatedByAndActiveTrue(String createdBy);

    /**
     * Find queries last modified by specific user
     */
    List<Query> findByLastModifiedByAndActiveTrue(String lastModifiedBy);

    /**
     * Find queries with cache timeout greater than specified value
     */
    List<Query> findByCacheTimeoutSecondsGreaterThanAndActiveTrue(Integer timeoutSeconds);

    /**
     * Count active queries
     */
    long countByActiveTrue();

    /**
     * Count queries by category
     */
    long countByCategoryAndActiveTrue(String category);

    /**
     * Count queries by type
     */
    long countByQueryTypeAndActiveTrue(String queryType);

    /**
     * Count cacheable queries
     */
    long countByCacheableAndActiveTrue(boolean cacheable);

    /**
     * Find queries by multiple categories
     */
    List<Query> findByCategoryInAndActiveTrue(List<String> categories);

    /**
     * Find queries by multiple types
     */
    List<Query> findByQueryTypeInAndActiveTrue(List<String> queryTypes);
}

