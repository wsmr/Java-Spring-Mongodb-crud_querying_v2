package com.diyawanna.sup.exception;

/**
 * Custom exception for university already exists scenarios
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class UniversityAlreadyExistsException extends RuntimeException {

    public UniversityAlreadyExistsException(String message) {
        super(message);
    }

    public UniversityAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

