package fr.esipe.way2go.exception.simulation;

public class SimulationNameFormatWrong extends RuntimeException {
    public SimulationNameFormatWrong() {
        super("Le nom de la simulation ne doit pas contenir un espace");
    }
}
