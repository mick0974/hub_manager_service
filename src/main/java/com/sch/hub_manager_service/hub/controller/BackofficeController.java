package com.sch.hub_manager_service.hub.controller;

import com.sch.hub_manager_service.domain.model.persistency.ChargerOperationalState;
import com.sch.hub_manager_service.hub.dto.response.ChargerStateDTO;
import com.sch.hub_manager_service.hub.dto.response.HubStateDTO;
import com.sch.hub_manager_service.hub.service.BackofficeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hub/backoffice")
public class BackofficeController {

    private final BackofficeService backofficeService;

    public BackofficeController(BackofficeService backofficeService) {
        this.backofficeService = backofficeService;
    }

    @GetMapping("/charger")
    public ResponseEntity<HubStateDTO> getAllChargersState() {
        HubStateDTO hubStateDTO = backofficeService.getHubState();
        return ResponseEntity.status(HttpStatus.OK).body(hubStateDTO);
    }

    @GetMapping("/charger/{chargerId}")
    public ResponseEntity<ChargerStateDTO> getChargerState(@PathVariable String chargerId) {
        ChargerStateDTO chargerStateDTO = backofficeService.getChargerStateById(chargerId);
        return ResponseEntity.status(HttpStatus.OK).body(chargerStateDTO);
    }

    @PutMapping("/hub/charger/{chargerId}/enable")
    public ResponseEntity<ChargerStateDTO> activateCharger(@PathVariable String chargerId) {
        backofficeService.changeChargerOperationalState(chargerId, ChargerOperationalState.ACTIVE);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/hub/charger/{chargerId}/disable")
    public ResponseEntity<ChargerStateDTO> deactivateCharger(@PathVariable String chargerId) {
        backofficeService.changeChargerOperationalState(chargerId, ChargerOperationalState.INACTIVE);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/hub/charger/{chargerId}/poweron")
    public ResponseEntity<ChargerStateDTO> powerOnCharger(@PathVariable String chargerId) {
        backofficeService.changeChargerOperationalState(chargerId, ChargerOperationalState.ON);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/hub/charger/{chargerId}/poweroff")
    public ResponseEntity<ChargerStateDTO> powerOffColonnina(@PathVariable String chargerId) {
        backofficeService.changeChargerOperationalState(chargerId, ChargerOperationalState.OFF);
        return ResponseEntity.status(HttpStatus.OK).build();
    }






    /*
    @GetMapping("/hub/charger/{chargerId}")
    public ResponseEntity<Object> getChargerStatus(@PathVariable String chargerId) {

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PutMapping("/hub/charger/{chargerId}/enable")
    public ResponseEntity<Object> enableColonnina(@PathVariable String chargerId) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/hub/charger/{chargerId}/disable")
    public ResponseEntity<Object> disableColonnina(@PathVariable String chargerId) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/hub/charger/{chargerId}/poweron")
    public ResponseEntity<Object> powerOnColonnina(@PathVariable String chargerId) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/hub/charger/{chargerId}/poweroff")
    public ResponseEntity<Object> powerOffColonnina(@PathVariable String chargerId) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/hub/charger/{chargerId}/status")
    public ResponseEntity<Object> getColonninaStatus(@PathVariable String chargerId) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/transaction")
    public ResponseEntity<Object> getTransactions() {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

     */
}
