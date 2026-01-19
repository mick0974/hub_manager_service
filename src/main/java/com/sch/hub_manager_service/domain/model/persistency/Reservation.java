package com.sch.hub_manager_service.domain.model.persistency;

import com.sch.hub_manager_service.domain.model.state.PlugType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "reservations",
        indexes = {
                @Index(name = "reservation_date_index", columnList = "reservation_date"),
                @Index(name = "reservation_time_index", columnList = "start_time, end_time"),
                @Index(name = "reservation_date_time_index", columnList = "reservation_date, start_time, end_time")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "only_one_reservation_per_charger_at_time", columnNames = {"charger_id", "reservation_date", "start_time"})
        })
@NoArgsConstructor
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "plug_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlugType reservedPlug;

    @Column(name = "charger_id", nullable = true)
    private String chargerId;

    @CreationTimestamp
    @Column(name = "audit_created_at", nullable = false)
    private Timestamp createdAt;
}
