package com.sch.hub_manager_service.simulation.dto.websocket.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "payload complessivo aggiornamento hub via WebSocket")
public class HubUpdatePayload {

    // Getter e setter
    private Double timestamp;

    @Schema(description = "List di hub con i loro stati")
    private List<HubStatusPayload> hubs;

    // Costruttore vuoto
    public HubUpdatePayload() {
    }

    // Costruttore completo
    public HubUpdatePayload(Double timestamp, List<HubStatusPayload> hubs) {
        this.timestamp = timestamp;
        this.hubs = hubs;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public void setHubs(List<HubStatusPayload> hubs) {
        this.hubs = hubs;
    }

    // toString()
    @Override
    public String toString() {
        return "HubUpdatePayload{" +
                "timestamp=" + timestamp +
                ", hubs=" + hubs +
                '}';
    }
}
