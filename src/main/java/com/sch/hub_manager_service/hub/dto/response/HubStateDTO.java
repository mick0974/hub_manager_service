package com.sch.hub_manager_service.hub.dto.response;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@ToString
@NoArgsConstructor
public class HubStateDTO {
    private List<ChargerStateDTO> chargerStates;
}
