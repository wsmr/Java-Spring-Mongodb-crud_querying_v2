package com.diyawanna.sup.controller;

import com.diyawanna.sup.entity.User;
import com.diyawanna.sup.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User controller for REST API endpoints
 * 
 * This controller provides:
 * - Complete CRUD operations for users
 * - Search and filtering endpoints
 * - User management operations
 * - Proper error handling and validation
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get all active users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllActiveUsers();
            // Remove passwords from response
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve users");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            User user = userService.getUserById(id);
            user.setPassword(null); // Remove password from response
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            error.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user by username
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            user.setPassword(null); // Remove password from response
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            error.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new user
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            createdUser.setPassword(null); // Remove password from response
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create user");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update user
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            updatedUser.setPassword(null); // Remove password from response
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update user");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete user (soft delete)
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete user");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Hard delete user
     * DELETE /api/users/{id}/hard
     */
    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> hardDeleteUser(@PathVariable String id) {
        try {
            userService.hardDeleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User permanently deleted");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete user");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search users by name
     * GET /api/users/search/name
     */
    @GetMapping("/search/name")
    public ResponseEntity<?> searchUsersByName(@RequestParam String name) {
        try {
            List<User> users = userService.searchUsersByName(name);
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get users by university
     * GET /api/users/university/{university}
     */
    @GetMapping("/university/{university}")
    public ResponseEntity<?> getUsersByUniversity(@PathVariable String university) {
        try {
            List<User> users = userService.getUsersByUniversity(university);
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve users");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get users by age range
     * GET /api/users/age-range
     */
    @GetMapping("/age-range")
    public ResponseEntity<?> getUsersByAgeRange(@RequestParam Integer minAge, @RequestParam Integer maxAge) {
        try {
            List<User> users = userService.getUsersByAgeRange(minAge, maxAge);
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve users");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get users by work
     * GET /api/users/work/{work}
     */
    @GetMapping("/work/{work}")
    public ResponseEntity<?> getUsersByWork(@PathVariable String work) {
        try {
            List<User> users = userService.getUsersByWork(work);
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve users");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get users by school
     * GET /api/users/school/{school}
     */
    @GetMapping("/school/{school}")
    public ResponseEntity<?> getUsersBySchool(@PathVariable String school) {
        try {
            List<User> users = userService.getUsersBySchool(school);
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve users");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Activate user
     * PUT /api/users/{id}/activate
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable String id) {
        try {
            User user = userService.activateUser(id);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to activate user");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Deactivate user
     * PUT /api/users/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable String id) {
        try {
            User user = userService.deactivateUser(id);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to deactivate user");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get user statistics
     * GET /api/users/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalActiveUsers", userService.countActiveUsers());
            stats.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve statistics");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check username availability
     * GET /api/users/check/username/{username}
     */
    @GetMapping("/check/username/{username}")
    public ResponseEntity<?> checkUsernameAvailability(@PathVariable String username) {
        try {
            boolean exists = userService.usernameExists(username);
            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("available", !exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to check username");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check email availability
     * GET /api/users/check/email/{email}
     */
    @GetMapping("/check/email/{email}")
    public ResponseEntity<?> checkEmailAvailability(@PathVariable String email) {
        try {
            boolean exists = userService.emailExists(email);
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("available", !exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to check email");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

