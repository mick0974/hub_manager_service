package com.sch.hub_manager_service.hub.dto.response;

import com.sch.hub_manager_service.domain.model.persistency.ChargerOperationalState;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class ChargerStateDTO {
    private String chargerId;
    private ChargerOperationalState chargerOperationalState;
    private double energy;
    private boolean occupied;
    //private List<PlugStateDTO> plugStates;
}
