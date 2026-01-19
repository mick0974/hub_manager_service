package com.sch.hub_manager_service.hub.controller.advice;

import com.sch.hub_manager_service.hub.dto.response.ApiError;
import com.sch.hub_manager_service.hub.service.exception.ChargerStateNotAvailableException;
import com.sch.hub_manager_service.hub.service.exception.HubNotInitializedException;
import com.sch.hub_manager_service.hub.service.exception.InvalidChargerStateTransitionException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class BackofficeExceptionHandler {

    @ExceptionHandler(HubNotInitializedException.class)
    public ResponseEntity<ApiError> handleHubNotInitializedException(HubNotInitializedException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        e.getMessage(),
                        Timestamp.from(Instant.now())
                )
        );
    }

    @ExceptionHandler(ChargerStateNotAvailableException.class)
    public ResponseEntity<ApiError> handleChargerStateNotFoundException(ChargerStateNotAvailableException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiError(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage(),
                        Timestamp.from(Instant.now())
                )
        );
    }

    @ExceptionHandler(InvalidChargerStateTransitionException.class)
    public ResponseEntity<ApiError> handleSameChargerOperationalStateException(InvalidChargerStateTransitionException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiError(
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        e.getMessage(),
                        Timestamp.from(Instant.now())
                )
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException e) {
        List<ApiError.ValidationError> errors = new ArrayList<>();
        e.getConstraintViolations().forEach(violation -> errors.add(ApiError.ValidationError.builder()
                .field(violation.getPropertyPath().toString())
                .message(violation.getMessage())
                .rejectedValue(String.valueOf(violation.getInvalidValue()))
                .build()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Corpo della richiesta non valido",
                Timestamp.from(Instant.now()),
                errors
        ));
    }
}
