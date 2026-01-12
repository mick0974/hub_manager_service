package com.sch.hub_manager_service.domain.model.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@AllArgsConstructor
@ToString
public class PlugMetrics {
    private final String plugId;
    private final boolean occupied;
    private final double energy;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        PlugMetrics that = (PlugMetrics) o;
        return Objects.equals(plugId, that.plugId) &&
                occupied == that.occupied &&
                Double.compare(energy, that.energy) == 0;
    }

    @Override
    public int hashCode() {
        int result = plugId.hashCode();
        result = 31 * result + Boolean.hashCode(occupied);
        result = 31 * result + Double.hashCode(energy);
        return result;
    }
}
