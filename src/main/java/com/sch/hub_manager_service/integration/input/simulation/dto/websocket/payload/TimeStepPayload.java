package com.sch.hub_manager_service.integration.input.simulation.dto.websocket.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(description = "payload aggiornamento hub e veicoli ad ogni timestep via WebSocket")
public class TimeStepPayload {

    // Getter e setter
    private Double timestamp;

    @Schema(description = "List dei veicoli con i loro stati")
    private List<VehicleStatus> vehicles;

    @Schema(description = "List di hub con i loro stati")
    private List<HubStatusPayload> hubs;

    // Costruttore vuoto
    public TimeStepPayload() {
    }

    // Costruttore completo
    public TimeStepPayload(Double timestamp, List<VehicleStatus> vehicles, List<HubStatusPayload> hubs) {
        this.timestamp = timestamp;
        this.vehicles = vehicles;
        this.hubs = hubs;
    }

    // toString()
    @Override
    public String toString() {
        return "TimeStepPayload{" +
                "timestamp=" + timestamp +
                ", vehicles=" + vehicles +
                ", hubs=" + hubs +
                '}';
    }
}
