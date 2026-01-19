package com.sch.hub_manager_service.hub.event.data;

import com.sch.hub_manager_service.domain.model.state.ChargerMetrics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ChargerMetricsChange {
    private String chargerId;
    private ChargerMetrics chargerMetrics;
}
