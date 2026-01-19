package com.sch.hub_manager_service.hub.event;

import com.sch.hub_manager_service.domain.model.state.ChargerOperationalState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import tools.jackson.databind.annotation.JsonSerialize;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonSerialize
public class ChargerOperationalStateChangedEvent {
    private String chargerId;
    private ChargerOperationalState newChargerOperationalState;
}
