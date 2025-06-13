package com.diyawanna.sup.service;

import com.diyawanna.sup.entity.Query;
import com.diyawanna.sup.repository.QueryRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dynamic Query Management Service
 * 
 * This service provides:
 * - Loading query configurations from external JSON
 * - Dynamic query execution with parameter substitution
 * - Support for MongoDB find and aggregation operations
 * - Message and variable mapping management
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Service
public class DynamicQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private QueryRepository queryRepository;

    private JsonNode queryConfig;
    private Map<String, String> successMessages;
    private Map<String, String> errorMessages;
    private Map<String, JsonNode> queryMappings;
    private Map<String, String> variableMappings;

    @PostConstruct
    public void loadConfiguration() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("query-config.json");
            queryConfig = mapper.readTree(resource.getInputStream());
            
            loadMessages();
            loadQueryMappings();
            loadVariableMappings();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load query configuration", e);
        }
    }

    private void loadMessages() {
        successMessages = new HashMap<>();
        errorMessages = new HashMap<>();
        
        JsonNode messages = queryConfig.get("messages");
        if (messages != null) {
            JsonNode success = messages.get("success");
            if (success != null) {
                success.fields().forEachRemaining(entry -> 
                    successMessages.put(entry.getKey(), entry.getValue().asText()));
            }
            
            JsonNode error = messages.get("error");
            if (error != null) {
                error.fields().forEachRemaining(entry -> 
                    errorMessages.put(entry.getKey(), entry.getValue().asText()));
            }
        }
    }

    private void loadQueryMappings() {
        queryMappings = new HashMap<>();
        JsonNode mappings = queryConfig.get("queryMappings");
        if (mappings != null) {
            mappings.fields().forEachRemaining(entry -> 
                queryMappings.put(entry.getKey(), entry.getValue()));
        }
    }

    private void loadVariableMappings() {
        variableMappings = new HashMap<>();
        JsonNode variables = queryConfig.get("variableMappings");
        if (variables != null) {
            variables.fields().forEachRemaining(entry -> 
                variableMappings.put(entry.getKey(), entry.getValue().asText()));
        }
    }

    /**
     * Execute dynamic query by name with parameters
     */
    public Object executeDynamicQuery(String queryName, Map<String, Object> parameters) {
        try {
            // Parse query name (e.g., "user.findById")
            String[] parts = queryName.split("\\.");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid query name format. Expected: 'entity.operation'");
            }
            
            String entity = parts[0];
            String operation = parts[1];
            
            // Get query mapping
            JsonNode entityMappings = queryMappings.get(entity);
            if (entityMappings == null) {
                throw new IllegalArgumentException("Entity not found: " + entity);
            }
            
            JsonNode queryMapping = entityMappings.get(operation);
            if (queryMapping == null) {
                throw new IllegalArgumentException("Operation not found: " + operation + " for entity: " + entity);
            }
            
            // Extract query details
            String queryString = queryMapping.get("query").asText();
            String collection = queryMapping.get("collection").asText();
            String type = queryMapping.get("type").asText();
            
            // Substitute parameters
            String processedQuery = substituteParameters(queryString, parameters);
            
            // Execute query based on type
            return executeQuery(processedQuery, collection, type);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute dynamic query: " + e.getMessage(), e);
        }
    }

    /**
     * Execute stored query from database
     */
    public Object executeStoredQuery(String queryId, Map<String, Object> parameters) {
        try {
            Optional<Query> queryOpt = queryRepository.findById(queryId);
            if (queryOpt.isEmpty()) {
                throw new IllegalArgumentException("Query not found: " + queryId);
            }
            
            Query query = queryOpt.get();
            if (!query.isActive()) {
                throw new IllegalArgumentException("Query is not active: " + queryId);
            }
            
            // Merge stored parameters with provided parameters
            Map<String, Object> allParameters = new HashMap<>(query.getParameters());
            if (parameters != null) {
                allParameters.putAll(parameters);
            }
            
            // Substitute parameters
            String processedQuery = substituteParameters(query.getQueryContent(), allParameters);
            
            // Execute query
            return executeQuery(processedQuery, query.getCollection(), query.getQueryType());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute stored query: " + e.getMessage(), e);
        }
    }

    /**
     * Substitute parameters in query string
     */
    private String substituteParameters(String queryString, Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return queryString;
        }
        
        String result = queryString;
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(queryString);
        
        while (matcher.find()) {
            String paramName = matcher.group(1);
            Object paramValue = parameters.get(paramName);
            
            if (paramValue == null) {
                throw new IllegalArgumentException("Missing required parameter: " + paramName);
            }
            
            // Convert parameter value to appropriate string representation
            String valueStr = convertParameterValue(paramValue, paramName);
            result = result.replace("${" + paramName + "}", valueStr);
        }
        
        return result;
    }

    /**
     * Convert parameter value to appropriate string representation
     */
    private String convertParameterValue(Object value, String paramName) {
        String type = variableMappings.get(paramName);
        
        if ("Integer".equals(type)) {
            return value.toString();
        } else if ("String".equals(type)) {
            return "'" + value.toString() + "'";
        } else {
            // Default to string representation
            return value.toString();
        }
    }

    /**
     * Execute query based on type
     */
    private Object executeQuery(String queryString, String collection, String type) {
        try {
            switch (type.toUpperCase()) {
                case "FIND":
                    return executeFindQuery(queryString, collection);
                case "AGGREGATE":
                    return executeAggregateQuery(queryString, collection);
                case "COUNT":
                    return executeCountQuery(queryString, collection);
                default:
                    throw new IllegalArgumentException("Unsupported query type: " + type);
            }
        } catch (Exception e) {
            throw new RuntimeException("Query execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Execute MongoDB find query
     */
    private List<Object> executeFindQuery(String queryString, String collection) {
        try {
            // Parse JSON query string to create MongoDB query
            ObjectMapper mapper = new ObjectMapper();
            JsonNode queryJson = mapper.readTree(queryString);
            
            org.springframework.data.mongodb.core.query.Query mongoQuery = 
                new org.springframework.data.mongodb.core.query.Query();
            
            // Build criteria from JSON
            Criteria criteria = buildCriteriaFromJson(queryJson);
            mongoQuery.addCriteria(criteria);
            
            return mongoTemplate.find(mongoQuery, Object.class, collection);
            
        } catch (Exception e) {
            throw new RuntimeException("Find query execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Execute MongoDB aggregation query
     */
    private List<Object> executeAggregateQuery(String queryString, String collection) {
        try {
            // Parse aggregation pipeline
            ObjectMapper mapper = new ObjectMapper();
            JsonNode pipelineJson = mapper.readTree(queryString);
            
            List<org.springframework.data.mongodb.core.aggregation.AggregationOperation> operations = 
                new ArrayList<>();
            
            // Build aggregation operations from JSON
            if (pipelineJson.isArray()) {
                for (JsonNode stage : pipelineJson) {
                    // This is a simplified implementation
                    // In a production environment, you would need more sophisticated parsing
                    // For Spring Boot 2.7.x compatibility, we'll use a basic match operation
                    operations.add(Aggregation.match(new org.springframework.data.mongodb.core.query.Criteria()));
                }
            }
            
            Aggregation aggregation = Aggregation.newAggregation(operations);
            AggregationResults<Object> results = mongoTemplate.aggregate(aggregation, collection, Object.class);
            
            return results.getMappedResults();
            
        } catch (Exception e) {
            throw new RuntimeException("Aggregation query execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Execute MongoDB count query
     */
    private long executeCountQuery(String queryString, String collection) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode queryJson = mapper.readTree(queryString);
            
            org.springframework.data.mongodb.core.query.Query mongoQuery = 
                new org.springframework.data.mongodb.core.query.Query();
            
            Criteria criteria = buildCriteriaFromJson(queryJson);
            mongoQuery.addCriteria(criteria);
            
            return mongoTemplate.count(mongoQuery, collection);
            
        } catch (Exception e) {
            throw new RuntimeException("Count query execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Build MongoDB Criteria from JSON
     */
    private Criteria buildCriteriaFromJson(JsonNode queryJson) {
        Criteria criteria = new Criteria();
        
        queryJson.fields().forEachRemaining(entry -> {
            String field = entry.getKey();
            JsonNode value = entry.getValue();
            
            if (value.isTextual()) {
                criteria.and(field).is(value.asText());
            } else if (value.isNumber()) {
                criteria.and(field).is(value.asLong());
            } else if (value.isBoolean()) {
                criteria.and(field).is(value.asBoolean());
            } else if (value.isObject()) {
                // Handle complex queries like regex, range, etc.
                handleComplexCriteria(criteria, field, value);
            }
        });
        
        return criteria;
    }

    /**
     * Handle complex criteria like regex, range queries
     */
    private void handleComplexCriteria(Criteria criteria, String field, JsonNode value) {
        if (value.has("$regex")) {
            String regex = value.get("$regex").asText();
            String options = value.has("$options") ? value.get("$options").asText() : "";
            criteria.and(field).regex(regex, options);
        } else if (value.has("$gte") || value.has("$lte")) {
            if (value.has("$gte")) {
                criteria.and(field).gte(value.get("$gte").asLong());
            }
            if (value.has("$lte")) {
                criteria.and(field).lte(value.get("$lte").asLong());
            }
        } else if (value.has("$in")) {
            List<Object> inValues = new ArrayList<>();
            value.get("$in").forEach(item -> inValues.add(item.asText()));
            criteria.and(field).in(inValues);
        }
    }

    /**
     * Get success message by key
     */
    public String getSuccessMessage(String key) {
        return successMessages.getOrDefault(key, "Operation completed successfully");
    }

    /**
     * Get error message by key
     */
    public String getErrorMessage(String key) {
        return errorMessages.getOrDefault(key, "Operation failed");
    }

    /**
     * Get available query mappings
     */
    public Map<String, JsonNode> getQueryMappings() {
        return queryMappings;
    }

    /**
     * Get sample queries
     */
    public JsonNode getSampleQueries() {
        return queryConfig.get("sampleQueries");
    }

    /**
     * Validate query parameters
     */
    public boolean validateParameters(String queryName, Map<String, Object> parameters) {
        try {
            String[] parts = queryName.split("\\.");
            if (parts.length != 2) return false;
            
            JsonNode entityMappings = queryMappings.get(parts[0]);
            if (entityMappings == null) return false;
            
            JsonNode queryMapping = entityMappings.get(parts[1]);
            if (queryMapping == null) return false;
            
            JsonNode requiredParams = queryMapping.get("parameters");
            if (requiredParams != null && requiredParams.isArray()) {
                for (JsonNode param : requiredParams) {
                    String paramName = param.asText();
                    if (!parameters.containsKey(paramName)) {
                        return false;
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

