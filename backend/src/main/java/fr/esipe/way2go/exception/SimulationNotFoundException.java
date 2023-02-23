package fr.esipe.way2go.exception;

public class SimulationNotFoundException extends RuntimeException {
    public SimulationNotFoundException() {
        super("Simulation not found");
    }
}

