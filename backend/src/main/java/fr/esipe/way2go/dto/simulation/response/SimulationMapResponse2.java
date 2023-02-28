package fr.esipe.way2go.dto.simulation.response;

import fr.esipe.way2go.dao.SimulationEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationMapResponse2 {
    private String title;
    private String description;
    private String status;
    private String script;
    private List<String> roads;
    private Map<String, String> results;

    public SimulationMapResponse2(SimulationEntity simulation) {
        this.title = simulation.getName();
        this.description = simulation.getDescription();
        this.status = simulation.getStatus();
        results = new HashMap<>();
        simulation.getResultEntities().forEach(resultEntity -> results.put(resultEntity.getKey(), resultEntity.getContent()));
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

    public String getStatus() {
        return status;
    }

    public List<String> getRoads() {
        return roads;
    }

    public Map<String, String> getResults() {
        return results;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoads(List<String> roads) {
        this.roads = roads;
    }

    public void setResults(Map<String, String> results) {
        this.results = results;
    }
}
