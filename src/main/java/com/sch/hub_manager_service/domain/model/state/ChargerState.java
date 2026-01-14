package com.sch.hub_manager_service.domain.model.state;

import com.sch.hub_manager_service.domain.model.persistency.ChargerOperationalState;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChargerState {

    private String chargerId;
    private ChargerOperationalState operationalState;
    private ChargerMetrics metrics;

    public boolean updateFromSimulation(ChargerMetrics incomingState) {
        if (!canAcceptSimulationUpdate()) {
            return false;
        }

        if (Objects.equals(this.metrics, incomingState)) {
            return false;
        }

        this.metrics = incomingState;
        return true;
    }

    public boolean updateOperationalState(ChargerOperationalState newState) {
        if (this.operationalState == newState) {
            return false;
        }

        this.operationalState = newState;
        return true;
    }

    private boolean canAcceptSimulationUpdate() {
        return operationalState.equals(ChargerOperationalState.ON) || operationalState.equals(ChargerOperationalState.ACTIVE);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ChargerState that = (ChargerState) o;
        return Objects.equals(chargerId, that.chargerId) &&
                operationalState == that.operationalState &&
                Objects.equals(metrics, that.metrics);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(chargerId);
        result = 31 * result + Objects.hashCode(operationalState);
        result = 31 * result + Objects.hashCode(metrics);
        return result;
    }
}
