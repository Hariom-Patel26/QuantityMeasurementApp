package com.app.quantitymeasurement.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));

        logger.warning("Validation failed: " + errorMessage);

        return ResponseEntity.badRequest().body(buildErrorBody(
            HttpStatus.BAD_REQUEST.value(),
            "Quantity Measurement Error",
            errorMessage,
            ex.getBindingResult().getObjectName()
        ));
    }


    @ExceptionHandler(QuantityMeasurementException.class)
    public ResponseEntity<Map<String, Object>> handleQuantityException(
            QuantityMeasurementException ex,
            HttpServletRequest request) {

        logger.warning("QuantityMeasurementException: " + ex.getMessage());

        return ResponseEntity.badRequest().body(buildErrorBody(
            HttpStatus.BAD_REQUEST.value(),
            "Quantity Measurement Error",
            ex.getMessage(),
            request.getRequestURI()
        ));
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        logger.warning("IllegalArgumentException: " + ex.getMessage());

        return ResponseEntity.badRequest().body(buildErrorBody(
            HttpStatus.BAD_REQUEST.value(),
            "Quantity Measurement Error",
            ex.getMessage(),
            request.getRequestURI()
        ));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        logger.severe("Unhandled exception: " + ex.getMessage());

        return ResponseEntity.internalServerError().body(buildErrorBody(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage(),
            request.getRequestURI()
        ));
    }


    private Map<String, Object> buildErrorBody(int status, String error,
                                               String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status);
        body.put("error",     error);
        body.put("message",   message);
        body.put("path",      path);
        return body;
    }
}
