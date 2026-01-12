package com.sch.hub_manager_service.simulation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class HubListDTO {
    List<HubDTO> hubs;
}
