package fr.esipe.way2go.dto.simulation.response;

import fr.esipe.way2go.dao.SimulationEntity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SimulationResponse {
    /**
     * Name of the simulation
     */
    private String name;

    /**
     * Description of the simulation
     */
    private String description;

    /**
     * Status of the simulation
     */
    private String status;

    /**
     * Name of the computing_script algorithm
     */
    private String computingScript;

    /**
     * Types of roads
     */
    private List<String> roads;

    /**
     * Distance use in algo
     */
    private double distance;

    /**
     * Begin date
     */
    private Calendar beginDate;

    /**
     * End date
     */
    private Calendar endDate;

    /**
     * List of logs response
     */
    private List<LogResponse> logResponses;

    public SimulationResponse(SimulationEntity simulation) {
        this.name = simulation.getName();
        this.description = simulation.getDescription();
        this.status = simulation.getStatistics();
        this.computingScript = simulation.getComputingScript();
        this.roads = simulation.getRoadType();
        this.distance = simulation.getGenerationDistance();
        this.beginDate = simulation.getBeginDate();
        this.endDate = simulation.getEndDate();
        this.logResponses = new ArrayList<>();

        for (var log : simulation.getLogs()) {
            var content = log.getContent() == null ? new String[]{} : log.getContent().split("\n");
            logResponses.add(new LogResponse(log.getStatus(), log.getScript(), content));
        }
    }
    public String getDescription() {
        return description;
    }
    public String getStatus() {
        return status;
    }

    public String getComputingScript() {
        return computingScript;
    }

    public List<String> getRoads() {
        return roads;
    }

    public double getDistance() {
        return distance;
    }

    public Calendar getBeginDate() {
        return beginDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public List<LogResponse> getLogResponses() {
        return logResponses;
    }

    public void setSimulationName(String simulationName) {
        this.name = simulationName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setComputingScript(String computingScript) {
        this.computingScript = computingScript;
    }

    public void setRoads(List<String> roads) {
        this.roads = roads;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setLogResponses(List<LogResponse> logResponses) {
        this.logResponses = logResponses;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBeginDate(Calendar beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }
}
