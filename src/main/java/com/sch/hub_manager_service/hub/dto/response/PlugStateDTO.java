package com.sch.hub_manager_service.hub.dto.response;

import com.sch.hub_manager_service.domain.model.persistency.PlugType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PlugStateDTO {
    private long plugId;
    private PlugType plugType;
    private boolean occupied;
    private double energy;
}
