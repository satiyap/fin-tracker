package com.fintracker.api.exception;

import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.exception.ValidationException;
import com.fintracker.util.LoggingUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        LoggingUtils.setErrorCode("NOT_FOUND");
        log.warn("Resource not found: {}", ex.getMessage());
        
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
        
        LoggingUtils.clearErrorCode();
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex, WebRequest request) {
        LoggingUtils.setErrorCode("VALIDATION_ERROR");
        log.warn("Validation error: {}", ex.getMessage());
        
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
        
        LoggingUtils.clearErrorCode();
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        LoggingUtils.setErrorCode("INVALID_INPUT");
        log.warn("Invalid input data: {} fields have validation errors", ex.getBindingResult().getErrorCount());
        
        ValidationErrorResponse response = new ValidationErrorResponse();
        List<ValidationErrorResponse.Violation> violations = new ArrayList<>();
        
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            ValidationErrorResponse.Violation violation = new ValidationErrorResponse.Violation(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
            violations.add(violation);
            log.debug("Validation error on field '{}': {}", fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        response.setViolations(violations);
        LoggingUtils.clearErrorCode();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        LoggingUtils.setErrorCode("INVALID_CREDENTIALS");
        log.warn("Authentication failed: invalid credentials");
        
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Invalid username or password")
                .path(request.getDescription(false))
                .build();
        
        LoggingUtils.clearErrorCode();
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        LoggingUtils.setErrorCode("ACCESS_DENIED");
        log.warn("Access denied: {}", ex.getMessage());
        
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("You do not have permission to access this resource")
                .path(request.getDescription(false))
                .build();
        
        LoggingUtils.clearErrorCode();
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        
        LoggingUtils.setErrorCode("DATA_INTEGRITY");
        log.error("Database constraint violation: {}", ex.getMessage());
        
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message("Data integrity violation. The operation conflicts with existing data.")
                .path(request.getDescription(false))
                .build();
        
        LoggingUtils.clearErrorCode();
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        LoggingUtils.setErrorCode("TYPE_MISMATCH");
        log.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());
        
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(String.format("Invalid value for parameter '%s'. Expected type: %s",
                        ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"))
                .path(request.getDescription(false))
                .build();
        
        LoggingUtils.clearErrorCode();
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex, WebRequest request) {
        LoggingUtils.setErrorCode("INTERNAL_ERROR");
        log.error("Unexpected error occurred", ex);
        
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred")
                .path(request.getDescription(false))
                .build();
        
        LoggingUtils.clearErrorCode();
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}