package com.sch.hub_manager_service.hub.controller.advice;

import com.sch.hub_manager_service.hub.service.exception.ChargerNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BackofficeExceptionHandler {

    @ExceptionHandler(ChargerNotFoundException.class)
    public ResponseEntity<Object> handleHubNotFoundException(ChargerNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
