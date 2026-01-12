package com.sch.hub_manager_service.simulation.mapper;

import com.sch.hub_manager_service.domain.model.persistency.Charger;
import com.sch.hub_manager_service.domain.model.persistency.ChargerOperationalState;
import com.sch.hub_manager_service.simulation.dto.ChargerDTO;

public class SimulationMapper {

    public static Charger mapToCharger(ChargerDTO dto) {
        Charger charger = new Charger();
        charger.setId(dto.getChargerId());
        charger.setChargerOperationalState(ChargerOperationalState.ON);
        charger.setPlugs(dto.getAvailablePlugs());

        /*
        List<Plug> plugs = dto.getAvailablePlugs().stream()
                .map(Plug::new)
                .toList();
        charger.setPlugs(plugs);
         */

        return charger;
    }

}
