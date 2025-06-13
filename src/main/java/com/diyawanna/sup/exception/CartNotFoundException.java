package com.diyawanna.sup.exception;

/**
 * Custom exception for cart not found scenarios
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(String message) {
        super(message);
    }

    public CartNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

