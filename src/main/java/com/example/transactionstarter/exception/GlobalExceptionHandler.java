package com.example.transactionstarter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, Object> response = new HashMap<>();

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        response.put("error", "VALIDATION_ERROR");
        response.put("message", "Request validation failed");
        response.put("fields", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }


    // Transaction not found
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(
            TransactionNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "TRANSACTION_NOT_FOUND",
                        "message", ex.getMessage()
                ));
    }


    // Duplicate transaction
    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(
            DuplicateTransactionException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "DUPLICATE_TRANSACTION",
                        "message", ex.getMessage()
                ));
    }


    // Invalid status transition
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidStatus(
            InvalidStatusTransitionException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "INVALID_STATUS_TRANSITION",
                        "message", ex.getMessage()
                ));
    }


    // Invalid business rule
    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTransaction(
            InvalidTransactionException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "INVALID_TRANSACTION",
                        "message", ex.getMessage()
                ));
    }
}