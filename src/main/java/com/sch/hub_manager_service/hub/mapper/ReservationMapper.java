package com.sch.hub_manager_service.hub.mapper;

import com.sch.hub_manager_service.domain.model.persistency.Reservation;
import com.sch.hub_manager_service.hub.dto.ReservationDTO;

import java.util.ArrayList;
import java.util.List;

public class ReservationMapper {

    public static List<ReservationDTO> toReservationDTOs(List<Reservation> reservations) {
        List<ReservationDTO> reservationDTOs = new ArrayList<>();
        reservations.forEach(reservation -> reservationDTOs.add(toReservationDTO(reservation)));
        return reservationDTOs;
    }

    public static ReservationDTO toReservationDTO(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setVehicleId(reservation.getVehicleId());
        reservationDTO.setReservationDate(reservation.getReservationDate());
        reservationDTO.setStartTime(reservation.getStartTime());
        reservationDTO.setEndTime(reservation.getEndTime());
        reservationDTO.setReservedPlug(reservation.getReservedPlug());
        reservationDTO.setChargerId(reservation.getChargerId());

        return reservationDTO;
    }

    public static Reservation toReservation(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        reservation.setVehicleId(reservationDTO.getVehicleId());
        reservation.setReservationDate(reservationDTO.getReservationDate());
        reservation.setStartTime(reservationDTO.getStartTime());
        reservation.setEndTime(reservationDTO.getEndTime());
        reservation.setReservedPlug(reservationDTO.getReservedPlug());
        reservation.setChargerId(reservationDTO.getChargerId());
        return reservation;
    }
}
