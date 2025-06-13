package com.diyawanna.sup.exception;

/**
 * Custom exception for query already exists scenarios
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class QueryAlreadyExistsException extends RuntimeException {

    public QueryAlreadyExistsException(String message) {
        super(message);
    }

    public QueryAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

