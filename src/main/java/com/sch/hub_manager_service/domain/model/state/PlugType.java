package com.sch.hub_manager_service.domain.model.state;

public enum PlugType {
    STANDARD("standard"),
    FAST_CHARGE("fast charge");

    private final String value;

    PlugType(String value) {
        this.value = value;
    }

    public static PlugType fromValue(String value) {
        if (value == null) return null;

        return switch (value.toLowerCase()) {
            case "default" -> STANDARD;
            case "fast charge" -> FAST_CHARGE;
            default -> STANDARD;
        };
    }

    @Override
    public String toString() {
        return value;
    }
}
