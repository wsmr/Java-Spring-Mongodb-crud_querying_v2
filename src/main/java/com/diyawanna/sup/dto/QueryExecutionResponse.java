package com.diyawanna.sup.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for dynamic query execution
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class QueryExecutionResponse {

    private boolean success;
    private String message;
    private Object data;
    private String queryName;
    private String queryId;
    private LocalDateTime executionTime;
    private Long executionDurationMs;
    private Integer resultCount;
    private Map<String, Object> metadata;

    public QueryExecutionResponse() {}

    public QueryExecutionResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.executionTime = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
        // Set result count if data is a collection
        if (data instanceof java.util.Collection) {
            this.resultCount = ((java.util.Collection<?>) data).size();
        } else if (data != null) {
            this.resultCount = 1;
        } else {
            this.resultCount = 0;
        }
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public Long getExecutionDurationMs() {
        return executionDurationMs;
    }

    public void setExecutionDurationMs(Long executionDurationMs) {
        this.executionDurationMs = executionDurationMs;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "QueryExecutionResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", queryName='" + queryName + '\'' +
                ", queryId='" + queryId + '\'' +
                ", executionTime=" + executionTime +
                ", executionDurationMs=" + executionDurationMs +
                ", resultCount=" + resultCount +
                '}';
    }
}

