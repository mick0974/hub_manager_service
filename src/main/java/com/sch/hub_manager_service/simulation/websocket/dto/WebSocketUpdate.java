package com.sch.hub_manager_service.simulation.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe base generica per tutti i messaggi di aggiornamento su WebSocket.
 *
 * @param <T> Il tipo di dato specifico contenuto nel payload.
 */
@Setter
@Getter
@Schema(description = "Messaggio aggiornamento via WebSocket ha un campo payload generico da adattare in base al messaggio")
public class WebSocketUpdate<T> {

    // Getter e setter
    @Schema(description = "Tipo messaggio")
    private String type;

    @Schema(description = "Progresso simulazione")
    private double progress;

    @Schema(description = "Messaggio di stato opzionale")
    @JsonProperty(required = false)
    private String statusMessage;

    @Schema(description = "Payload personalizzabile")
    private T payload;

    // Costruttore vuoto
    public WebSocketUpdate() {
    }

    // Costruttore completo
    public WebSocketUpdate(String type, double progress, T payload, String statusMessage) {
        this.type = type;
        this.progress = progress;
        this.payload = payload;
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        return "WebSocketUpdate{" +
                "type='" + type + '\'' +
                ", progress=" + progress +
                ", payload=" + payload +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
