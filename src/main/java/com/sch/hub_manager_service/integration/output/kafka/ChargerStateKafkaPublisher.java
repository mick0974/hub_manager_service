package com.sch.hub_manager_service.integration.output.kafka;

import com.sch.hub_manager_service.domain.model.persistency.Reservation;
import com.sch.hub_manager_service.domain.repository.ReservationRepository;
import com.sch.hub_manager_service.domain.repository.specification.ReservationSpecification;
import com.sch.hub_manager_service.hub.event.ChargerMetricsChangedEvent;
import com.sch.hub_manager_service.hub.event.ChargerOperationalStateChangedEvent;
import com.sch.hub_manager_service.hub.mapper.ReservationMapper;
import com.sch.hub_manager_service.integration.output.kafka.message.HubIntegrationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class ChargerStateKafkaPublisher {

    private final KafkaTemplate<String, HubIntegrationMessage<?>> kafkaTemplate;
    private final ReservationRepository reservationRepository;
    @Value("${hub.target}")
    private String targetHubId;
    @Value("${kafka.topic}")
    private String topic = "template";
    @Value("${kafka.key}")
    private String key = "key";

    public ChargerStateKafkaPublisher(KafkaTemplate<String, HubIntegrationMessage<?>> kafkaTemplate, ReservationRepository reservationRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.reservationRepository = reservationRepository;
    }

    @EventListener
    public void onChargerOperationalStateChanged(ChargerOperationalStateChangedEvent event) {
        log.info("[ChargerStateKafkaPublisher] Ricevuto cambio stato operativo connettore: {}", event);

        Specification<Reservation> spec =
                ReservationSpecification.hasDate(LocalDate.now())
                        .or(ReservationSpecification.hasDateAfter(LocalDate.now()));

        HubIntegrationMessage<ChargerOperationalStateChangedEvent> message = new HubIntegrationMessage<>(
                targetHubId,
                HubIntegrationMessage.ChangeType.CHARGER_OPERATIONAL_STATE_CHANGED,
                event,
                ReservationMapper.toReservationDTOs(reservationRepository.findAll(spec))
        );

        sendMessage(message);
    }

    @EventListener
    public void onChargerStateChanged(ChargerMetricsChangedEvent event) {
        log.info("[ChargerStateKafkaPublisher] Ricevuto cambio stato metriche hub: {}", event);

        Specification<Reservation> spec =
                ReservationSpecification.hasDate(LocalDate.now())
                        .or(ReservationSpecification.hasDateAfter(LocalDate.now()));

        HubIntegrationMessage<ChargerMetricsChangedEvent> message = new HubIntegrationMessage<>(
                targetHubId,
                HubIntegrationMessage.ChangeType.CHARGER_METRICS_CHANGED,
                event,
                ReservationMapper.toReservationDTOs(reservationRepository.findAll(spec))
        );

        sendMessage(message);
    }

    private void sendMessage(HubIntegrationMessage<?> event) {
        log.info("Evento inviato: {}", event);

        /*
        kafkaTemplate.send(topic, targetHubId, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[KafkaPublisher] Errore invio evento {}", event, ex);
                    } else {
                        log.info(
                                "[KafkaPublisher] Evento inviato (partition={}, offset={})",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }
                });

         */
    }
}
