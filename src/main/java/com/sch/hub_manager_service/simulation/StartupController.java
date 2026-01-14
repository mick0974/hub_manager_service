package com.sch.hub_manager_service.simulation;

import com.sch.hub_manager_service.domain.model.persistency.Charger;
import com.sch.hub_manager_service.domain.repository.ChargerRepository;
import com.sch.hub_manager_service.simulation.dto.api.HubDTO;
import com.sch.hub_manager_service.simulation.dto.api.HubListDTO;
import com.sch.hub_manager_service.simulation.mapper.SimulationMapper;
import com.sch.hub_manager_service.simulation.websocket.SimulationWebSocketClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/hub/startup")
@Tag(
        name = "Startup API",
        description = "API esposte per la connessione alla simulazione e all'inizializzazione dell'hub."
)
public class StartupController {

    private final RestTemplate restTemplate;
    private final ChargerRepository chargerRepository;
    private final SimulationWebSocketClient wsClient;
    private final Logger logger = LoggerFactory.getLogger(StartupController.class);

    @Value("${simulation.api.baseUrl}")
    private String apiBaseUrl;
    @Value("${hub.target}")
    private String targetHubId;

    public StartupController(RestTemplate restTemplate, ChargerRepository chargerRepository, SimulationWebSocketClient wsClient) {
        this.restTemplate = restTemplate;
        this.chargerRepository = chargerRepository;
        this.wsClient = wsClient;
    }

    @Operation(
        summary = "Avvia la connessione WebSocket con il simulatore.",
        description = """
            Avvia in modo asincrono la connessione WebSocket verso il simulatore.
            La risposta indica solo l'accettazione della richiesta,
            la connessione potrebbe non essere ancora stabilita al momento della risposta.
            """
    )
    @ApiResponse(
            responseCode = "202",
            description = "Richiesta accettata, connessione WebSocket in fase di instaurazione",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(
                            example = "Richiesta accettata, connessione in corso..."
                    )
            )
    )
    @PostMapping("/ws/connect")
    public ResponseEntity<String> connectToSimulation() {
        wsClient.connect();

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Richiesta accettata, connessione in corso...");
    }

    @Operation(
            summary = "Restituisce lo stato corrente della connessione alla simulazione.",
            description = """
            Restituisce lo stato della connessione alla simulazione. Gli stati previsti sono:
             - Non connesso: nessuna connessione stabilita o si è verificato un errore;
             - Connesso: connessione alla simulazione stabilita;
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Stato della connessione alla simulazione",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(
                            example = "Connesso"
                    )
            )
    )
    @GetMapping("/ws/status")
    public ResponseEntity<String> verifyConnectionToSimulation() {
        String state = wsClient.isConnected() ? "Connesso" : "Non connesso";

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(state);
    }

    @Operation(
            summary = "Termina la connessione alla simulazione.",
            description = """
            Richiede la chiusura della connessione instaurata con la simulazione
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Connessione chiusa",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(
                            example = "Connessione terminata"
                    )
            )
    )
    @PostMapping("/ws/disconnect")
    public ResponseEntity<String> disconnectFromSimulation() {
        wsClient.disconnect();

        return ResponseEntity.status(HttpStatus.OK).body("Connessione chiusa con successo");
    }

    @Operation(
            summary = "Inizializza l'hub con i dati del simulatore",
            description = """
                Richiede al simulatore la lista degli hub disponibili,
                seleziona l'hub configurato e salva le colonnine associate.
                L'operazione è consentita solo se l'hub non è già stato inizializzato.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Hub inizializzato correttamente",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(example = "Dati hub ricevuti e salvati con successo")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Hub già popolato",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(example = "Hub già popolato")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hub configurato non trovato nei dati del simulatore",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(example = "Hub con id HUB_1 non trovato")
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Simulatore non pronto o dati non ancora disponibili",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(example = "Dati hub non ancora disponibili nella simulazione")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Errore interno o configurazione non valida",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(example = "Hub da gestire non indicato, configurazione non valida")
                    )
            )
    })
    @PostMapping("/populate")
    public ResponseEntity<String> populateHub() {

        if (targetHubId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Hub da gestire non indicato, configurazione non valida");
        }

        if (chargerRepository.count() != 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Hub già popolato");
        }

        ResponseEntity<HubListDTO> response = restTemplate.exchange(
                apiBaseUrl + "/simulation/hubs",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                HubListDTO.class
        );

        if (response.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Simulazione non avviata");
        }

        if (response.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Dati hub non ancora disponibili nella simulazione");
        }

        HubListDTO body = response.getBody();
        if (body == null || body.getHubs() == null || body.getHubs().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Nessun dato hub ricevuto dal simulatore");
        }

        HubDTO selectedHub = body.getHubs().stream()
                .filter(dto -> dto.getHubId().equals(targetHubId))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Hub con id " + targetHubId + " non trovato"
                        )
                );

        List<Charger> chargers = selectedHub.getChargers().stream()
                .map(SimulationMapper::mapToCharger)
                .toList();

        chargerRepository.saveAll(chargers);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Dati hub ricevuti e salvati con successo");
    }

}
