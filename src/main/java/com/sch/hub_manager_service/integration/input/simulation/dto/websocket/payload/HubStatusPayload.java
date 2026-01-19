package com.sch.hub_manager_service.integration.input.simulation.dto.websocket.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Schema(description = "payload aggiornamento hub via WebSocket")
public class HubStatusPayload {

    private String hubId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private double energy;                          // energia totale consumata dall’hub
    private int occupancy;                          // numero di veicoli in carica
    private Map<String, ChargerStatus> chargers;    // stato dei charger

    public HubStatusPayload() {
    }

    public HubStatusPayload(String hubId, double energy, int occupancy, Map<String, ChargerStatus> chargers) {
        this.hubId = hubId;
        this.energy = energy;
        this.occupancy = occupancy;
        this.chargers = chargers;
    }

    @Override
    public String toString() {
        return "HubStatusPayload{" +
                "hubId='" + hubId + '\'' +
                ", energy=" + energy +
                ", occupancy=" + occupancy +
                ", chargers=" + chargers +
                '}';
    }
}
