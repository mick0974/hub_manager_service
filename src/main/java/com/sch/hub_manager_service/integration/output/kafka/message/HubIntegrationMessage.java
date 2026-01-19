package com.sch.hub_manager_service.integration.output.kafka.message;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sch.hub_manager_service.hub.dto.ReservationDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@JsonSerialize
public class HubIntegrationMessage<T> {

    private String hubId;
    private ChangeType eventType;
    private T payload;
    private List<ReservationDTO> hubReservations;

    public enum ChangeType {
        CHARGER_OPERATIONAL_STATE_CHANGED,
        CHARGER_METRICS_CHANGED
    }
}
