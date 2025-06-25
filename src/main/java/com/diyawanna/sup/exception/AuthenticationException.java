package com.diyawanna.sup.exception;

/**
 * Custom exception for authentication failures
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
public class AuthenticationException extends RuntimeException {

    private Long retryAfter;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Long retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public Long getRetryAfter() {
        return retryAfter;
    }

    public void setRetryAfter(Long retryAfter) {
        this.retryAfter = retryAfter;
    }
}

