package com.diyawanna.sup.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
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
 * - Conditional stack trace inclusion (development only)
 * - Rate limiting support
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.include-stack-trace:false}")
    private boolean includeStackTrace;

    @Value("${app.environment:production}")
    private String environment;

    /**
     * Create base error response with conditional stack trace
     */
    private Map<String, Object> createErrorResponse(String error, String message, HttpStatus status, 
                                                   WebRequest request, Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("path", request.getDescription(false).replace("uri=", ""));
        response.put("environment", environment);

        // Include stack trace only in development environment
        if (includeStackTrace && "development".equalsIgnoreCase(environment)) {
            response.put("stackTrace", getStackTrace(ex));
            response.put("exceptionType", ex.getClass().getSimpleName());
        }

        return response;
    }

    /**
     * Get stack trace as string
     */
    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = createErrorResponse(
            "Validation failed", 
            "Invalid input data", 
            HttpStatus.BAD_REQUEST, 
            request, 
            ex
        );
        response.put("details", errors);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle authentication exceptions with rate limiting support
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        
        Map<String, Object> response = createErrorResponse(
            "Authentication failed", 
            ex.getMessage(), 
            status, 
            request, 
            ex
        );

        // Add rate limiting information if present
        if (ex.getRetryAfter() != null && ex.getRetryAfter() > 0) {
            response.put("retryAfter", ex.getRetryAfter());
            response.put("error", "Too many authentication attempts");
            status = HttpStatus.TOO_MANY_REQUESTS;
            response.put("status", status.value());
        }
        
        return new ResponseEntity<>(response, status);
    }

    /**
     * Handle user not found exceptions
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "User not found", 
            ex.getMessage(), 
            HttpStatus.NOT_FOUND, 
            request, 
            ex
        );
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle user already exists exceptions
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "User already exists", 
            ex.getMessage(), 
            HttpStatus.CONFLICT, 
            request, 
            ex
        );
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle university not found exceptions
     */
    @ExceptionHandler(UniversityNotFoundException.class)
    public ResponseEntity<?> handleUniversityNotFoundException(UniversityNotFoundException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "University not found", 
            ex.getMessage(), 
            HttpStatus.NOT_FOUND, 
            request, 
            ex
        );
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle university already exists exceptions
     */
    @ExceptionHandler(UniversityAlreadyExistsException.class)
    public ResponseEntity<?> handleUniversityAlreadyExistsException(UniversityAlreadyExistsException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "University already exists", 
            ex.getMessage(), 
            HttpStatus.CONFLICT, 
            request, 
            ex
        );
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle faculty not found exceptions
     */
    @ExceptionHandler(FacultyNotFoundException.class)
    public ResponseEntity<?> handleFacultyNotFoundException(FacultyNotFoundException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "Faculty not found", 
            ex.getMessage(), 
            HttpStatus.NOT_FOUND, 
            request, 
            ex
        );
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle cart not found exceptions
     */
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<?> handleCartNotFoundException(CartNotFoundException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "Cart not found", 
            ex.getMessage(), 
            HttpStatus.NOT_FOUND, 
            request, 
            ex
        );
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle query not found exceptions
     */
    @ExceptionHandler(QueryNotFoundException.class)
    public ResponseEntity<?> handleQueryNotFoundException(QueryNotFoundException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "Query not found", 
            ex.getMessage(), 
            HttpStatus.NOT_FOUND, 
            request, 
            ex
        );
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle query already exists exceptions
     */
    @ExceptionHandler(QueryAlreadyExistsException.class)
    public ResponseEntity<?> handleQueryAlreadyExistsException(QueryAlreadyExistsException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "Query already exists", 
            ex.getMessage(), 
            HttpStatus.CONFLICT, 
            request, 
            ex
        );
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "Invalid argument", 
            ex.getMessage(), 
            HttpStatus.BAD_REQUEST, 
            request, 
            ex
        );
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "Runtime error", 
            ex.getMessage(), 
            HttpStatus.INTERNAL_SERVER_ERROR, 
            request, 
            ex
        );
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            "Internal server error", 
            "An unexpected error occurred", 
            HttpStatus.INTERNAL_SERVER_ERROR, 
            request, 
            ex
        );
        
        // Always log the full exception for debugging (server-side only)
        ex.printStackTrace();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

