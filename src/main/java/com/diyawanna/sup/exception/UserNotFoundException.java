package com.diyawanna.sup.exception;

/**
 * Custom exception for user not found scenarios
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

