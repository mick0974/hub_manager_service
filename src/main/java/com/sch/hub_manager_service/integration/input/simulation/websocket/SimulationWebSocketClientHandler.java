package com.sch.hub_manager_service.integration.input.simulation.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sch.hub_manager_service.domain.model.state.ChargerMetrics;
import com.sch.hub_manager_service.domain.model.state.HubMetrics;
import com.sch.hub_manager_service.hub.service.HubStateManager;
import com.sch.hub_manager_service.integration.input.simulation.dto.websocket.WebSocketUpdate;
import com.sch.hub_manager_service.integration.input.simulation.dto.websocket.payload.HubStatusPayload;
import com.sch.hub_manager_service.integration.input.simulation.dto.websocket.payload.TimeStepPayload;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Component
public class SimulationWebSocketClientHandler extends TextWebSocketHandler {

    private final HubStateManager hubStateManager;
    private final ObjectMapper objectMapper;
    @Value("${hub.target}")
    private String targetHubId;

    public SimulationWebSocketClientHandler(HubStateManager hubStateManager, ObjectMapper objectMapper) {
        this.hubStateManager = hubStateManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("[WebSocket] Connessione alla Simulazione completata con successo");
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        log.info("[WebSocket] Messaggio ricevuto: {}", message.getPayload());
        String payload = message.getPayload();

        try {
            JsonNode messageRoot = objectMapper.readTree(payload);
            JsonNode typeNode = messageRoot.get("type");

            if (typeNode == null || !typeNode.isTextual()) {
                log.error("[WebSocket] Il messaggio ricevuto non contiene il campo \"type\"");
                return;
            }

            String type = typeNode.asText();
            switch (type) {
                case "TimeStepUpdate" -> handleTimeStepPayload(payload);
                default -> log.warn("[WebSocket] Ricevuto messaggio con 'type' sconosciuto: {}", type);
            }
        } catch (JsonProcessingException e) {
            log.error("[WebSocket] Errore durante l'elaborazione del payload JSON ricevuto nel messaggio: {}", e.getMessage());
        }
    }

    private void handleTimeStepPayload(String payload) throws JsonProcessingException {
        WebSocketUpdate<TimeStepPayload> message =
                objectMapper.readValue(
                        payload,
                        new TypeReference<WebSocketUpdate<TimeStepPayload>>() {
                        }
                );

        log.info("[WebSocket] Ricevuto messaggio di tipo TimeStepPayload");

        Optional<HubStatusPayload> hubTargetOpt = message.getPayload().getHubs().stream()
                .filter(dto -> dto.getHubId().equals(targetHubId))
                .findFirst();

        if (hubTargetOpt.isEmpty()) {
            log.info("[WebSocket] Il messaggio ricevuto non aggiorna lo stato dell'hub target {}", targetHubId);
            return;
        }

        HubStatusPayload hubTarget = hubTargetOpt.get();

        if (hubTarget.getChargers() == null || hubTarget.getChargers().isEmpty()) {
            log.info("[WebSocket] Nessun aggiornamento colonnine per l'hub target {}", targetHubId);
            return;
        }

        HubMetrics hubMetrics = new HubMetrics(hubTarget.getEnergy(), hubTarget.getOccupancy());
        Map<String, ChargerMetrics> chargerMetrics = new HashMap<>();
        hubTarget.getChargers().forEach((chargerId, metrics) ->
                chargerMetrics.put(chargerId, new ChargerMetrics(metrics.getEnergy(), metrics.isOccupied())));

        log.info("[WebSocket] Aggiornamento elaborato con successo, invio dati a HubStateManager");
        hubStateManager.updateFromSimulation(hubMetrics, chargerMetrics, message.getPayload().getTimestamp());
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, Throwable exception) {
        log.error("[WebSocket] Errore di trasporto: {}", exception.getMessage());

    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        log.info("[WebSocket] Connessione alla simulazione chiusa con stato {}", status);
    }

}
