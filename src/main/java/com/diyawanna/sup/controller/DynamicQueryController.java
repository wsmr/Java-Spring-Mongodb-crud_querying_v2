package com.diyawanna.sup.controller;

import com.diyawanna.sup.service.DynamicQueryService;
import com.diyawanna.sup.dto.QueryExecutionRequest;
import com.diyawanna.sup.dto.QueryExecutionResponse;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Dynamic Query controller for executing stored and configured queries
 * 
 * This controller provides:
 * - Dynamic query execution with parameter substitution
 * - Stored query execution from database
 * - Query validation and parameter checking
 * - Sample query examples and documentation
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/dynamic-query")
@CrossOrigin(origins = "*")
public class DynamicQueryController {

    @Autowired
    private DynamicQueryService dynamicQueryService;

    /**
     * Execute dynamic query with parameters
     * POST /api/dynamic-query/execute
     */
    @PostMapping("/execute")
    public ResponseEntity<?> executeDynamicQuery(@Valid @RequestBody QueryExecutionRequest request) {
        try {
            // Validate parameters
            if (!dynamicQueryService.validateParameters(request.getQueryName(), request.getParameters())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", dynamicQueryService.getErrorMessage("invalid_parameters"));
                error.put("message", "Invalid or missing parameters for query: " + request.getQueryName());
                return ResponseEntity.badRequest().body(error);
            }

            // Execute query
            Object result = dynamicQueryService.executeDynamicQuery(request.getQueryName(), request.getParameters());
            
            // Create response
            QueryExecutionResponse response = new QueryExecutionResponse();
            response.setSuccess(true);
            response.setMessage(dynamicQueryService.getSuccessMessage("query_executed"));
            response.setData(result);
            response.setQueryName(request.getQueryName());
            response.setExecutionTime(LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", dynamicQueryService.getErrorMessage("execution_failed"));
            error.put("message", e.getMessage());
            error.put("queryName", request.getQueryName());
            error.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Execute stored query from database
     * POST /api/dynamic-query/execute-stored/{queryId}
     */
    @PostMapping("/execute-stored/{queryId}")
    public ResponseEntity<?> executeStoredQuery(@PathVariable String queryId, @RequestBody(required = false) Map<String, Object> parameters) {
        try {
            Object result = dynamicQueryService.executeStoredQuery(queryId, parameters);
            
            QueryExecutionResponse response = new QueryExecutionResponse();
            response.setSuccess(true);
            response.setMessage(dynamicQueryService.getSuccessMessage("query_executed"));
            response.setData(result);
            response.setQueryId(queryId);
            response.setExecutionTime(LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", dynamicQueryService.getErrorMessage("execution_failed"));
            error.put("message", e.getMessage());
            error.put("queryId", queryId);
            error.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get available query mappings
     * GET /api/dynamic-query/mappings
     */
    @GetMapping("/mappings")
    public ResponseEntity<?> getQueryMappings() {
        try {
            Map<String, JsonNode> mappings = dynamicQueryService.getQueryMappings();
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve query mappings");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get sample queries with examples
     * GET /api/dynamic-query/samples
     */
    @GetMapping("/samples")
    public ResponseEntity<?> getSampleQueries() {
        try {
            JsonNode samples = dynamicQueryService.getSampleQueries();
            return ResponseEntity.ok(samples);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve sample queries");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Validate query parameters
     * POST /api/dynamic-query/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateQuery(@RequestBody QueryExecutionRequest request) {
        try {
            boolean isValid = dynamicQueryService.validateParameters(request.getQueryName(), request.getParameters());
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("queryName", request.getQueryName());
            response.put("message", isValid ? "Query parameters are valid" : "Invalid or missing parameters");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get query documentation
     * GET /api/dynamic-query/docs
     */
    @GetMapping("/docs")
    public ResponseEntity<?> getQueryDocumentation() {
        try {
            Map<String, Object> docs = new HashMap<>();
            docs.put("description", "Dynamic Query API allows execution of predefined queries with parameter substitution");
            docs.put("version", "1.0.0");
            docs.put("endpoints", Map.of(
                "execute", "POST /api/dynamic-query/execute - Execute dynamic query",
                "execute-stored", "POST /api/dynamic-query/execute-stored/{queryId} - Execute stored query",
                "mappings", "GET /api/dynamic-query/mappings - Get available query mappings",
                "samples", "GET /api/dynamic-query/samples - Get sample queries",
                "validate", "POST /api/dynamic-query/validate - Validate query parameters"
            ));
            docs.put("queryFormat", "Queries use ${parameterName} syntax for parameter substitution");
            docs.put("supportedTypes", new String[]{"FIND", "AGGREGATE", "COUNT"});
            docs.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(docs);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve documentation");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Health check for dynamic query service
     * GET /api/dynamic-query/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("service", "Dynamic Query Service");
            health.put("configurationLoaded", dynamicQueryService.getQueryMappings() != null);
            health.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "DOWN");
            error.put("error", "Health check failed");
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(503).body(error);
        }
    }
}

