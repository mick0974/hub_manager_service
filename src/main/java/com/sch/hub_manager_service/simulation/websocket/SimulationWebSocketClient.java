package com.sch.hub_manager_service.simulation.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SimulationWebSocketClient {
    private final WebSocketClient client;
    private final SimulationWebSocketClientHandler handler;
    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LoggerFactory.getLogger(SimulationWebSocketClient.class);
    @Value("${simulation.ws.url}")
    private String wsUrl;

    public SimulationWebSocketClient(SimulationWebSocketClientHandler handler) {
        this.handler = handler;
        this.client = new StandardWebSocketClient();
    }

    public void connect() {
        logger.info("[WebSocket] Connessione al simulatore in corso...");

        client.execute(handler, wsUrl)
                .whenComplete((session, ex) -> {
                    if (ex != null) {
                        logger.warn("[WebSocket] Connessione fallita, retry in 5 secondi...");
                        retryConnection();
                    } else {
                        logger.info("[WebSocket] WebSocket connessa al simulatore");
                    }
                });
    }


    private void retryConnection() {
        executor.schedule(this::connect, 5, TimeUnit.SECONDS);
    }
}
