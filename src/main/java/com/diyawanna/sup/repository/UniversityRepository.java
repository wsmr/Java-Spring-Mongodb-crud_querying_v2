package com.diyawanna.sup.repository;

import com.diyawanna.sup.entity.University;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * University repository interface for MongoDB operations
 * 
 * This repository provides:
 * - Basic CRUD operations
 * - Custom query methods for university management
 * - Faculty relationship queries
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Repository
public interface UniversityRepository extends MongoRepository<University, String> {

    /**
     * Find university by name
     */
    Optional<University> findByName(String name);

    /**
     * Check if university name exists
     */
    boolean existsByName(String name);

    /**
     * Find all active universities
     */
    List<University> findByActiveTrue();

    /**
     * Find universities by location
     */
    List<University> findByLocationContainingIgnoreCaseAndActiveTrue(String location);

    /**
     * Find universities that have specific faculty
     */
    List<University> findByFacultiesContainingAndActiveTrue(String facultyId);

    /**
     * Custom query to find universities by name pattern
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}, 'active': true}")
    List<University> findByNameContainingIgnoreCase(String name);

    /**
     * Custom query to find universities by description pattern
     */
    @Query("{'description': {$regex: ?0, $options: 'i'}, 'active': true}")
    List<University> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Find universities with website
     */
    @Query("{'website': {$exists: true, $ne: null}, 'active': true}")
    List<University> findUniversitiesWithWebsite();

    /**
     * Count active universities
     */
    long countByActiveTrue();

    /**
     * Count universities by location
     */
    long countByLocationContainingIgnoreCaseAndActiveTrue(String location);
}

