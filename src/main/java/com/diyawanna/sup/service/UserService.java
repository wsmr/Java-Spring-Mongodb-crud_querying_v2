package com.diyawanna.sup.service;

import com.diyawanna.sup.entity.User;
import com.diyawanna.sup.repository.UserRepository;
import com.diyawanna.sup.exception.UserNotFoundException;
import com.diyawanna.sup.exception.UserAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User service for business logic and CRUD operations
 * 
 * This service provides:
 * - Complete CRUD operations for users
 * - Business logic validation
 * - Caching for performance optimization
 * - Search and filtering capabilities
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all active users
     */
    @Cacheable(value = "users", key = "'all_active'")
    public List<User> getAllActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    /**
     * Get user by ID
     */
    @Cacheable(value = "users", key = "#id")
    public User getUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        return user.get();
    }

    /**
     * Get user by username
     */
    @Cacheable(value = "users", key = "'username_' + #username")
    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with username: " + username);
        }
        return user.get();
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        return user.get();
    }

    /**
     * Create new user
     */
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user) {
        // Validate username uniqueness
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + user.getUsername());
        }

        // Validate email uniqueness if provided
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + user.getEmail());
        }

        // Encode password if provided
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Set default values
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Update user
     */
    @CachePut(value = "users", key = "#id")
    @CacheEvict(value = "users", key = "'all_active'")
    public User updateUser(String id, User userUpdate) {
        User existingUser = getUserById(id);

        // Update fields if provided
        if (userUpdate.getName() != null) {
            existingUser.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null) {
            // Check email uniqueness
            if (!userUpdate.getEmail().equals(existingUser.getEmail()) && 
                userRepository.existsByEmail(userUpdate.getEmail())) {
                throw new UserAlreadyExistsException("Email already exists: " + userUpdate.getEmail());
            }
            existingUser.setEmail(userUpdate.getEmail());
        }
        if (userUpdate.getAge() != null) {
            existingUser.setAge(userUpdate.getAge());
        }
        if (userUpdate.getUniversity() != null) {
            existingUser.setUniversity(userUpdate.getUniversity());
        }
        if (userUpdate.getSchool() != null) {
            existingUser.setSchool(userUpdate.getSchool());
        }
        if (userUpdate.getWork() != null) {
            existingUser.setWork(userUpdate.getWork());
        }
        if (userUpdate.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userUpdate.getPhoneNumber());
        }
        if (userUpdate.getAddress() != null) {
            existingUser.setAddress(userUpdate.getAddress());
        }
        if (userUpdate.getProfilePicture() != null) {
            existingUser.setProfilePicture(userUpdate.getProfilePicture());
        }

        existingUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(existingUser);
    }

    /**
     * Delete user (soft delete)
     */
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(String id) {
        User user = getUserById(id);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Hard delete user
     */
    @CacheEvict(value = "users", allEntries = true)
    public void hardDeleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Search users by name
     */
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Get users by university
     */
    public List<User> getUsersByUniversity(String university) {
        return userRepository.findByUniversityAndActiveTrue(university);
    }

    /**
     * Get users by age range
     */
    public List<User> getUsersByAgeRange(Integer minAge, Integer maxAge) {
        return userRepository.findByAgeBetweenAndActiveTrue(minAge, maxAge);
    }

    /**
     * Get users by work
     */
    public List<User> getUsersByWork(String work) {
        return userRepository.findByWorkContainingIgnoreCaseAndActiveTrue(work);
    }

    /**
     * Get users by school
     */
    public List<User> getUsersBySchool(String school) {
        return userRepository.findBySchoolContainingIgnoreCaseAndActiveTrue(school);
    }

    /**
     * Get users created after specific date
     */
    public List<User> getUsersCreatedAfter(LocalDateTime date) {
        return userRepository.findUsersCreatedAfter(date);
    }

    /**
     * Count active users
     */
    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }

    /**
     * Count users by university
     */
    public long countUsersByUniversity(String university) {
        return userRepository.countByUniversityAndActiveTrue(university);
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Activate user
     */
    @CachePut(value = "users", key = "#id")
    public User activateUser(String id) {
        User user = getUserById(id);
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Deactivate user
     */
    @CachePut(value = "users", key = "#id")
    public User deactivateUser(String id) {
        User user = getUserById(id);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Update user password
     */
    @CacheEvict(value = "users", key = "#id")
    public void updateUserPassword(String id, String newPassword) {
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}

