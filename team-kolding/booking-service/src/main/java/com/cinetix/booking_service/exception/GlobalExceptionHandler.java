package com.cinetix.booking_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Centralized error response body
    public record ErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, String> validationErrors
    ) {}

    private ErrorResponse build(HttpStatus status, String message, String path) {
        return new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                null
        );
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFound(BookingNotFoundException ex, HttpServletRequest request) {
        log.warn("Booking not found: {}", ex.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(build(status, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleSeatAlreadyBooked(SeatAlreadyBookedException ex, HttpServletRequest request) {
        log.warn("Seat already booked: {}", ex.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(build(status, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fe) {
                fieldErrors.put(fe.getField(), fe.getDefaultMessage());
            } else {
                fieldErrors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });
        String summary = "Validation failed for " + ex.getBindingResult().getObjectName() + " (" + fieldErrors.size() + " error(s))";
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                summary,
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.debug("Request body unreadable", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Malformed JSON request";
        return ResponseEntity.status(status).body(build(status, message, request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        // Unexpected error; log full stack
        log.error("Unhandled exception", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred";
        return ResponseEntity.status(status).body(build(status, message, request.getRequestURI()));
    }
}

