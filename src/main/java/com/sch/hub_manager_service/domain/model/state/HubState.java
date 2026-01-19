package com.sch.hub_manager_service.domain.model.state;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class HubState {
    private HubMetrics aggregatedMetrics;                                       // metriche aggregate dell'hub
    private Map<String, ChargerState> chargersStates = new HashMap<>();          // stato dei connettori
    private Double simulationTimestamp;                                      // istante di aggiornamento nel simulatore

    public HubState deepCopy() {
        HubState copy = new HubState();
        copy.aggregatedMetrics = aggregatedMetrics.deepCopy();
        copy.chargersStates = Map.copyOf(chargersStates);
        copy.simulationTimestamp = simulationTimestamp;

        return copy;
    }

    public ChargerState getChargerState(String chargerId) {
        return chargersStates.get(chargerId);
    }
}
