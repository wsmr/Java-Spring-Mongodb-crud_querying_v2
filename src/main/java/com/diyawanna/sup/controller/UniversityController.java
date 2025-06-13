package com.diyawanna.sup.controller;

import com.diyawanna.sup.entity.University;
import com.diyawanna.sup.service.UniversityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * University controller for REST API endpoints
 * 
 * This controller provides:
 * - Complete CRUD operations for universities
 * - Search and filtering endpoints
 * - Faculty management operations
 * - Proper error handling and validation
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/universities")
@CrossOrigin(origins = "*")
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    /**
     * Get all active universities
     * GET /api/universities
     */
    @GetMapping
    public ResponseEntity<?> getAllUniversities() {
        try {
            List<University> universities = universityService.getAllActiveUniversities();
            return ResponseEntity.ok(universities);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve universities");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get university by ID
     * GET /api/universities/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUniversityById(@PathVariable String id) {
        try {
            University university = universityService.getUniversityById(id);
            return ResponseEntity.ok(university);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "University not found");
            error.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get university by name
     * GET /api/universities/name/{name}
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getUniversityByName(@PathVariable String name) {
        try {
            University university = universityService.getUniversityByName(name);
            return ResponseEntity.ok(university);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "University not found");
            error.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new university
     * POST /api/universities
     */
    @PostMapping
    public ResponseEntity<?> createUniversity(@Valid @RequestBody University university) {
        try {
            University createdUniversity = universityService.createUniversity(university);
            return ResponseEntity.ok(createdUniversity);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create university");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update university
     * PUT /api/universities/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUniversity(@PathVariable String id, @RequestBody University university) {
        try {
            University updatedUniversity = universityService.updateUniversity(id, university);
            return ResponseEntity.ok(updatedUniversity);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update university");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete university (soft delete)
     * DELETE /api/universities/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUniversity(@PathVariable String id) {
        try {
            universityService.deleteUniversity(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "University deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete university");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search universities by name
     * GET /api/universities/search/name
     */
    @GetMapping("/search/name")
    public ResponseEntity<?> searchUniversitiesByName(@RequestParam String name) {
        try {
            List<University> universities = universityService.searchUniversitiesByName(name);
            return ResponseEntity.ok(universities);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get universities by location
     * GET /api/universities/location/{location}
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<?> getUniversitiesByLocation(@PathVariable String location) {
        try {
            List<University> universities = universityService.getUniversitiesByLocation(location);
            return ResponseEntity.ok(universities);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve universities");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Add faculty to university
     * POST /api/universities/{universityId}/faculties/{facultyId}
     */
    @PostMapping("/{universityId}/faculties/{facultyId}")
    public ResponseEntity<?> addFacultyToUniversity(@PathVariable String universityId, @PathVariable String facultyId) {
        try {
            University university = universityService.addFacultyToUniversity(universityId, facultyId);
            return ResponseEntity.ok(university);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add faculty");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Remove faculty from university
     * DELETE /api/universities/{universityId}/faculties/{facultyId}
     */
    @DeleteMapping("/{universityId}/faculties/{facultyId}")
    public ResponseEntity<?> removeFacultyFromUniversity(@PathVariable String universityId, @PathVariable String facultyId) {
        try {
            University university = universityService.removeFacultyFromUniversity(universityId, facultyId);
            return ResponseEntity.ok(university);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to remove faculty");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get university statistics
     * GET /api/universities/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getUniversityStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalActiveUniversities", universityService.countActiveUniversities());
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
     * Check university name availability
     * GET /api/universities/check/name/{name}
     */
    @GetMapping("/check/name/{name}")
    public ResponseEntity<?> checkUniversityNameAvailability(@PathVariable String name) {
        try {
            boolean exists = universityService.universityNameExists(name);
            Map<String, Object> response = new HashMap<>();
            response.put("name", name);
            response.put("available", !exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to check name");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

