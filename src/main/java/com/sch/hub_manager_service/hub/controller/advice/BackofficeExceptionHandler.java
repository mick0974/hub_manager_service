package com.sch.hub_manager_service.hub.controller.advice;

import com.sch.hub_manager_service.hub.dto.response.ApiError;
import com.sch.hub_manager_service.hub.service.exception.ChargerNotFoundException;
import com.sch.hub_manager_service.hub.service.exception.ChargerStateNotFoundException;
import com.sch.hub_manager_service.hub.service.exception.SameChargerOperationalStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BackofficeExceptionHandler {

    @ExceptionHandler(ChargerNotFoundException.class)
    public ResponseEntity<ApiError> handleChargerStateNotFoundException(ChargerNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiError(e.getMessage())
        );
    }

    @ExceptionHandler(ChargerStateNotFoundException.class)
    public ResponseEntity<ApiError> handleChargerStateNotFoundException(ChargerStateNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiError(e.getMessage())
        );
    }

    @ExceptionHandler(SameChargerOperationalStateException.class)
    public ResponseEntity<ApiError> handleSameChargerOperationalStateException(SameChargerOperationalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiError(e.getMessage())
        );
    }
}
