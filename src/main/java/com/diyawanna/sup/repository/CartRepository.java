package com.diyawanna.sup.repository;

import com.diyawanna.sup.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Cart repository interface for MongoDB operations
 * 
 * This repository provides:
 * - Basic CRUD operations
 * - Custom query methods for cart management
 * - User relationship queries
 * - Status and item-related queries
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    /**
     * Find carts by user ID
     */
    List<Cart> findByUserIdAndActiveTrue(String userId);

    /**
     * Find carts by user ID and status
     */
    List<Cart> findByUserIdAndStatusAndActiveTrue(String userId, String status);

    /**
     * Find cart by name and user ID
     */
    Optional<Cart> findByNameAndUserIdAndActiveTrue(String name, String userId);

    /**
     * Find all active carts
     */
    List<Cart> findByActiveTrue();

    /**
     * Find carts by status
     */
    List<Cart> findByStatusAndActiveTrue(String status);

    /**
     * Find carts by user name
     */
    List<Cart> findByUserNameContainingIgnoreCaseAndActiveTrue(String userName);

    /**
     * Custom query to find carts by name pattern
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}, 'active': true}")
    List<Cart> findByNameContainingIgnoreCase(String name);

    /**
     * Find carts with total amount greater than specified value
     */
    List<Cart> findByTotalAmountGreaterThanAndActiveTrue(Double amount);

    /**
     * Find carts with total amount between range
     */
    List<Cart> findByTotalAmountBetweenAndActiveTrue(Double minAmount, Double maxAmount);

    /**
     * Find carts created after specific date
     */
    List<Cart> findByCreatedAtAfterAndActiveTrue(LocalDateTime date);

    /**
     * Find carts created between dates
     */
    List<Cart> findByCreatedAtBetweenAndActiveTrue(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Custom query to find carts containing specific item
     */
    @Query("{'items.itemId': ?0, 'active': true}")
    List<Cart> findCartsContainingItem(String itemId);

    /**
     * Custom query to find carts by item name pattern
     */
    @Query("{'items.itemName': {$regex: ?0, $options: 'i'}, 'active': true}")
    List<Cart> findCartsByItemNameContaining(String itemName);

    /**
     * Find user's active cart (status = ACTIVE)
     */
    Optional<Cart> findByUserIdAndStatusAndActiveTrue(String userId, String status);

    /**
     * Count active carts
     */
    long countByActiveTrue();

    /**
     * Count carts by user ID
     */
    long countByUserIdAndActiveTrue(String userId);

    /**
     * Count carts by status
     */
    long countByStatusAndActiveTrue(String status);

    /**
     * Check if cart name exists for user
     */
    boolean existsByNameAndUserIdAndActiveTrue(String name, String userId);

    /**
     * Custom aggregation query to get total amount sum by user
     */
    @Query(value = "{'userId': ?0, 'active': true}", fields = "{'totalAmount': 1}")
    List<Cart> findCartTotalsByUserId(String userId);
}

