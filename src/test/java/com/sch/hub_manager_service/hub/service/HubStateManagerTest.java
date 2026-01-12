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

    @Test
    void updateFromSimulation_firstInsert_shouldPublishEvent() {
        // Creo i mock
        ChargerMetrics metrics = mock(ChargerMetrics.class);
        Map<String, ChargerMetrics> newState =
                Map.of("charger-1", metrics);

        // Aggiorno lo stato
        hubStateManager.updateFromSimulation(newState);

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
    void updateFromSimulation_NoChanges_shouldNotPublishEvent() {
        // Creo i mock
        ChargerMetrics metrics = mock(ChargerMetrics.class);
        Map<String, ChargerMetrics> previousState = Map.of("charger-1", metrics);
        Map<String, ChargerMetrics> newState = Map.of("charger-1", metrics);

        // Salvo lo stato precedente in memoria e resetto gli eventi
        hubStateManager.updateFromSimulation(previousState);
        reset(eventPublisher);

        // Aggiorno lo stato
        hubStateManager.updateFromSimulation(newState);

        // Verifico che:
        // - Che il numero di stati presenti sia 1 e sia associato a "charger-1"
        // - Non venga generato alcun evento
        assertThat(hubStateManager.getCurrentStateMap().size()).isEqualTo(1);

        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();

        verify(eventPublisher, never()).publishEvent(any(ChargerStateChangedEvent.class));
    }

    @Test
    void updateFromSimulation_MetricsChanges_shouldPublishEvent() {
        // Creo i mock
        Map<String, ChargerMetrics> previousState = Map.of("charger-1", mock(ChargerMetrics.class));
        Map<String, ChargerMetrics> newState = Map.of("charger-1", mock(ChargerMetrics.class));

        // Salvo lo stato precedente in memoria e resetto gli eventi
        hubStateManager.updateFromSimulation(previousState);
        reset(eventPublisher);

        // Aggiorno lo stato
        hubStateManager.updateFromSimulation(newState);

        // Verifico che:
        // - Che il numero di stati presenti sia 1 e sia associato a "charger-1"
        // - Venga generato 1 evento di aggiornamento di stato associato a "charger-1"
        assertThat(hubStateManager.getCurrentStateMap().size()).isEqualTo(1);

        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();

        ArgumentCaptor<ChargerStateChangedEvent> captor =
                ArgumentCaptor.forClass(ChargerStateChangedEvent.class);

        verify(eventPublisher).publishEvent(captor.capture());

        ChargerStateChangedEvent event = captor.getValue();
        assertThat(event.getNewStates().size()).isEqualTo(1);
        assertThat(event.getNewStates().getFirst().getChargerId())
                .isEqualTo("charger-1");
    }

    @Test
    void updateChargerOperationalState_newState_shouldPublishEvent() {
        // Creo i mock
        ChargerOperationalState newOperationalState = ChargerOperationalState.OFF;
        ChargerMetrics metrics = mock(ChargerMetrics.class);
        Map<String, ChargerMetrics> newState =
                Map.of("charger-1", metrics);

        // Salvo lo stato precedente in memoria e resetto gli eventi
        hubStateManager.updateFromSimulation(newState);
        reset(eventPublisher);

        // Aggiorno lo stato
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
        // Creo i
        ChargerOperationalState newOperationalState = ChargerOperationalState.ACTIVE;
        ChargerMetrics metrics = mock(ChargerMetrics.class);
        Map<String, ChargerMetrics> newState =
                Map.of("charger-1", metrics);

        // Salvo lo stato precedente in memoria e resetto gli eventi
        hubStateManager.updateFromSimulation(newState);
        reset(eventPublisher);

        // Aggiorno lo stato
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
        // Aggiorno l'OperationalState di un charger non salvato in memoria
        hubStateManager.updateChargerOperationalState(
                "unknown",
                ChargerOperationalState.ON
        );

        // Verifico che:
        // - Nessuno stato venga salvato
        // - Non venga generato alcun evento
        assertThat(hubStateManager.getCurrentStateMap().size()).isEqualTo(0);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateFromSimulation_operationalStateOFF_shouldNotPublishEvent() {
        // Creo i mock
        ChargerOperationalState operationalState = ChargerOperationalState.OFF;
        ChargerMetrics previousMetrics = mock(ChargerMetrics.class);
        Map<String, ChargerMetrics> previousState = Map.of("charger-1", previousMetrics);
        Map<String, ChargerMetrics> newState = Map.of("charger-1", mock(ChargerMetrics.class));

        // Salvo lo stato precedente in memoria e resetto gli eventi
        hubStateManager.updateFromSimulation(previousState);
        hubStateManager.updateChargerOperationalState("charger-1", operationalState);
        reset(eventPublisher);

        // Aggiorno lo stato
        hubStateManager.updateFromSimulation(newState);

        // Verifico che:
        // - Lo stato non venga aggiornato (charger inattivo)
        // - Non venga generato alcun evento
        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();
        assertThat(state.getOperationalState()).isEqualTo(operationalState);
        assertThat(state.getMetrics()).isSameAs(previousMetrics);

        verify(eventPublisher, never()).publishEvent(any(ChargerStateChangedEvent.class));
    }

    @Test
    void updateFromSimulation_operationalStateINACTIVE_shouldNotPublishEvent() {
        // Creo i mock
        ChargerOperationalState operationalState = ChargerOperationalState.INACTIVE;
        ChargerMetrics previousMetrics = mock(ChargerMetrics.class);
        Map<String, ChargerMetrics> previousState = Map.of("charger-1", previousMetrics);
        Map<String, ChargerMetrics> newState = Map.of("charger-1", mock(ChargerMetrics.class));

        // Salvo lo stato precedente in memoria e resetto gli eventi
        hubStateManager.updateFromSimulation(previousState);
        hubStateManager.updateChargerOperationalState("charger-1", operationalState);
        reset(eventPublisher);

        // Aggiorno lo stato
        hubStateManager.updateFromSimulation(newState);

        // Verifico che:
        // - Lo stato non venga aggiornato (charger inattivo)
        // - Non venga generato alcun evento
        ChargerState state = hubStateManager.getChargerCurrentState("charger-1");
        assertThat(state).isNotNull();
        assertThat(state.getOperationalState()).isEqualTo(operationalState);
        assertThat(state.getMetrics()).isSameAs(previousMetrics);

        verify(eventPublisher, never()).publishEvent(any(ChargerStateChangedEvent.class));
    }
}
