package com.sch.hub_manager_service.hub.mapper;

import com.sch.hub_manager_service.domain.model.state.ChargerState;
import com.sch.hub_manager_service.domain.model.state.HubState;
import com.sch.hub_manager_service.hub.dto.response.ChargerStateDTO;
import com.sch.hub_manager_service.hub.dto.response.HubStateDTO;

import java.util.ArrayList;
import java.util.List;

public class HubStateMapper {

    public static HubStateDTO toHubStateDTO(HubState hubState) {
        HubStateDTO dto = new HubStateDTO();
        dto.setOccupancy(hubState.getAggregatedMetrics().getOccupancy());
        dto.setEnergy(hubState.getAggregatedMetrics().getEnergy());
        List<ChargerStateDTO> chargerStateDTOs = new ArrayList<>();
        hubState.getChargersStates().forEach((chargerId, chargerState) ->
                chargerStateDTOs.add(toChargerStateDTO(chargerState)));

        dto.setChargerStates(chargerStateDTOs);
        return dto;
    }

    public static ChargerStateDTO toChargerStateDTO(ChargerState chargerState) {
        ChargerStateDTO dto = new ChargerStateDTO();

        dto.setChargerId(chargerState.getChargerId());
        dto.setChargerOperationalState(chargerState.getChargerOperationalState());

        dto.setEnergy(chargerState.getMetrics().getEnergy());
        dto.setOccupied(chargerState.getMetrics().isOccupied());

        return dto;
    }
}
