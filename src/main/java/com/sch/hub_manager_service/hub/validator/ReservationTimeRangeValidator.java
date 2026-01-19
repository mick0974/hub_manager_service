package com.sch.hub_manager_service.hub.validator;

import com.sch.hub_manager_service.hub.dto.ReservationDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReservationTimeRangeValidator implements ConstraintValidator<ValidReservationTimeRange, ReservationDTO> {

    @Override
    public boolean isValid(ReservationDTO reservationDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (reservationDTO == null)
            return true;

        if (reservationDTO.getStartTime() == null || reservationDTO.getEndTime() == null)
            return false;

        return reservationDTO.getStartTime().isBefore(reservationDTO.getEndTime());
    }
}
