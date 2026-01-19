package com.sch.hub_manager_service.domain.model.state;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class HubMetrics {
    private double energy = 0.0;    // energia totale consumata dall’hub
    private int occupancy = 0;      // numero di veicoli in carica

    public HubMetrics deepCopy() {
        return new HubMetrics(energy, occupancy);
    }
}
