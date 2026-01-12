package com.sch.hub_manager_service.simulation;

import com.sch.hub_manager_service.domain.model.persistency.Charger;
import com.sch.hub_manager_service.domain.repository.ChargerRepository;
import com.sch.hub_manager_service.hub.service.HubStateManager;
import com.sch.hub_manager_service.simulation.dto.HubDTO;
import com.sch.hub_manager_service.simulation.dto.HubListDTO;
import com.sch.hub_manager_service.simulation.mapper.SimulationMapper;
import com.sch.hub_manager_service.simulation.websocket.SimulationWebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/hub/simulation")
public class StartupController {

    @Value("${simulation.api.baseUrl}")
    private String apiBaseUrl;
    @Value("${hub.target}")
    private String targetHubId;

    private final RestTemplate restTemplate;
    private final ChargerRepository chargerRepository;
    private final SimulationWebSocketClient wsClient;
    private final HubStateManager hubStateManager;

    private final Logger logger = LoggerFactory.getLogger(StartupController.class);

    public StartupController(RestTemplate restTemplate, ChargerRepository chargerRepository, SimulationWebSocketClient wsClient, HubStateManager hubStateManager) {
        this.restTemplate = restTemplate;
        this.chargerRepository = chargerRepository;
        this.wsClient = wsClient;
        this.hubStateManager = hubStateManager;
    }

    @PostMapping("/connect")
    public ResponseEntity<String> startAndConnect() {
        wsClient.connect();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Richiesta accettata, connessione in corso...");
    }

    @PostMapping("/populate")
    public ResponseEntity<String> populateHub() {
        if (targetHubId == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hub da gestire non indicato, impossibile avviare applicazione");

        if (chargerRepository.count() != 0)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Hub già popolato");

        ResponseEntity<HubListDTO> response = restTemplate.getForEntity(
                apiBaseUrl + "/api/simulation/hubs",
                HubListDTO.class
        );

        if (response.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Simulazione non avviata");
        } else if (response.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Dati hub non ancora disponibili nella simulazione");
        }

        HubListDTO body = response.getBody();
        if (body == null || body.getHubs() == null || body.getHubs().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nessun dato hub ricevuto dal simulatore");
        }

        HubDTO selectedHub = body.getHubs().stream()
                .filter(dto -> dto.getHubId().equals(targetHubId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Hub con id " + targetHubId + " non trovato"));

        List<Charger> chargers = selectedHub.getChargers().stream()
                .map(SimulationMapper::mapToCharger)
                .toList();

        logger.info("Colonnine elaborate:  {}", chargers);
        chargerRepository.saveAll(chargers);

        return ResponseEntity.status(HttpStatus.CREATED).body("Dati hub ricevuti e salvati con successo");
    }
}
