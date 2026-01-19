package com.sch.hub_manager_service.hub.event;

import com.sch.hub_manager_service.domain.model.state.HubMetrics;
import com.sch.hub_manager_service.hub.event.data.ChargerMetricsChange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import tools.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
@JsonSerialize
public class ChargerMetricsChangedEvent {
    private HubMetrics aggregatedHubMetrics;                        // metriche aggregate dell'hub
    private List<ChargerMetricsChange> newChargerStates;                 // stati modificati dei connettori nel timestamp indicato
    private Double simulationTimestamp;                          // istante di aggiornamento nel simulatore

}
