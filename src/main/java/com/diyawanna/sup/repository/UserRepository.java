package com.diyawanna.sup.repository;

import com.diyawanna.sup.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * User repository interface for MongoDB operations
 * 
 * This repository provides:
 * - Basic CRUD operations
 * - Custom query methods for user management
 * - Authentication-related queries
 * - Pagination support
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by username (used for authentication)
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all active users
     */
    List<User> findByActiveTrue();

    /**
     * Find all active users with pagination
     */
    Page<User> findByActiveTrue(Pageable pageable);

    /**
     * Find users by university
     */
    List<User> findByUniversityAndActiveTrue(String university);

    /**
     * Find users by university with pagination
     */
    Page<User> findByUniversityAndActiveTrue(String university, Pageable pageable);

    /**
     * Find users by age range
     */
    List<User> findByAgeBetweenAndActiveTrue(Integer minAge, Integer maxAge);

    /**
     * Find users by work
     */
    List<User> findByWorkContainingIgnoreCaseAndActiveTrue(String work);

    /**
     * Find users by school
     */
    List<User> findBySchoolContainingIgnoreCaseAndActiveTrue(String school);

    /**
     * Custom query to find users by name pattern
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}, 'active': true}")
    List<User> findByNameContainingIgnoreCase(String name);

    /**
     * Custom query to find users created after a specific date
     */
    @Query("{'createdAt': {$gte: ?0}, 'active': true}")
    List<User> findUsersCreatedAfter(java.time.LocalDateTime date);

    /**
     * Count active users
     */
    long countByActiveTrue();

    /**
     * Count users by university
     */
    long countByUniversityAndActiveTrue(String university);

    /**
     * Count all users (including inactive)
     */
    long count();
}

