package com.sch.hub_manager_service.simulation.dto.websocket.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "payload aggiornamento vehicle con stato e link via WebSocket")
public class VehicleStatus {

    // --- Getters e Setters ---
    private String vehicleId;             // riferimento al veicolo
    private double soc;                   // stato di carica in %
    private double kmDriven;              // chilometri percorsi
    private double currentEnergyJoules;   // energia corrente in Joule
    @JsonProperty("State")
    private String state;                  // stato del veicolo
    private String linkId;                // opzionale: link corrente, valido se MOVING o CHARGING
    private Double simTime;               // opzionale: tempo di simulazione relativo al link

    // Costruttore vuoto
    public VehicleStatus() {
    }

    // Costruttore completo senza link (per STOPPED o PARKED)
    public VehicleStatus(
            String vehicleId,
            double soc,
            double kmDriven,
            double currentEnergyJoules,
            String state
    ) {
        this.vehicleId = vehicleId;
        this.soc = soc;
        this.kmDriven = kmDriven;
        this.currentEnergyJoules = currentEnergyJoules;
        this.state = state;
        this.linkId = null;
        this.simTime = null;
    }

    // Costruttore completo con link (per MOVING o CHARGING)
    public VehicleStatus(
            String vehicleId,
            double soc,
            double kmDriven,
            double currentEnergyJoules,
            String state,
            String linkId,
            Double simTime
    ) {
        this.vehicleId = vehicleId;
        this.soc = soc;
        this.kmDriven = kmDriven;
        this.currentEnergyJoules = currentEnergyJoules;
        this.state = state;

        // Link valido solo se MOVING o CHARGING
        if (state.contains("moving") || state.contains("charging")) {
            this.linkId = linkId;
            this.simTime = simTime;
        } else {
            this.linkId = null;
            this.simTime = null;
        }
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setSoc(double soc) {
        this.soc = soc;
    }

    public void setKmDriven(double kmDriven) {
        this.kmDriven = kmDriven;
    }

    public void setCurrentEnergyJoules(double currentEnergyJoules) {
        this.currentEnergyJoules = currentEnergyJoules;
    }

    public void setState(String String) {
        this.state = String;
    }

    public void setLinkId(String linkId) {
        if (state.contains("moving") || state.contains("charging")) {
            this.linkId = linkId;
        } else {
            this.linkId = null;
        }
    }

    public void setSimTime(Double simTime) {
        if (state.contains("moving") || state.contains("charging")) {
            this.simTime = simTime;
        } else {
            this.simTime = null;
        }
    }

    // --- Backward compatibility ---
    public boolean isCharging() {
        return (this.state.contains("charging"));
    }

    @Override
    public String toString() {
        return "VehicleStatus{" +
                "vehicleId='" + vehicleId + '\'' +
                ", soc=" + soc +
                ", kmDriven=" + kmDriven +
                ", currentEnergyJoules=" + currentEnergyJoules +
                ", State=" + state +
                ", linkId='" + linkId + '\'' +
                ", simTime=" + simTime +
                '}';
    }
}
