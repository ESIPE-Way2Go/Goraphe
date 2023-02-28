package fr.esipe.way2go.dao;

import fr.esipe.way2go.dao.converter.CalendarConverter;
import fr.esipe.way2go.dao.converter.StringListConverter;
import fr.esipe.way2go.utils.StatusSimulation;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "simulation", schema = "public", catalog = "goraphe")
public class SimulationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "simulation_id")
    private Long simulationId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "computing_script", nullable = false)
    private String computingScript;

    @Column(name = "generation_distance", nullable = false)
    private int generationDistance;


    @Column(name = "road_type", nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> roadType;

    @Column(name = "begin_date")
    @Convert(converter = CalendarConverter.class)
    private Calendar beginDate;

    @Column(name = "end_date")
    @Convert(converter = CalendarConverter.class)
    private Calendar endDate;

    @Column(name = "share_link", nullable = false, unique = true)
    private UUID shareLink;

    @Column(name = "statistics", nullable = false)
    private String statistics;
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "shortestPath", length = 10000000)
    private String shortestPath;

    @Column(name = "random_points", length = 10000000)
    private String randomPoints;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulation")
    private List<LogEntity> logs;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "simulation")
    private List<ResultEntity> resultEntities;

    public SimulationEntity() {
    }

    public SimulationEntity(String name, UserEntity user, String description, int distance, String computingScript, List<String> roadTypes) {
        this.name = name;
        this.user = user;
        this.description = description;
        this.computingScript = computingScript;
        this.generationDistance = distance;
        this.roadType = roadTypes;
        this.shareLink = UUID.randomUUID();
        this.statistics = "statistics";
        this.status = StatusSimulation.WAIT.getDescription();
    }

    public UUID getShareLink() {
        return shareLink;
    }

    public void setShareLink(UUID shareLink) {
        this.shareLink = shareLink;
    }

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
     * Returns this simulation's owner user.
     *
     * @return user This simulation's owner user. (UserEntity)
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * Sets a new user owner for this simulation.
     *
     * @param user This simulation's new owner user. (UserEntity)
     */
    public void setUser(UserEntity user) {
        this.user = user;
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
    public int getGenerationDistance() {
        return generationDistance;
    }

    public Calendar getBeginDate() {
        return beginDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    /**
     * Sets a new generation distance for this simulation.
     *
     * @param generationDistance This simulation's new generation distance. (Double)
     */
    public void setGenerationDistance(int generationDistance) {
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
    public List<String> getRoadType() {
        return roadType;
    }

    /**
     * Sets a new road type for this simulation.
     *
     * @param roadType This simulation's new road type. (String)
     */
    public void setRoadType(List<String> roadType) {
        this.roadType = roadType;
    }

    public String getShortestPath() {
        return shortestPath;
    }

    public String getStatus() {
        return status;
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

    /**
     * Returns this simulation's logs.
     *
     * @return logs This simulation's logs. (List<LogEntity>)
     */
    public List<LogEntity> getLogs() {
        return logs;
    }

    /**
     * Sets new logs for this simulation.
     *
     * @param logs This simulation's new statistics. (List<LogEntity>)
     */
    public void setLogs(List<LogEntity> logs) {
        this.logs = logs;
    }

    public void setBeginDate(Calendar beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setShortestPath(String shortestPath) {
        this.shortestPath = shortestPath;
    }

    public List<ResultEntity> getResultEntities() {
        return resultEntities;
    }

    public void setResultEntities(List<ResultEntity> resultEntities) {
        this.resultEntities = resultEntities;
    }

    @Override
    public String toString() {
        return "SimulationEntity{" +
                "simulationId=" + simulationId +
                ", name='" + name + '\'' +
                ", user=" + user +
                ", description='" + description + '\'' +
                ", computingScript='" + computingScript + '\'' +
                ", generationDistance=" + generationDistance +
                ", roadType=" + roadType +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", shareLink=" + shareLink +
                ", statistics='" + statistics + '\'' +
                ", status='" + status + '\'' +
                ", shortestPath='" + shortestPath + '\'' +
                ", randomPoints='" + randomPoints + '\'' +
                ", logs=" + logs +
                '}';
    }
}
