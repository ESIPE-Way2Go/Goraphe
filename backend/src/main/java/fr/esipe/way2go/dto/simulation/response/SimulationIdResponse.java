package fr.esipe.way2go.dto.simulation.response;

public class SimulationIdResponse {
    private Long simulationId;

    public SimulationIdResponse(Long simulationId) {
        this.simulationId = simulationId;
    }

    public Long getSimulationId() {
        return simulationId;
    }
}
