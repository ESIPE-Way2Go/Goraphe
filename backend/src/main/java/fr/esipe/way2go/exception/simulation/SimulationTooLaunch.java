package fr.esipe.way2go.exception.simulation;

public class SimulationTooLaunch extends RuntimeException {
    public SimulationTooLaunch() {
        super("Trop de simulation lancée en même temps. Vous devez attendre qu'une des simulations se termine ou vous pouvez en annuler.");
    }
}

