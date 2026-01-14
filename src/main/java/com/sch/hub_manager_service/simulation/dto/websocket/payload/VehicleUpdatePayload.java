package com.sch.hub_manager_service.simulation.dto.websocket.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(description = "payload complessivo aggiornamento veicolo via WebSocket")
public class VehicleUpdatePayload {

    // Getter e setter
    private Double timestamp;

    @Schema(description = "List di veicoli con i loro stati")
    private List<VehicleStatus> vehicles;

    // Costruttore vuoto
    public VehicleUpdatePayload() {
    }

    // Costruttore completo
    public VehicleUpdatePayload(Double timestamp, List<VehicleStatus> vehicles) {
        this.timestamp = timestamp;
        this.vehicles = vehicles;
    }

    // toString()
    @Override
    public String toString() {
        return "VehicleUpdatePayload{" +
                "timestamp=" + timestamp +
                ", vehicles=" + vehicles +
                '}';
    }
}
