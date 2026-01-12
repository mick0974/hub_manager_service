package com.sch.hub_manager_service.domain.model.persistency;

import lombok.Getter;

@Getter
public enum PlugType {
    FAST_CHARGER("fast charger"),
    DEFAULT("default");

    private final String type;

    PlugType(String type) {
        this.type = type;
    }

    public static PlugType fromType(String type) {
        for (PlugType p : values()) {
            if (p.type.equalsIgnoreCase(type)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown plug type: " + type);
    }
}

