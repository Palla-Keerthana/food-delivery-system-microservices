package com.fooddelivery.menumodule.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers.
 * Catches exceptions thrown anywhere in the application
 * and returns proper JSON error responses with HTTP status codes.
 * Uses @RestControllerAdvice to intercept all controller exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles invalid input validation errors.
     * Triggered when @Valid annotation fails on request body.
     * Returns 400 BAD REQUEST with field-level error messages.
     *
     * @param ex the MethodArgumentNotValidException thrown by Spring
     * @return 400 BAD REQUEST with map of field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("error", "Validation Failed");
        response.put("messages", fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles invalid request exceptions.
     * Triggered when user provides invalid input like empty name or zero price.
     * Returns 400 BAD REQUEST with error message.
     *
     * @param ex the InvalidRequestException thrown by service layer
     * @return 400 BAD REQUEST with error details
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(
            InvalidRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles menu item not found exceptions.
     * Triggered when a menu item doesn't exist in the database.
     * Returns 404 NOT FOUND with error message.
     *
     * @param ex the MenuItemNotFoundException thrown by service or repository
     * @return 404 NOT FOUND with error details
     */
    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMenuItemNotFound(
            MenuItemNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles restaurant not found exceptions.
     * Triggered when a restaurant doesn't exist in the database.
     * Returns 404 NOT FOUND with error message.
     *
     * @param ex the RestaurantNotFoundException thrown by service
     * @return 404 NOT FOUND with error details
     */
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRestaurantNotFound(
            RestaurantNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles resource not found exceptions.
     * General not found exception for any resource.
     * Returns 404 NOT FOUND with error message.
     *
     * @param ex the ResourceNotFoundException thrown by service
     * @return 404 NOT FOUND with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles all other unexpected exceptions.
     * Catches any exception not handled by specific handlers.
     * Returns 500 INTERNAL SERVER ERROR.
     *
     * @param ex the Exception thrown anywhere in application
     * @return 500 INTERNAL SERVER ERROR with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(
            Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong: " + ex.getMessage());
    }

    /**
     * Private helper method that builds consistent JSON error response.
     * Used by all exception handler methods to ensure uniform response format.
     *
     * @param status  the HTTP status code
     * @param message the error message to include in response
     * @return ResponseEntity with error details map
     */
    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}