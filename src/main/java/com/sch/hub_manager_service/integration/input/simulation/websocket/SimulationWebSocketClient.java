package com.sch.hub_manager_service.integration.input.simulation.websocket;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Component
public class SimulationWebSocketClient {

    private final WebSocketClient wsClient;
    private final SimulationWebSocketClientHandler wsHandler;
    private final Logger logger = LoggerFactory.getLogger(SimulationWebSocketClient.class);
    @Value("${simulation.ws.url}")
    private String wsUrl;
    private WebSocketConnectionManager connectionManager;

    public SimulationWebSocketClient(SimulationWebSocketClientHandler wsHandler) {
        this.wsHandler = wsHandler;
        this.wsClient = new StandardWebSocketClient();
    }

    @PostConstruct
    private void initializeConnectionManager() {
        this.connectionManager = new WebSocketConnectionManager(wsClient, wsHandler, wsUrl);
        connectionManager.setAutoStartup(false);
    }

    public void connect() {
        if (!connectionManager.isConnected())
            connectionManager.stop();

        connectionManager.start();
    }

    public void disconnect() {
        if (connectionManager.isRunning()) {
            connectionManager.stop();
        }
    }

    public boolean isConnected() {
        return connectionManager.isConnected();
    }

    @PreDestroy
    private void shutdown() {
        disconnect();
    }
}
