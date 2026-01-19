package com.sch.hub_manager_service.hub.service.exception;

import com.sch.hub_manager_service.domain.model.state.ChargerOperationalState;

public class InvalidChargerStateTransitionException extends RuntimeException {
    public InvalidChargerStateTransitionException(ChargerOperationalState previousState, ChargerOperationalState newState) {
        super("Transizione non valida da stato '%s' a stato '%s'".formatted(previousState, newState));
    }
}
