package com.diyawanna.sup.exception;

/**
 * Custom exception for user already exists scenarios
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

