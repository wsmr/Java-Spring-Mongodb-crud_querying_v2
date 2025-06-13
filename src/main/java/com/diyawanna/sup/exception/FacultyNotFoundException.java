package com.diyawanna.sup.exception;

/**
 * Custom exception for faculty not found scenarios
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class FacultyNotFoundException extends RuntimeException {

    public FacultyNotFoundException(String message) {
        super(message);
    }

    public FacultyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

