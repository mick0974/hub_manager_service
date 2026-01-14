package com.sch.hub_manager_service.simulation.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChargerDTO {
    private String chargerId;
    private Set<String> availablePlugs;
}
