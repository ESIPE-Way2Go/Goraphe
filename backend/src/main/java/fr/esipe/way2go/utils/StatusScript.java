package fr.esipe.way2go.utils;

public enum StatusScript {
    NOT_LAUNCHED("Pas lancé"),
    LOAD("En cours"),
    SUCCESS("SUCCESS"),
    ERROR("ERROR");

    private String description;

    StatusScript(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
