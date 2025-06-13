package com.diyawanna.sup.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Map;

/**
 * Request DTO for dynamic query execution
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class QueryExecutionRequest {

    @NotBlank(message = "Query name is required")
    private String queryName;

    @NotNull(message = "Parameters cannot be null")
    private Map<String, Object> parameters;

    private boolean cacheable = false;
    private Integer cacheTimeoutSeconds;

    public QueryExecutionRequest() {}

    public QueryExecutionRequest(String queryName, Map<String, Object> parameters) {
        this.queryName = queryName;
        this.parameters = parameters;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
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

    @Override
    public String toString() {
        return "QueryExecutionRequest{" +
                "queryName='" + queryName + '\'' +
                ", parameters=" + parameters +
                ", cacheable=" + cacheable +
                ", cacheTimeoutSeconds=" + cacheTimeoutSeconds +
                '}';
    }
}

