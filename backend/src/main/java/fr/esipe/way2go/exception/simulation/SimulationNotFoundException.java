package fr.esipe.way2go.exception.simulation;

public class SimulationNotFoundException extends RuntimeException {
    public SimulationNotFoundException() {
        super("Simulation not found");
    }
}

