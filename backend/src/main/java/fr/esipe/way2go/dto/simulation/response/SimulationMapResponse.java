package fr.esipe.way2go.dto.simulation.response;

import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.utils.StatusSimulation;

import java.util.List;

public class SimulationMapResponse {
    private String title;
    private String description;
    private StatusSimulation status;
    private String script;

    private List<String> roads;
    private String path;
    private String randomPoints;

    public SimulationMapResponse(SimulationEntity simulation) {
        this.title = simulation.getName();
        this.description = simulation.getDescription();
        this.status = simulation.getStatus();
        this.roads = simulation.getRoadType();
        this.script = simulation.getComputingScript();
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public StatusSimulation getStatus() {
        return status;
    }

    public List<String> getRoads() {
        return roads;
    }

    public String getPath() {
        return path;
    }

    public String getRandomPoints() {
        return randomPoints;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(StatusSimulation status) {
        this.status = status;
    }

    public void setRoads(List<String> roads) {
        this.roads = roads;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRandomPoints(String randomPoints) {
        this.randomPoints = randomPoints;
    }
}
