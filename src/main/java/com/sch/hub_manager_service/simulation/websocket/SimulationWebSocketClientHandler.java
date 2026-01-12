package com.sch.hub_manager_service.simulation.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sch.hub_manager_service.hub.service.HubStateManager;
import com.sch.hub_manager_service.domain.model.state.ChargerMetrics;
import com.sch.hub_manager_service.simulation.websocket.dto.WebSocketUpdate;
import com.sch.hub_manager_service.simulation.websocket.dto.payload.ChargerStatus;
import com.sch.hub_manager_service.simulation.websocket.dto.payload.HubStatusPayload;
import com.sch.hub_manager_service.simulation.websocket.dto.payload.TimeStepPayload;
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

@Component
public class SimulationWebSocketClientHandler extends TextWebSocketHandler {
    @Value("${hub.target}")
    private String targetHubId;

    private final HubStateManager hubStateManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(SimulationWebSocketClientHandler.class);
    private WebSocketSession session;

    public SimulationWebSocketClientHandler(HubStateManager hubStateManager) {
        this.hubStateManager = hubStateManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.session = session;
        logger.info("[WebSocket] Connessione alla Simulazione completata con successo");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        logger.info("[WebSocket] Messaggio ricevuto: {}", message.getPayload());
        String payload = message.getPayload();
        try {
            JsonNode messageRoot = objectMapper.readTree(payload);
            String messageType = messageRoot.get("type").asText();

            switch (messageType) {
                case "TimeStepUpdate":
                    handleTimeStepPayload(payload);
                    break;
                default:
                    logger.warn("[WebSocket] Ricevuto messaggio con 'type' sconosciuto: {}", messageType);
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

        HubStatusPayload hubTarget = message.getPayload().getHubs().stream()
                .filter(dto -> dto.getHubId().equals(targetHubId))
                .findFirst()
                .orElse(null);

        if (hubTarget == null) {
            logger.info("[WebSocket] Il messaggio ricevuto non aggiorna lo stato dell'hub target {}", targetHubId);
            return;
        } else if (hubTarget.getChargers().isEmpty()) {
            logger.info("[WebSocket] Il messaggio ricevuto non contiene aggiornamento per le colonnine dell'hub target {}", targetHubId);
            return;
        }

        Map<String, ChargerMetrics> update = new HashMap<>();
        for (String chargerId : hubTarget.getChargers().keySet()) {
            ChargerStatus status = message.getPayload().getHubs().getFirst().getChargers().get(chargerId);
            update.put(chargerId, new ChargerMetrics(status.getEnergy(), status.isOccupied()));
        }

        hubStateManager.updateFromSimulation(update);

    }

    @Override
    public void handleTransportError(
            WebSocketSession session,
            Throwable exception) {

        logger.error("[WebSocket] Errore di trasporto: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status) {

        logger.info("[WS] Connessione alla simulazione chiusa con stato {}", status);
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }
}
