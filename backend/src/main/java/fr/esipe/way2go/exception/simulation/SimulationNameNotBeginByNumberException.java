package fr.esipe.way2go.exception.simulation;

public class SimulationNameNotBeginByNumberException extends RuntimeException {
    public SimulationNameNotBeginByNumberException() {
        super("Le nom de la simulation doit commencer par une lettre");
    }
}
