package com.sch.hub_manager_service.hub.service;

import com.sch.hub_manager_service.domain.model.persistency.Charger;
import com.sch.hub_manager_service.domain.model.persistency.ChargerOperationalState;
import com.sch.hub_manager_service.domain.repository.ChargerRepository;
import com.sch.hub_manager_service.hub.dto.response.ChargerStateDTO;
import com.sch.hub_manager_service.hub.dto.response.HubStateDTO;
import com.sch.hub_manager_service.hub.mapper.HubMapper;
import com.sch.hub_manager_service.hub.service.exception.ChargerNotFoundException;
import com.sch.hub_manager_service.hub.service.exception.SameChargerOperationalStateException;
import com.sch.hub_manager_service.domain.model.state.ChargerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BackofficeService {

    private final ChargerRepository chargerRepository;
    private final HubStateManager hubStateManager;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BackofficeService(ChargerRepository chargerRepository, HubStateManager hubStateManager) {
        this.chargerRepository = chargerRepository;
        this.hubStateManager = hubStateManager;
    }

    private Charger findChargerById(String id) {
        Optional<Charger> charger = chargerRepository.findById(id);
        return charger.orElseThrow(() -> new ChargerNotFoundException("Colonnina non trovata"));
    }

    private List<Charger> getAllChargers() {
        List<Charger> chargers = chargerRepository.findAll();
        if (chargers.isEmpty())
            throw new ChargerNotFoundException("Nessuna colonnina trovata");

        return chargers;
    }

    public HubStateDTO getHubState() {
        List<Charger> chargers = getAllChargers();

        return HubMapper.toHubStateDTO(chargers, hubStateManager.getCurrentStateMap());
    }

    public ChargerStateDTO getChargerStateById(String chargerId) {
        Charger charger = findChargerById(chargerId);
        ChargerState chargerState = hubStateManager.getChargerCurrentState(chargerId);

        return HubMapper.toChargerStateDTO(charger, chargerState);
    }

    public void changeChargerOperationalState(String chargerId, ChargerOperationalState newOperationalState) {
        Charger selectedCharger = findChargerById(chargerId);
        ChargerOperationalState currentOperationalState = selectedCharger.getChargerOperationalState();

        if (currentOperationalState.equals(newOperationalState)) {
            throw new SameChargerOperationalStateException("");
        } else {
            selectedCharger.setChargerOperationalState(newOperationalState);
            chargerRepository.save(selectedCharger);

            hubStateManager.updateChargerOperationalState(chargerId, newOperationalState);
        }
    }

}
