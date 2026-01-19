package com.sch.hub_manager_service.domain.model.state;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChargerState {

    private String chargerId;
    private PlugType plugType;
    private ChargerOperationalState chargerOperationalState = ChargerOperationalState.ON;
    private ChargerMetrics metrics;

    public ChargerState(String chargerId, PlugType plugType) {
        this.chargerId = chargerId;
        this.plugType = plugType;
    }

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
        if (this.chargerOperationalState == newState) {
            return false;
        }

        this.chargerOperationalState = newState;
        return true;
    }

    private boolean canAcceptSimulationUpdate() {
        return chargerOperationalState.equals(ChargerOperationalState.ON) || chargerOperationalState.equals(ChargerOperationalState.ACTIVE);
    }

    public void clearMetrics() {
        // this.metrics = new ChargerMetrics(0, false);
        this.metrics = new ChargerMetrics(this.metrics.getEnergy(), false);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ChargerState that = (ChargerState) o;
        return Objects.equals(chargerId, that.chargerId) &&
                chargerOperationalState == that.chargerOperationalState &&
                Objects.equals(metrics, that.metrics);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(chargerId);
        result = 31 * result + Objects.hashCode(chargerOperationalState);
        result = 31 * result + Objects.hashCode(metrics);
        return result;
    }
}
