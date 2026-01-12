package com.sch.hub_manager_service.hub.mapper;

import com.sch.hub_manager_service.domain.model.persistency.Charger;
import com.sch.hub_manager_service.domain.model.persistency.Plug;
import com.sch.hub_manager_service.hub.dto.response.ChargerStateDTO;
import com.sch.hub_manager_service.hub.dto.response.HubStateDTO;
import com.sch.hub_manager_service.hub.dto.response.PlugStateDTO;
import com.sch.hub_manager_service.domain.model.state.ChargerState;
import com.sch.hub_manager_service.domain.model.state.PlugMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HubMapper {

    public static HubStateDTO toHubStateDTO(List<Charger> chargers, Map<String, ChargerState> chargerStateMap) {
        HubStateDTO dto = new HubStateDTO();

        List<ChargerStateDTO> chargerStateDTOs = new ArrayList<>();
        for (Charger charger : chargers) {
            chargerStateDTOs.add(toChargerStateDTO(charger, chargerStateMap.get(charger.getId())));
        }

        dto.setChargerStates(chargerStateDTOs);
        return new HubStateDTO();
    }

    public static ChargerStateDTO toChargerStateDTO(Charger charger, ChargerState chargerState) {
        ChargerStateDTO dto = new ChargerStateDTO();

        dto.setChargerId(charger.getId());
        dto.setChargerOperationalState(chargerState.getOperationalState());

        dto.setEnergy(chargerState.getMetrics().getEnergy());
        dto.setOccupied(chargerState.getMetrics().isOccupied());

        /*
        List<PlugStateDTO> plugStateDTOs = new ArrayList<>();
        for (Plug plug : charger.getPlugs()) {
            plugStateDTOs.add(toPlugStateDTO(
                    plug,
                    chargerState.getPlugState(plug.getId())
            ));
        }
        dto.setPlugStates(plugStateDTOs);
         */

        return dto;
    }

    private static PlugStateDTO toPlugStateDTO(Plug plug, PlugMetrics plugState) {
        PlugStateDTO dto = new PlugStateDTO();

        dto.setPlugId(plug.getId());
        dto.setPlugType(plug.getPlugType());

        dto.setEnergy(plugState.getEnergy());
        dto.setOccupied(plugState.isOccupied());

        return dto;
    }
}
