package com.sch.hub_manager_service.hub.service;

import com.sch.hub_manager_service.domain.model.persistency.ChargerOperationalState;
import com.sch.hub_manager_service.domain.model.state.ChargerMetrics;
import com.sch.hub_manager_service.domain.model.state.ChargerState;
import com.sch.hub_manager_service.hub.event.ChargerStateChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class HubStateManagerTest {

    private ApplicationEventPublisher eventPublisher;
    private HubStateManager hubStateManager;

    @BeforeEach
    void setUp() {
        eventPublisher = mock(ApplicationEventPublisher.class);
        hubStateManager = new HubStateManager(eventPublisher);
    }

    private void initializeCache(String chargerId, ChargerMetrics metrics) {
        Map<String, ChargerMetrics> update = Map.of(chargerId, metrics);
        hubStateManager.updateFromSimulation(update);
        reset(eventPublisher);
    }

    private void initializeChargerOperationalState(String chargerId, ChargerOperationalState operationalState) {
        hubStateManager.updateChargerOperationalState(chargerId, operationalState);
        reset(eventPublisher);
    }

    @Test
    void updateFromSimulation_firstInsert_shouldPublishEvent() {
        // Eseguo il caso da testare
        Map<String, ChargerMetrics> update = Map.of("charger-1", mock(ChargerMetrics.class));
        hubStateManager.updateFromSimulation(update);

        // Verifico che:
        // - Il nuovo stato sia salvato sulla cache
        // - Venga generato 1 evento di aggiornamento di stato
        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();
        assertThat(state.getOperationalState()).isEqualTo(ChargerOperationalState.ACTIVE);

        verify(eventPublisher, times(1))
                .publishEvent(any(ChargerStateChangedEvent.class));
    }

    @Test
    void updateFromSimulation_noChanges_shouldNotPublishEvent() {
        // Inizializzo la cache
        ChargerMetrics metrics = mock(ChargerMetrics.class);
        initializeCache("charger-1", metrics);

        // Eseguo il caso da testare
        Map<String, ChargerMetrics> simulationUpdate = Map.of("charger-1", metrics);
        hubStateManager.updateFromSimulation(simulationUpdate);

        // Verifico che:
        // - Che il numero di stati presenti sia 1 e sia associato a "charger-1"
        // - Non venga generato alcun evento
        assertThat(hubStateManager.getCurrentStateMap().size()).isEqualTo(1);

        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();

        verify(eventPublisher, never()).publishEvent(any(ChargerStateChangedEvent.class));
    }

    @Test
    void updateFromSimulation_metricsChanges_shouldPublishEvent() {
        // Inizializzo la cache
        initializeCache("charger-1", mock(ChargerMetrics.class));

        // Eseguo il caso da testare
        Map<String, ChargerMetrics> simulationUpdate = Map.of("charger-1", mock(ChargerMetrics.class));
        hubStateManager.updateFromSimulation(simulationUpdate);

        // Verifico che:
        // - Che il numero di stati presenti sia 1 e sia associato a "charger-1"
        // - Venga generato 1 evento di aggiornamento di stato associato a "charger-1"
        assertThat(hubStateManager.getCurrentStateMap().size()).isEqualTo(1);

        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();

        ArgumentCaptor<ChargerStateChangedEvent> captor = ArgumentCaptor.forClass(ChargerStateChangedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        ChargerStateChangedEvent event = captor.getValue();
        assertThat(event.getNewStates().size()).isEqualTo(1);
        assertThat(event.getNewStates().getFirst().getChargerId())
                .isEqualTo("charger-1");
    }

    @Test
    void updateChargerOperationalState_stateChanged_shouldPublishEvent() {
        // Inizializzo la cache
        initializeCache("charger-1", mock(ChargerMetrics.class));

        // Eseguo il caso da testare
        ChargerOperationalState newOperationalState = ChargerOperationalState.OFF;
        hubStateManager.updateChargerOperationalState("charger-1", newOperationalState);

        // Verifico che:
        // - L'OperationalState di "charger-1" sia cambiato correttamente
        // - Venga generato 1 evento di aggiornamento di stato associato a "charger-1"
        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();
        assertThat(state.getOperationalState()).isEqualTo(newOperationalState);

        verify(eventPublisher, times(1)).publishEvent(any(ChargerStateChangedEvent.class));
    }

    @Test
    void updateChargerOperationalState_sameState_shouldNotPublishEvent() {
        // Inizializzo la cache
        initializeCache("charger-1", mock(ChargerMetrics.class));

        // Aggiorno lo stato
        ChargerOperationalState newOperationalState = ChargerOperationalState.ACTIVE;
        hubStateManager.updateChargerOperationalState("charger-1", newOperationalState);

        // Verifico che:
        // - L'OperationalState di "charger-1" sia cambiato correttamente
        // - Venga generato 1 evento di aggiornamento di stato associato a "charger-1"
        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();
        assertThat(state.getOperationalState()).isEqualTo(newOperationalState);

        verify(eventPublisher, never()).publishEvent(any(ChargerStateChangedEvent.class));
    }

    @Test
    void updateChargerOperationalState_unknownCharger_shouldDoNothing() {
        // Eseguo il caso da testare
        hubStateManager.updateChargerOperationalState(
                "unknown",
                ChargerOperationalState.ON
        );

        // Verifico che:
        // - Nessuno stato venga salvato
        // - Non venga generato alcun evento
        assertThat(hubStateManager.getCurrentStateMap().size()).isZero();
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateFromSimulation_operationalStateOFF_shouldNotPublishEvent() {
        // Inizializzo la cache
        ChargerMetrics cachedMetrics = mock(ChargerMetrics.class);
        ChargerOperationalState cachedOperationalState = ChargerOperationalState.OFF;
        initializeCache("charger-1", cachedMetrics);
        initializeChargerOperationalState("charger-1", cachedOperationalState);

        // Eseguo il caso da testare
        Map<String, ChargerMetrics> simulationUpdate = Map.of("charger-1", mock(ChargerMetrics.class));
        hubStateManager.updateFromSimulation(simulationUpdate);

        // Verifico che:
        // - Lo stato non venga aggiornato (charger inattivo)
        // - Non venga generato alcun evento
        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();
        assertThat(state.getOperationalState()).isEqualTo(cachedOperationalState);
        assertThat(state.getMetrics()).isSameAs(cachedMetrics);

        verify(eventPublisher, never()).publishEvent(any(ChargerStateChangedEvent.class));
    }

    @Test
    void updateFromSimulation_operationalStateINACTIVE_shouldNotPublishEvent() {
        // Inizializzo la cache
        ChargerMetrics cachedMetrics = mock(ChargerMetrics.class);
        ChargerOperationalState cachedOperationalState = ChargerOperationalState.INACTIVE;
        initializeCache("charger-1", cachedMetrics);
        initializeChargerOperationalState("charger-1", cachedOperationalState);

        // Eseguo il caso da testare
        Map<String, ChargerMetrics> simulationUpdate = Map.of("charger-1", mock(ChargerMetrics.class));
        hubStateManager.updateFromSimulation(simulationUpdate);

        // Verifico che:
        // - Lo stato non venga aggiornato (charger inattivo)
        // - Non venga generato alcun evento
        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();
        assertThat(state.getOperationalState()).isEqualTo(cachedOperationalState);
        assertThat(state.getMetrics()).isSameAs(cachedMetrics);

        verify(eventPublisher, never()).publishEvent(any(ChargerStateChangedEvent.class));
    }
}
