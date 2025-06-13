package com.diyawanna.sup.controller;

import com.diyawanna.sup.entity.Query;
import com.diyawanna.sup.service.QueryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Query controller for REST API endpoints
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/queries")
@CrossOrigin(origins = "*")
public class QueryController {

    @Autowired
    private QueryService queryService;

    @GetMapping
    public ResponseEntity<?> getAllQueries() {
        try {
            List<Query> queries = queryService.getAllActiveQueries();
            return ResponseEntity.ok(queries);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve queries");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQueryById(@PathVariable String id) {
        try {
            Query query = queryService.getQueryById(id);
            return ResponseEntity.ok(query);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Query not found");
            error.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getQueryByName(@PathVariable String name) {
        try {
            Query query = queryService.getQueryByName(name);
            return ResponseEntity.ok(query);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Query not found");
            error.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createQuery(@Valid @RequestBody Query query) {
        try {
            Query createdQuery = queryService.createQuery(query);
            return ResponseEntity.ok(createdQuery);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create query");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuery(@PathVariable String id, @RequestBody Query query) {
        try {
            Query updatedQuery = queryService.updateQuery(id, query);
            return ResponseEntity.ok(updatedQuery);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update query");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuery(@PathVariable String id) {
        try {
            queryService.deleteQuery(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Query deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete query");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getQueriesByCategory(@PathVariable String category) {
        try {
            List<Query> queries = queryService.getQueriesByCategory(category);
            return ResponseEntity.ok(queries);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve queries");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/type/{queryType}")
    public ResponseEntity<?> getQueriesByType(@PathVariable String queryType) {
        try {
            List<Query> queries = queryService.getQueriesByType(queryType);
            return ResponseEntity.ok(queries);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve queries");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/collection/{collection}")
    public ResponseEntity<?> getQueriesByCollection(@PathVariable String collection) {
        try {
            List<Query> queries = queryService.getQueriesByCollection(collection);
            return ResponseEntity.ok(queries);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve queries");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/cacheable")
    public ResponseEntity<?> getCacheableQueries() {
        try {
            List<Query> queries = queryService.getCacheableQueries();
            return ResponseEntity.ok(queries);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve cacheable queries");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/search/name")
    public ResponseEntity<?> searchQueriesByName(@RequestParam String name) {
        try {
            List<Query> queries = queryService.searchQueriesByName(name);
            return ResponseEntity.ok(queries);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/creator/{createdBy}")
    public ResponseEntity<?> getQueriesByCreator(@PathVariable String createdBy) {
        try {
            List<Query> queries = queryService.getQueriesByCreator(createdBy);
            return ResponseEntity.ok(queries);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve queries");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getQueryStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalActiveQueries", queryService.countActiveQueries());
            stats.put("totalCacheableQueries", queryService.countCacheableQueries());
            stats.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve statistics");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/check/name/{name}")
    public ResponseEntity<?> checkQueryNameAvailability(@PathVariable String name) {
        try {
            boolean exists = queryService.queryNameExists(name);
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

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateQuery(@PathVariable String id) {
        try {
            Query query = queryService.activateQuery(id);
            return ResponseEntity.ok(query);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to activate query");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateQuery(@PathVariable String id) {
        try {
            Query query = queryService.deactivateQuery(id);
            return ResponseEntity.ok(query);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to deactivate query");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

