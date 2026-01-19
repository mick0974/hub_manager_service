package com.sch.hub_manager_service.domain.repository.specification;

import com.sch.hub_manager_service.domain.model.persistency.Reservation;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationSpecification {

    public static Specification<Reservation> hasDate(LocalDate reservationDate) {
        return (root, query, criteriaBuilder) -> {
            if (reservationDate == null)
                return null;

            return criteriaBuilder.equal(root.get("reservationDate"), reservationDate);
        };
    }

    public static Specification<Reservation> hasDateAfter(LocalDate reservationDate) {
        return (root, query, criteriaBuilder) -> {
            if (reservationDate == null)
                return null;

            return criteriaBuilder.greaterThan(root.get("reservationDate"), reservationDate);
        };
    }

    public static Specification<Reservation> isBetweenTimeInterval(LocalTime startTime, LocalTime endTime) {
        return (root, query, criteriaBuilder) -> {
            if (startTime == null || endTime == null)
                return null;

            return criteriaBuilder.and(
                    criteriaBuilder.between(root.get("startTime"), startTime, endTime),
                    criteriaBuilder.between(root.get("endTime"), startTime, endTime));
        };
    }
}
