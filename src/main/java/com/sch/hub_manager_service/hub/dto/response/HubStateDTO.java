package com.sch.hub_manager_service.hub.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class HubStateDTO {
    private List<ChargerStateDTO> chargerStates;
}
