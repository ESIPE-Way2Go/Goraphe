package fr.esipe.way2go.utils;


public enum StatusSimulation {
    WAIT("Simulation pas encore lancée"),
    LOAD("Simulation en cours"),
    SUCCESS("SUCCESS"),
    ERROR("ERROR"),
    CANCEL("Simulation annulée");
    private String description;

    private StatusSimulation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

