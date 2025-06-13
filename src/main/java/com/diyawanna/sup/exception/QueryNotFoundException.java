package com.diyawanna.sup.exception;

/**
 * Custom exception for query not found scenarios
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class QueryNotFoundException extends RuntimeException {

    public QueryNotFoundException(String message) {
        super(message);
    }

    public QueryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

