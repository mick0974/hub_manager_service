package com.sch.hub_manager_service.hub.service;

import com.sch.hub_manager_service.domain.model.state.ChargerOperationalState;
import com.sch.hub_manager_service.domain.model.state.ChargerState;
import com.sch.hub_manager_service.domain.model.state.HubState;
import com.sch.hub_manager_service.hub.dto.response.ChargerStateDTO;
import com.sch.hub_manager_service.hub.dto.response.HubStateDTO;
import com.sch.hub_manager_service.hub.dto.response.HubStructureDTO;
import com.sch.hub_manager_service.hub.mapper.HubStateMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class HubManagementService {

    private final HubStateManager hubStateManager;

    public HubManagementService(HubStateManager hubStateManager) {
        this.hubStateManager = hubStateManager;
    }

    private ChargerState findChargerStateById(String id) {
        return hubStateManager.getChargerState(id);
    }

    public HubStructureDTO getHubStructure() {
        List<HubStructureDTO.ChargerStructureDTO> structureDTOS = new ArrayList<>();
        HubState hubState = hubStateManager.getHubState();
        hubState.getChargersStates().forEach((charger, state) ->
                structureDTOS.add(new HubStructureDTO.ChargerStructureDTO(charger, state.getPlugType())));
        return new HubStructureDTO(structureDTOS);
    }

    public HubStateDTO getHubState() {
        HubState hubState = hubStateManager.getHubState();
        return HubStateMapper.toHubStateDTO(hubState);
    }

    public ChargerStateDTO getChargerStateById(String chargerId) {
        ChargerState chargerState = findChargerStateById(chargerId);
        return HubStateMapper.toChargerStateDTO(chargerState);
    }

    public void changeChargerOperationalState(String chargerId, ChargerOperationalState newOperationalState) {
        hubStateManager.updateChargerOperationalState(chargerId, newOperationalState);
    }

}
