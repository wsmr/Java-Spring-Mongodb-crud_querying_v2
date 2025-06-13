package com.diyawanna.sup.controller;

import com.diyawanna.sup.entity.Faculty;
import com.diyawanna.sup.service.FacultyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Faculty controller for REST API endpoints
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/faculties")
@CrossOrigin(origins = "*")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @GetMapping
    public ResponseEntity<?> getAllFaculties() {
        try {
            List<Faculty> faculties = facultyService.getAllActiveFaculties();
            return ResponseEntity.ok(faculties);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve faculties");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFacultyById(@PathVariable String id) {
        try {
            Faculty faculty = facultyService.getFacultyById(id);
            return ResponseEntity.ok(faculty);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Faculty not found");
            error.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createFaculty(@Valid @RequestBody Faculty faculty) {
        try {
            Faculty createdFaculty = facultyService.createFaculty(faculty);
            return ResponseEntity.ok(createdFaculty);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create faculty");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFaculty(@PathVariable String id, @RequestBody Faculty faculty) {
        try {
            Faculty updatedFaculty = facultyService.updateFaculty(id, faculty);
            return ResponseEntity.ok(updatedFaculty);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update faculty");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFaculty(@PathVariable String id) {
        try {
            facultyService.deleteFaculty(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Faculty deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete faculty");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/university/{universityId}")
    public ResponseEntity<?> getFacultiesByUniversity(@PathVariable String universityId) {
        try {
            List<Faculty> faculties = facultyService.getFacultiesByUniversity(universityId);
            return ResponseEntity.ok(faculties);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve faculties");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/search/name")
    public ResponseEntity<?> searchFacultiesByName(@RequestParam String name) {
        try {
            List<Faculty> faculties = facultyService.searchFacultiesByName(name);
            return ResponseEntity.ok(faculties);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{facultyId}/subjects/{subject}")
    public ResponseEntity<?> addSubjectToFaculty(@PathVariable String facultyId, @PathVariable String subject) {
        try {
            Faculty faculty = facultyService.addSubjectToFaculty(facultyId, subject);
            return ResponseEntity.ok(faculty);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add subject");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{facultyId}/subjects/{subject}")
    public ResponseEntity<?> removeSubjectFromFaculty(@PathVariable String facultyId, @PathVariable String subject) {
        try {
            Faculty faculty = facultyService.removeSubjectFromFaculty(facultyId, subject);
            return ResponseEntity.ok(faculty);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to remove subject");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getFacultyStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalActiveFaculties", facultyService.countActiveFaculties());
            stats.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve statistics");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

