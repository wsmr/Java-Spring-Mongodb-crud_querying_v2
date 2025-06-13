package com.diyawanna.sup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for standardized error responses
 * 
 * This handler provides:
 * - Standardized JSON error responses
 * - Appropriate HTTP status codes
 * - Validation error handling
 * - Custom exception handling
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("error", "Validation failed");
        response.put("message", "Invalid input data");
        response.put("details", errors);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Authentication failed");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle user not found exceptions
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "User not found");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle user already exists exceptions
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "User already exists");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle university not found exceptions
     */
    @ExceptionHandler(UniversityNotFoundException.class)
    public ResponseEntity<?> handleUniversityNotFoundException(UniversityNotFoundException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "University not found");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle university already exists exceptions
     */
    @ExceptionHandler(UniversityAlreadyExistsException.class)
    public ResponseEntity<?> handleUniversityAlreadyExistsException(UniversityAlreadyExistsException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "University already exists");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle faculty not found exceptions
     */
    @ExceptionHandler(FacultyNotFoundException.class)
    public ResponseEntity<?> handleFacultyNotFoundException(FacultyNotFoundException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Faculty not found");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle cart not found exceptions
     */
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<?> handleCartNotFoundException(CartNotFoundException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Cart not found");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle query not found exceptions
     */
    @ExceptionHandler(QueryNotFoundException.class)
    public ResponseEntity<?> handleQueryNotFoundException(QueryNotFoundException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Query not found");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle query already exists exceptions
     */
    @ExceptionHandler(QueryAlreadyExistsException.class)
    public ResponseEntity<?> handleQueryAlreadyExistsException(QueryAlreadyExistsException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Query already exists");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid argument");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Runtime error");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", "An unexpected error occurred");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("path", request.getDescription(false));
        
        // Log the full exception for debugging
        ex.printStackTrace();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

