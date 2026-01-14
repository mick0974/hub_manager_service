package com.sch.hub_manager_service.simulation.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sch.hub_manager_service.domain.model.state.ChargerMetrics;
import com.sch.hub_manager_service.hub.service.HubStateManager;
import com.sch.hub_manager_service.simulation.dto.websocket.WebSocketUpdate;
import com.sch.hub_manager_service.simulation.dto.websocket.payload.HubStatusPayload;
import com.sch.hub_manager_service.simulation.dto.websocket.payload.TimeStepPayload;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SimulationWebSocketClientHandler extends TextWebSocketHandler {

    @Value("${hub.target}")
    private String targetHubId;

    private final HubStateManager hubStateManager;
    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(SimulationWebSocketClientHandler.class);

    public SimulationWebSocketClientHandler(HubStateManager hubStateManager, ObjectMapper objectMapper) {
        this.hubStateManager = hubStateManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        logger.info("[WebSocket] Connessione alla Simulazione completata con successo");
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        logger.info("[WebSocket] Messaggio ricevuto: {}", message.getPayload());
        String payload = message.getPayload();

        try {
            JsonNode messageRoot = objectMapper.readTree(payload);
            JsonNode typeNode = messageRoot.get("type");

            if (typeNode == null || !typeNode.isTextual()) {
                logger.error("[WebSocket] Il messaggio ricevuto non contiene il campo \"type\"");
                return;
            }

            String type = typeNode.asText();
            switch (type) {
                case "TimeStepUpdate" -> handleTimeStepPayload(payload);
                default -> logger.warn("[WebSocket] Ricevuto messaggio con 'type' sconosciuto: {}", type);
            }
        } catch (JsonProcessingException e) {
            logger.error("[WebSocket] Errore durante l'elaborazione del payload JSON ricevuto nel messaggio: {}", e.getMessage());
        }
    }

    private void handleTimeStepPayload(String payload) throws JsonProcessingException {
        WebSocketUpdate<TimeStepPayload> message =
                objectMapper.readValue(
                        payload,
                        new TypeReference<WebSocketUpdate<TimeStepPayload>>() {
                        }
                );

        logger.info("[WebSocket] Ricevuto messaggio di tipo TimeStepPayload");

        Optional<HubStatusPayload> hubTargetOpt = message.getPayload().getHubs().stream()
                .filter(dto -> dto.getHubId().equals(targetHubId))
                .findFirst();

        if (hubTargetOpt.isEmpty()) {
            logger.info("[WebSocket] Il messaggio ricevuto non aggiorna lo stato dell'hub target {}", targetHubId);
            return;
        }

        HubStatusPayload hubTarget = hubTargetOpt.get();

        if (hubTarget.getChargers() == null || hubTarget.getChargers().isEmpty()) {
            logger.info("[WebSocket] Nessun aggiornamento colonnine per l'hub target {}", targetHubId);
            return;
        }

        Map<String, ChargerMetrics> update = new HashMap<>();
        hubTarget.getChargers().forEach((chargerId, chargerMetrics) ->
                update.put(chargerId, new ChargerMetrics(chargerMetrics.getEnergy(), chargerMetrics.isOccupied())));

        logger.info("[WebSocket] Aggiornamento elaborato con successo, invio dati a HubStateManager");
        hubStateManager.updateFromSimulation(update);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, Throwable exception) {
        logger.error("[WebSocket] Errore di trasporto: {}", exception.getMessage());

    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        logger.info("[WebSocket] Connessione alla simulazione chiusa con stato {}", status);
    }

}
