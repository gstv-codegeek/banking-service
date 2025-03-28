package com.silentowl.banking_app.handler;

import com.silentowl.banking_app.exceptions.TransactionException;
import com.silentowl.banking_app.exceptions.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> errors = new HashMap<>();
        e.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    if (error instanceof FieldError fieldError) {
                        errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                    }
                });
        response.put("status", "error");
        response.put("message", "Validation failed");
        response.put("errors", errors);

        log.warn("Validation error: {}", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", e.getMessage());
        log.warn("Authentication failed: {}", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", e.getMessage());
        log.warn("Entity not found {}", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(UsernameNotFoundException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("Username not found: {}", e.getMessage());
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", e.getMessage());
        log.warn("User not found: {}", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionException(TransactionException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", e.getMessage());
        log.error("Transaction error: {}", e.getMessage(), e);
        return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
