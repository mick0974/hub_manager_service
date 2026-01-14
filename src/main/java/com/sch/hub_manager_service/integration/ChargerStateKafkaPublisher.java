package com.sch.hub_manager_service.integration;

import com.sch.hub_manager_service.hub.event.ChargerStateChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChargerStateKafkaPublisher {

    private static final String TOPIC = "template";
    private final KafkaTemplate<String, ChargerStateChangedEvent> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(ChargerStateKafkaPublisher.class);

    @Value("${hub.target}")
    private String targetHubId;

    public ChargerStateKafkaPublisher(KafkaTemplate<String, ChargerStateChangedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener
    public void onChargerStateChanged(ChargerStateChangedEvent event) {
        logger.info("[KafkaProducerListener] Received state changed event: {}", event);

        /*
        CompletableFuture<SendResult<String, ChargerStateChangedEvent>> future = kafkaTemplate.send(TOPIC, targetHubId, event);

        future.whenComplete((r, e) -> {
            if (e != null) {
                logger.error("[KafkaProducerListener] Errore durante l'invio degli stati aggiornati", e);
            } else {
                logger.info("[KafkaProducerListener] Aggiornamento inviato con successo");
            }
        });

         */
    }
}
