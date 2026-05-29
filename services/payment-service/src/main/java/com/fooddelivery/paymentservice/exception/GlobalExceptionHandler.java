package com.fooddelivery.paymentservice.exception;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ← handles @Valid validation errors → 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 400);
        body.put("error", "Bad Request");

        // collect all field errors
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        body.put("message", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    // ← handles invalid enum value → 400
    @ExceptionHandler(
            org.springframework.http.converter
                    .HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(
            Exception ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body: " + ex.getMessage());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            PaymentNotFoundException ex) {
        return buildResponse(
                HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PaymentAlreadyDoneException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyDone(
            PaymentAlreadyDoneException ex) {
        return buildResponse(
                HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(
            Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}