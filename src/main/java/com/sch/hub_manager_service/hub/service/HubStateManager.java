package com.sch.hub_manager_service.hub.service;

import com.sch.hub_manager_service.domain.model.state.*;
import com.sch.hub_manager_service.hub.event.ChargerMetricsChangedEvent;
import com.sch.hub_manager_service.hub.event.ChargerOperationalStateChangedEvent;
import com.sch.hub_manager_service.hub.event.data.ChargerMetricsChange;
import com.sch.hub_manager_service.hub.service.exception.ChargerStateNotAvailableException;
import com.sch.hub_manager_service.hub.service.exception.HubNotInitializedException;
import com.sch.hub_manager_service.hub.service.exception.InvalidChargerStateTransitionException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class HubStateManager {

    private HubState hubState = null;
    private final ApplicationEventPublisher eventPublisher;

    public HubStateManager(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public synchronized void initChargersStates(List<Pair<String, PlugType>> initialStates) {
        Map<String, ChargerState> chargerStates = new HashMap<>();
        initialStates.forEach(pair ->
                chargerStates.put(pair.getFirst(), new ChargerState(pair.getFirst(), pair.getSecond())));

        hubState = new HubState();
        hubState.setAggregatedMetrics(new HubMetrics());
        hubState.setSimulationTimestamp(0.0);
        hubState.setChargersStates(chargerStates);
    }

    public synchronized void updateFromSimulation(HubMetrics hubMetrics, Map<String, ChargerMetrics> chargerMetrics, Double simulationTimestamp) {
        List<ChargerMetricsChange> changedStates = new ArrayList<>();

        hubState.setAggregatedMetrics(hubMetrics);
        hubState.setSimulationTimestamp(simulationTimestamp);
        for (Map.Entry<String, ChargerMetrics> entry : chargerMetrics.entrySet()) {
            String chargerId = entry.getKey();
            ChargerState currentState = hubState.getChargersStates().get(chargerId);

            if (currentState == null) {
                log.warn("[HubStateManager] Ricevuto stato per connettore non esistente in memoria: {}", chargerId);
            } else {
                boolean updated = currentState.updateFromSimulation(entry.getValue());
                if (updated)
                    changedStates.add(new ChargerMetricsChange(chargerId, currentState.getMetrics().deepCopy()));
            }
        }

        // Notifico il cambiamento di stato
        if (!changedStates.isEmpty()) {
            eventPublisher.publishEvent(new ChargerMetricsChangedEvent(hubMetrics, changedStates, simulationTimestamp));
        } else {
            log.info("[HubStateManager] Nessuno stato aggiornato");
        }
    }

    public synchronized void updateChargerOperationalState(String chargerId, ChargerOperationalState newOperationalState) {
        ChargerState chargerState = getChargerState(chargerId);

        if (!chargerState.updateOperationalState(newOperationalState)) {
            throw new InvalidChargerStateTransitionException(
                    chargerState.getChargerOperationalState(), newOperationalState
            );
        }

        // Simulo lo spegnimento della colonnina settando a 0 lo stato restituito dal simulatore
        if (newOperationalState.equals(ChargerOperationalState.INACTIVE) || newOperationalState.equals(ChargerOperationalState.OFF)) {
            chargerState.clearMetrics();
        }

        eventPublisher.publishEvent(
                new ChargerOperationalStateChangedEvent(chargerId, newOperationalState)
        );
    }

    public synchronized HubState getHubState() {
        if (hubState == null)
            throw new HubNotInitializedException("Hub non ancora inizializzato");
        return hubState.deepCopy();
    }

    public synchronized ChargerState getChargerState(String chargerId) {
        ChargerState state = getHubState().getChargerState(chargerId);
        if (state == null) {
            throw new ChargerStateNotAvailableException("Stato non presente per la colonnina: " + chargerId);
        }
        return state;
    }


}

