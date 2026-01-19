package com.sch.hub_manager_service.hub.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private int occupancy;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private double energy;
    private List<ChargerStateDTO> chargerStates;
}
