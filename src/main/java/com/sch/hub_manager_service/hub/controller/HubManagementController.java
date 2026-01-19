package com.sch.hub_manager_service.hub.controller;

import com.sch.hub_manager_service.domain.model.state.ChargerOperationalState;
import com.sch.hub_manager_service.hub.dto.response.ApiError;
import com.sch.hub_manager_service.hub.dto.response.ChargerStateDTO;
import com.sch.hub_manager_service.hub.dto.response.HubStateDTO;
import com.sch.hub_manager_service.hub.dto.response.HubStructureDTO;
import com.sch.hub_manager_service.hub.service.HubManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/hub/management")
@Tag(name = "Hub Management controller", description = "Gestione operativa dell'hub")
public class HubManagementController {

    private final HubManagementService hubManagementService;

    public HubManagementController(HubManagementService hubManagementService) {
        this.hubManagementService = hubManagementService;
    }

    @GetMapping(value = "/structure", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Restituisce la struttura dell'hub",
            description = "Restituisce informazioni riguardo la struttura dell'hub, quali connettori disponibili e relative prese"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Struttura dell'hub restituita correttamente",
                    content = @Content(schema = @Schema(implementation = HubStructureDTO.class))),
            @ApiResponse(responseCode = "500", description = "Hub non ancora inizializzato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<HubStructureDTO> getHubStructure() {
        HubStructureDTO hubStructureDTO = hubManagementService.getHubStructure();
        return ResponseEntity.status(HttpStatus.OK).body(hubStructureDTO);
    }

    @GetMapping(value = "/state", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Restituisce lo stato corrente di tutte le colonnine",
            description = "Restituisce lo stato corrente completo di tutte le colonnine associate all'hub gestito"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stato delle colonnine restituito correttamente",
                    content = @Content(schema = @Schema(implementation = HubStateDTO.class))),
            @ApiResponse(responseCode = "500", description = "Hub non ancora inizializzato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<HubStateDTO> getAllChargersState() {
        HubStateDTO hubStateDTO = hubManagementService.getHubState();
        return ResponseEntity.status(HttpStatus.OK).body(hubStateDTO);
    }

    @GetMapping(value = "/charger/{chargerId}/state", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Restituisce lo stato corrente della colonnina richiesta",
            description = "Restituisce lo stato corrente della colonnina selezionata associato all'hub gestito"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stato della colonnina restituito correttamente",
                    content = @Content(schema = @Schema(implementation = ChargerStateDTO.class))),
            @ApiResponse(responseCode = "404", description = "Colonnina o stato selezionata non trovato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Hub non ancora inizializzato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<ChargerStateDTO> getChargerState(
            @Parameter(description = "Id della colonnina da recuperare", required = true)
            @PathVariable String chargerId
    ) {
        ChargerStateDTO chargerStateDTO = hubManagementService.getChargerStateById(chargerId);
        return ResponseEntity.status(HttpStatus.OK).body(chargerStateDTO);
    }

    @PutMapping("/charger/{chargerId}/activate")
    @Operation(
            summary = "Attiva la colonnina selezionata",
            description = "Modifica lo stato della colonnina selezionata in \"attiva\""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stato della colonnina modificato correttamente"),
            @ApiResponse(responseCode = "404", description = "Colonnina selezionata non trovata",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Impossibile modificare lo stato precedente con il nuovo stato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Hub non ancora inizializzato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<Void> activateCharger(
            @Parameter(description = "Id della colonnina da recuperare", required = true)
            @PathVariable String chargerId
    ) {
        hubManagementService.changeChargerOperationalState(chargerId, ChargerOperationalState.ACTIVE);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/charger/{chargerId}/deactivate")
    @Operation(
            summary = "Disattiva la colonnina selezionata",
            description = "Modifica lo stato della colonnina selezionata in \"disattiva\""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stato della colonnina modificato correttamente"),
            @ApiResponse(responseCode = "404", description = "Colonnina selezionata non trovata",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Impossibile modificare lo stato precedente con il nuovo stato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Hub non ancora inizializzato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<Void> deactivateCharger(
            @Parameter(description = "Id della colonnina da recuperare", required = true)
            @PathVariable String chargerId
    ) {
        hubManagementService.changeChargerOperationalState(chargerId, ChargerOperationalState.INACTIVE);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/charger/{chargerId}/poweron")
    @Operation(
            summary = "Accende la colonnina selezionata",
            description = "Modifica lo stato della colonnina selezionata in \"accesa\""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stato della colonnina modificato correttamente"),
            @ApiResponse(responseCode = "404", description = "Colonnina selezionata non trovata",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Impossibile modificare lo stato precedente con il nuovo stato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Hub non ancora inizializzato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<Void> powerOnCharger(
            @Parameter(description = "Id della colonnina da recuperare", required = true)
            @PathVariable String chargerId
    ) {
        hubManagementService.changeChargerOperationalState(chargerId, ChargerOperationalState.ON);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/charger/{chargerId}/poweroff")
    @Operation(
            summary = "Spegne la colonnina selezionata",
            description = "Modifica lo stato della colonnina selezionata in \"spenta\""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stato della colonnina modificato correttamente"),
            @ApiResponse(responseCode = "404", description = "Colonnina selezionata non trovata",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Impossibile modificare lo stato precedente con il nuovo stato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Hub non ancora inizializzato",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<Void> powerOffColonnina(
            @Parameter(description = "Id della colonnina da recuperare", required = true)
            @PathVariable String chargerId
    ) {
        hubManagementService.changeChargerOperationalState(chargerId, ChargerOperationalState.OFF);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
