package com.sch.hub_manager_service.hub.event;

import com.sch.hub_manager_service.domain.model.state.ChargerState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import tools.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
@JsonSerialize
public class ChargerStateChangedEvent {
    private List<ChargerState> newStates;
}
