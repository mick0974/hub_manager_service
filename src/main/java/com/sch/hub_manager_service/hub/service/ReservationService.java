package com.sch.hub_manager_service.hub.service;

import com.sch.hub_manager_service.domain.model.persistency.Reservation;
import com.sch.hub_manager_service.domain.repository.ReservationRepository;
import com.sch.hub_manager_service.domain.repository.specification.ReservationSpecification;
import com.sch.hub_manager_service.hub.dto.ReservationDTO;
import com.sch.hub_manager_service.hub.mapper.ReservationMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationDTO> getReservationsBy(LocalDate reservationDate, LocalTime startTime, LocalTime endTime) {
        if ((startTime != null && endTime == null) || (startTime == null && endTime != null)) {
            throw new IllegalArgumentException("startTime ed endTime devono essere entrambi presenti o entrambi assenti");
        }

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("startTime non può essere successivo a endTime");
        }

        Specification<Reservation> spec =
                ReservationSpecification.hasDate(reservationDate)
                        .and(ReservationSpecification.isBetweenTimeInterval(startTime, endTime));

        List<Reservation> reservations = reservationRepository.findAll(spec);
        return ReservationMapper.toReservationDTOs(reservations);
    }

    public List<ReservationDTO> getAllReservations() {
        return ReservationMapper.toReservationDTOs(reservationRepository.findAll());
    }

    public void addNewReservation(ReservationDTO reservationDTO) {
        Reservation newReservation = ReservationMapper.toReservation(reservationDTO);
        reservationRepository.save(newReservation);
    }
}
