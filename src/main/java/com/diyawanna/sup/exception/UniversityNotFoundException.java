package com.diyawanna.sup.exception;

/**
 * Custom exception for university not found scenarios
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class UniversityNotFoundException extends RuntimeException {

    public UniversityNotFoundException(String message) {
        super(message);
    }

    public UniversityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

