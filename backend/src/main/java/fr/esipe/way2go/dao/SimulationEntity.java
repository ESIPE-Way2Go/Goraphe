package fr.esipe.way2go.dao;

import fr.esipe.way2go.dao.converter.CalendarConverter;
import fr.esipe.way2go.dao.converter.StatusSimulationConverter;
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


    @Column(name = "status", nullable = false)
    @Convert(converter = StatusSimulationConverter.class)
    private StatusSimulation status;

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
        this.status = StatusSimulation.WAIT;
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

    public List<ResultEntity> getResultEntities() {
        return resultEntities;
    }

    public void setResultEntities(List<ResultEntity> resultEntities) {
        this.resultEntities = resultEntities;
    }

    public StatusSimulation getStatus() {
        return status;
    }

    public void setStatus(StatusSimulation status) {
        this.status = status;
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
                ", status='" + status + '\'' +
                ", logs=" + logs +
                '}';
    }
}
