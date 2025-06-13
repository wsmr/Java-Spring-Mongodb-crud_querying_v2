package com.diyawanna.sup.repository;

import com.diyawanna.sup.entity.Faculty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Faculty repository interface for MongoDB operations
 * 
 * This repository provides:
 * - Basic CRUD operations
 * - Custom query methods for faculty management
 * - University relationship queries
 * - Subject-related queries
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Repository
public interface FacultyRepository extends MongoRepository<Faculty, String> {

    /**
     * Find faculty by name
     */
    Optional<Faculty> findByName(String name);

    /**
     * Find faculties by university ID
     */
    List<Faculty> findByUniversityIdAndActiveTrue(String universityId);

    /**
     * Find faculties by university name
     */
    List<Faculty> findByUniversityNameContainingIgnoreCaseAndActiveTrue(String universityName);

    /**
     * Find all active faculties
     */
    List<Faculty> findByActiveTrue();

    /**
     * Find faculties that have specific subject
     */
    List<Faculty> findBySubjectsContainingAndActiveTrue(String subject);

    /**
     * Find faculties by dean
     */
    List<Faculty> findByDeanContainingIgnoreCaseAndActiveTrue(String dean);

    /**
     * Custom query to find faculties by name pattern
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}, 'active': true}")
    List<Faculty> findByNameContainingIgnoreCase(String name);

    /**
     * Custom query to find faculties by description pattern
     */
    @Query("{'description': {$regex: ?0, $options: 'i'}, 'active': true}")
    List<Faculty> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Find faculties by university ID and name pattern
     */
    @Query("{'universityId': ?0, 'name': {$regex: ?1, $options: 'i'}, 'active': true}")
    List<Faculty> findByUniversityIdAndNameContainingIgnoreCase(String universityId, String name);

    /**
     * Find faculties with contact email
     */
    @Query("{'contactEmail': {$exists: true, $ne: null}, 'active': true}")
    List<Faculty> findFacultiesWithContactEmail();

    /**
     * Count active faculties
     */
    long countByActiveTrue();

    /**
     * Count faculties by university ID
     */
    long countByUniversityIdAndActiveTrue(String universityId);

    /**
     * Count faculties that have specific subject
     */
    long countBySubjectsContainingAndActiveTrue(String subject);

    /**
     * Check if faculty name exists for a university
     */
    boolean existsByNameAndUniversityIdAndActiveTrue(String name, String universityId);
}

