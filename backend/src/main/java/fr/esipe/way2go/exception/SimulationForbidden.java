package fr.esipe.way2go.exception;

public class SimulationForbidden extends RuntimeException {
    public SimulationForbidden() {
        super("you don't have the qualification for this Simulation");
    }
}

