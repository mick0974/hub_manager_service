package com.sch.hub_manager_service.domain.model.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ChargerMetrics {
    private double energy;
    private boolean occupied;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ChargerMetrics that = (ChargerMetrics) o;
        return Double.compare(energy, that.energy) == 0 && occupied == that.occupied;
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(energy);
        result = 31 * result + Boolean.hashCode(occupied);
        return result;
    }
}
