package com.sch.hub_manager_service.hub.service;

import com.sch.hub_manager_service.domain.model.persistency.ChargerOperationalState;
import com.sch.hub_manager_service.domain.model.state.ChargerMetrics;
import com.sch.hub_manager_service.domain.model.state.ChargerState;
import com.sch.hub_manager_service.hub.event.ChargerStateChangedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HubStateManager {

    private final Map<String, ChargerState> currentStateMap = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public HubStateManager(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void updateFromSimulation(Map<String, ChargerMetrics> simulatorStates) {
        List<ChargerState> changedStates = new ArrayList<>();

        for (Map.Entry<String, ChargerMetrics> entry : simulatorStates.entrySet()) {
            String chargerId = entry.getKey();
            ChargerMetrics metrics = entry.getValue();

            ChargerState currentState = currentStateMap.get(chargerId);

            if (currentState == null) {
                currentState = new ChargerState(chargerId, ChargerOperationalState.ACTIVE, metrics);
                currentStateMap.put(chargerId, currentState);
                changedStates.add(currentState);
            } else {
                boolean updated = currentState.updateFromSimulation(entry.getValue());
                if (updated)
                    changedStates.add(currentState);
            }
        }

        // Notifico il cambiamento di stato
        if (!changedStates.isEmpty()) {
            eventPublisher.publishEvent(new ChargerStateChangedEvent(changedStates));
        }
    }

    public void updateChargerOperationalState(String chargerId, ChargerOperationalState newOperationalState) {
        ChargerState currentState = currentStateMap.get(chargerId);
        if (currentState == null || currentState.getOperationalState() == newOperationalState) {
            return;
        }

        currentState.setOperationalState(newOperationalState);

        // Notifico il cambiamento di stato solo se la colonnina viene spenda o disattivata. La notifica della riaccensione
        // verrà inviata da updateFromSimulation() quando lo stato varrà aggiornato
        if (newOperationalState.equals(ChargerOperationalState.INACTIVE) || newOperationalState.equals(ChargerOperationalState.OFF)) {
            eventPublisher.publishEvent(
                    new ChargerStateChangedEvent(List.of(currentState))
            );
        }
    }

    public Map<String, ChargerState> getCurrentStateMap() {
        return Map.copyOf(currentStateMap);
    }

    public ChargerState getChargerCurrentState(String chargerId) {
        return currentStateMap.get(chargerId);
    }
}
