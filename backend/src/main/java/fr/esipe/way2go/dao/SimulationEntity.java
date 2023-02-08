package fr.esipe.way2go.dao;

import javax.persistence.*;

@Entity
@Table(name = "simulation", schema = "public", catalog = "compte")
@Inheritance(strategy = InheritanceType.JOINED)
public class SimulationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName="user_id")
    private UserEntity userId;

    @Column(name = "graph", nullable = false)
    private String graph;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "computing_script", nullable = false)
    private String computingScript;

    @Column(name = "generation_distance", nullable = false)
    private Double generationDistance;

    @Column(name = "random_points", nullable = false)
    private String randomPoints;

    @Column(name = "road_type", nullable = false)
    private String roadType;

    @Column(name = "log_path", nullable = false)
    private String logPath;

    @Column(name = "share_link", nullable = false, unique = true)
    private String shareLink;

    @Column(name = "statistics", nullable = false)
    private String statistics;

    /**
     * Returns this simulation's ID.
     *
     * @return simulationId This simulation's ID. (Long)
     */
    public Long getSimulationId() {
        return simulationId;
    }

    /**
     * Sets a new ID for this simulation.
     *
     * @param simulationId This simulation's new ID. (Long)
     */
    public void setSimulationId(Long simulationId) {
        this.simulationId = simulationId;
    }

    /**
     * Returns this simulation's name.
     *
     * @return name This simulation's name. (String)
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for this simulation.
     *
     * @param name This simulation's new name. (String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns this simulation's owner user's ID.
     *
     * @return userId This simulation's owner user's ID. (Long)
     */
    public UserEntity getUserId() {
        return userId;
    }

    /**
     * Sets a new ID for this simulation.
     *
     * @param userId The new ID for this simulation's owner user. (Long)
     */
    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    /**
     * Returns this simulation's graph.
     *
     * @return graph This simulation's graph. (String)
     */
    public String getGraph() {
        return graph;
    }

    /**
     * Sets a new graph for this simulation.
     *
     * @param graph This simulation's new graph. (String)
     */
    public void setGraph(String graph) {
        this.graph = graph;
    }

    /**
     * Returns this simulation's description.
     *
     * @return description This simulation's description. (String)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a new description for this simulation.
     *
     * @param description This simulation's new description. (String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns this simulation's computing script.
     *
     * @return computingScript This simulation's computing script. (String)
     */
    public String getComputingScript() {
        return computingScript;
    }

    /**
     * Sets a new computing script for this simulation.
     *
     * @param computingScript This simulation's new computing script. (String)
     */
    public void setComputingScript(String computingScript) {
        this.computingScript = computingScript;
    }

    /**
     * Returns this simulation's generation distance.
     *
     * @return generationDistance This simulation's generation distance. (Double)
     */
    public Double getGenerationDistance() {
        return generationDistance;
    }

    /**
     * Sets a new generation distance for this simulation.
     *
     * @param generationDistance This simulation's new generation distance. (Double)
     */
    public void setGenerationDistance(Double generationDistance) {
        this.generationDistance = generationDistance;
    }

    /**
     * Returns this simulation's random points.
     *
     * @return randomPoints This simulation's random points. (String)
     */
    public String getRandomPoints() {
        return randomPoints;
    }

    /**
     * Sets new random points for this simulation.
     *
     * @param randomPoints This simulation's new random points. (String)
     */
    public void setRandomPoints(String randomPoints) {
        this.randomPoints = randomPoints;
    }

    /**
     * Returns this simulation's road type.
     *
     * @return roadType This simulation's road type. (String)
     */
    public String getRoadType() {
        return roadType;
    }

    /**
     * Sets a new road type for this simulation.
     *
     * @param roadType This simulation's new road type. (String)
     */
    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    /**
     * Returns this simulation's log path.
     *
     * @return logPath This simulation's log path. (String)
     */
    public String getLogPath() {
        return logPath;
    }

    /**
     * Sets a new log path for this simulation.
     *
     * @param logPath This simulation's new log path. (String)
     */
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    /**
     * Returns this simulation's share link.
     *
     * @return shareLink This simulation's share link. (String)
     */
    public String getShareLink() {
        return shareLink;
    }

    /**
     * Sets a new share link for this simulation.
     *
     * @param shareLink This simulation's new shareLink. (String)
     */
    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    /**
     * Returns this simulation's statistics.
     *
     * @return statistics This simulation's statistics. (String)
     */
    public String getStatistics() {
        return statistics;
    }

    /**
     * Sets new statistics for this simulation.
     *
     * @param statistics This simulation's new statistics. (String)
     */
    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }
}
