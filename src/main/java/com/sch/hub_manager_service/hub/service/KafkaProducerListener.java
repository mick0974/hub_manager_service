package com.sch.hub_manager_service.hub.service;

import com.sch.hub_manager_service.domain.model.state.ChargerState;
import com.sch.hub_manager_service.hub.event.ChargerStateChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaProducerListener {

    private final KafkaTemplate<String, ChargerStateChangedEvent> kafkaTemplate;

    @Value("${hub.target}")
    private String targetHubId;

    private static final String TOPIC = "template";
    private final Logger logger = LoggerFactory.getLogger(KafkaProducerListener.class);

    public KafkaProducerListener(KafkaTemplate<String, ChargerStateChangedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener
    public void onChargerStateChanged(ChargerStateChangedEvent event) {
        logger.info("[KafkaProducerListener] Received state changed event: {}", event);

        CompletableFuture<SendResult<String, ChargerStateChangedEvent>> future = kafkaTemplate.send(TOPIC, targetHubId, event);

        future.whenComplete((r, e) -> {
            if (e != null) {
                logger.error("[KafkaProducerListener] Errore durante l'invio degli stati aggiornati", e);
            } else {
                logger.info("[KafkaProducerListener] Aggiornamento inviato con successo");
            }
        });
    }
}
