package com.diyawanna.sup.exception;

/**
 * Custom exception for authentication failures
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

