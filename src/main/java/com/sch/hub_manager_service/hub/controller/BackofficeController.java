package com.sch.hub_manager_service.hub.controller;

import com.sch.hub_manager_service.domain.model.persistency.ChargerOperationalState;
import com.sch.hub_manager_service.hub.dto.response.ChargerStateDTO;
import com.sch.hub_manager_service.hub.dto.response.HubStateDTO;
import com.sch.hub_manager_service.hub.service.BackofficeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hub/backoffice")
@Tag(
        name = "Backoffice Hub API",
        description = "API esposte al backoffice service"
)
public class BackofficeController {

    private final BackofficeService backofficeService;

    private final Logger logger = LoggerFactory.getLogger(BackofficeController.class);

    public BackofficeController(BackofficeService backofficeService) {
        this.backofficeService = backofficeService;
    }

    @Operation(
            summary = "Restituisce lo stato corrente di tutte le colonnine",
            description = "Restituisce lo stato corrente completo di tutte le colonnine associate all'hub gestito"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Stato delle colonnine restituito correttamente",
            content = @Content(schema = @Schema(implementation = HubStateDTO.class))
    )
    @GetMapping("/charger/all/status")
    public ResponseEntity<HubStateDTO> getAllChargersState() {
        HubStateDTO hubStateDTO = backofficeService.getHubState();
        return ResponseEntity.status(HttpStatus.OK).body(hubStateDTO);
    }

    @Operation(
            summary = "Restituisce lo stato corrente della colonnina richiesta",
            description = "Restituisce lo stato corrente della colonnina selezionata associato all'hub gestito"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Stato delle colonnine restituito correttamente",
            content = @Content(schema = @Schema(implementation = ChargerStateDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Colonnina o stato selezionata non trovato",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @GetMapping("/charger/{chargerId}/status")
    public ResponseEntity<ChargerStateDTO> getChargerState(
            @Parameter(
                    description = "Id della colonnina da recuperare",
                    required = true
            )
            @PathVariable String chargerId
    ) {
        ChargerStateDTO chargerStateDTO = backofficeService.getChargerStateById(chargerId);
        return ResponseEntity.status(HttpStatus.OK).body(chargerStateDTO);
    }

    @Operation(
            summary = "Attiva la colonnina selezionata",
            description = "Modifica lo stato della colonnina selezionata in \"attiva\""
    )
    @ApiResponse(
            responseCode = "200",
            description = "Stato delle colonnine modificato correttamente",
            content = @Content(schema = @Schema(implementation = ChargerStateDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Colonnina selezionata non trovata",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Il nuovo stato selezionato per la colonnina corrisponde al precedente",
            content = @Content(schema = @Schema(implementation = ChargerStateDTO.class))
    )
    @PutMapping("/charger/{chargerId}/activate")
    public ResponseEntity<ChargerStateDTO> activateCharger(
            @Parameter(
                    description = "Id della colonnina da recuperare",
                    required = true
            )
            @PathVariable String chargerId
    ) {
        ChargerStateDTO dto = backofficeService.changeChargerOperationalState(chargerId, ChargerOperationalState.ACTIVE);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Operation(
            summary = "Disattiva la colonnina selezionata",
            description = "Modifica lo stato della colonnina selezionata in \"disattiva\""
    )
    @ApiResponse(
            responseCode = "200",
            description = "Stato delle colonnine modificato correttamente",
            content = @Content(schema = @Schema(implementation = ChargerStateDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Colonnina selezionata non trovata",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Il nuovo stato selezionato per la colonnina corrisponde al precedente",
            content = @Content(schema = @Schema(implementation = ChargerStateDTO.class))
    )
    @PutMapping("/charger/{chargerId}/deactivate")
    public ResponseEntity<ChargerStateDTO> deactivateCharger(
            @Parameter(
                    description = "Id della colonnina da recuperare",
                    required = true
            )
            @PathVariable String chargerId
    ) {
        ChargerStateDTO dto = backofficeService.changeChargerOperationalState(chargerId, ChargerOperationalState.INACTIVE);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Operation(
            summary = "Accende la colonnina selezionata",
            description = "Modifica lo stato della colonnina selezionata in \"accesa\""
    )
    @ApiResponse(
            responseCode = "200",
            description = "Stato delle colonnine modificato correttamente",
            content = @Content(schema = @Schema(implementation = ChargerStateDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Colonnina selezionata non trovata",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Il nuovo stato selezionato per la colonnina corrisponde al precedente",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @PutMapping("/charger/{chargerId}/poweron")
    public ResponseEntity<ChargerStateDTO> powerOnCharger(
            @Parameter(
                    description = "Id della colonnina da recuperare",
                    required = true
            )
            @PathVariable String chargerId
    ) {
        ChargerStateDTO dto = backofficeService.changeChargerOperationalState(chargerId, ChargerOperationalState.ON);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Operation(
            summary = "Spegne la colonnina selezionata",
            description = "Modifica lo stato della colonnina selezionata in \"spenta\""
    )
    @ApiResponse(
            responseCode = "200",
            description = "Stato delle colonnine modificato correttamente",
            content = @Content(schema = @Schema(implementation = ChargerStateDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Colonnina selezionata non trovata",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Il nuovo stato selezionato per la colonnina corrisponde al precedente",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @PutMapping("/charger/{chargerId}/poweroff")
    public ResponseEntity<ChargerStateDTO> powerOffColonnina(
            @Parameter(
                    description = "Id della colonnina da recuperare",
                    required = true
            )
            @PathVariable String chargerId
    ) {
        ChargerStateDTO dto = backofficeService.changeChargerOperationalState(chargerId, ChargerOperationalState.OFF);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}
