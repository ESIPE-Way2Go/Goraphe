package fr.esipe.way2go.exception;

public class SimulationNotFoundException extends RuntimeException {
    public SimulationNotFoundException(Long id) {
        super(String.format("Simulation with Id %d not found", id));
    }
}

