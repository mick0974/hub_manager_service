package com.sch.hub_manager_service.hub.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sch.hub_manager_service.domain.model.state.ChargerOperationalState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChargerStateDTO {
    private String chargerId;
    private ChargerOperationalState chargerOperationalState;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private double energy;
    private boolean occupied;
}
