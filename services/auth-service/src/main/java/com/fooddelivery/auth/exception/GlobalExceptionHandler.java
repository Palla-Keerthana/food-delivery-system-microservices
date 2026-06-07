package com.fooddelivery.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the auth-service.
 * Intercepts exceptions thrown across all controllers and
 * returns consistent, structured error responses.
 * Uses {@code @RestControllerAdvice} to apply globally.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link InvalidRequestException} thrown when
     * request validation fails or business rules are violated.
     * Example: email already registered, password mismatch.
     *
     * @param ex the exception containing the error message
     * @return {@code 400 Bad Request} with error details
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(
            InvalidRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles {@link AuthenticationException} thrown when
     * authentication fails during login or password update.
     * Example: invalid credentials, inactive account.
     *
     * @param ex the exception containing the error message
     * @return {@code 401 Unauthorized} with error details
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthentication(
            AuthenticationException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * Handles {@link ResourceNotFoundException} thrown when
     * a requested resource does not exist in the database.
     * Example: user not found by ID.
     *
     * @param ex the exception containing the error message
     * @return {@code 404 Not Found} with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(
            ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles {@link MethodArgumentNotValidException} thrown when
     * {@code @Valid} annotation validation fails on request body fields.
     * Returns a map of field names to their validation error messages.
     * Example: missing email, invalid email format.
     *
     * @param ex the exception containing field validation errors
     * @return {@code 400 Bad Request} with map of field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(
                        err.getField(), err.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles all unhandled exceptions as a fallback.
     * Prevents internal error details from being exposed to the client.
     *
     * @param ex the unhandled exception
     * @return {@code 500 Internal Server Error} with generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }

    /**
     * Builds a consistent error response body.
     *
     * @param status  the HTTP status to return
     * @param message the error message to include in the response body
     * @return {@link ResponseEntity} with error map and given status
     */
    private ResponseEntity<Map<String, String>> buildResponse(
            HttpStatus status, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}