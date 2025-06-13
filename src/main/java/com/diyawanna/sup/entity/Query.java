package com.diyawanna.sup.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Query entity representing query collection in MongoDB
 * 
 * This entity stores dynamic queries including:
 * - Query information (name, description, SQL/MongoDB query)
 * - Query parameters and mappings
 * - Execution metadata
 * - Audit fields (created/modified dates)
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Document(collection = "query")
public class Query {

    @Id
    private String id;

    @NotBlank(message = "Query name is required")
    @Indexed(unique = true)
    private String name;

    private String description;
    
    @NotBlank(message = "Query content is required")
    private String queryContent;
    
    private String queryType; // FIND, AGGREGATE, UPDATE, DELETE, etc.
    
    private String collection; // Target collection for the query
    
    private Map<String, Object> parameters = new HashMap<>();
    
    private Map<String, String> variableMappings = new HashMap<>();
    
    private String successMessage;
    
    private String errorMessage;
    
    private String category; // USER, UNIVERSITY, FACULTY, CART, GENERAL
    
    private boolean cacheable = false;
    
    private Integer cacheTimeoutSeconds = 300; // 5 minutes default
    
    private boolean active = true;
    
    private String createdBy;
    
    private String lastModifiedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Default constructor
    public Query() {}

    // Constructor for essential fields
    public Query(String name, String queryContent) {
        this.name = name;
        this.queryContent = queryContent;
    }

    // Constructor with type and collection
    public Query(String name, String queryContent, String queryType, String collection) {
        this.name = name;
        this.queryContent = queryContent;
        this.queryType = queryType;
        this.collection = collection;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQueryContent() {
        return queryContent;
    }

    public void setQueryContent(String queryContent) {
        this.queryContent = queryContent;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? parameters : new HashMap<>();
    }

    public void addParameter(String key, Object value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, value);
    }

    public void removeParameter(String key) {
        if (this.parameters != null) {
            this.parameters.remove(key);
        }
    }

    public Map<String, String> getVariableMappings() {
        return variableMappings;
    }

    public void setVariableMappings(Map<String, String> variableMappings) {
        this.variableMappings = variableMappings != null ? variableMappings : new HashMap<>();
    }

    public void addVariableMapping(String variable, String mapping) {
        if (this.variableMappings == null) {
            this.variableMappings = new HashMap<>();
        }
        this.variableMappings.put(variable, mapping);
    }

    public void removeVariableMapping(String variable) {
        if (this.variableMappings != null) {
            this.variableMappings.remove(variable);
        }
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public Integer getCacheTimeoutSeconds() {
        return cacheTimeoutSeconds;
    }

    public void setCacheTimeoutSeconds(Integer cacheTimeoutSeconds) {
        this.cacheTimeoutSeconds = cacheTimeoutSeconds;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Query{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", queryType='" + queryType + '\'' +
                ", collection='" + collection + '\'' +
                ", category='" + category + '\'' +
                ", cacheable=" + cacheable +
                ", active=" + active +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

