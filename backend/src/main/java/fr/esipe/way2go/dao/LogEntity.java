package fr.esipe.way2go.dao;

import fr.esipe.way2go.utils.StatusScript;

import javax.persistence.*;

@Entity
@Table(name = "log", schema = "public", catalog = "goraphe")
public class LogEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne
    @JoinColumn(name="simulation", nullable = false)
    private SimulationEntity simulation;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "script", nullable = false)
    private String script;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;


    public LogEntity() {
    }

    public LogEntity(SimulationEntity simulation, String script) {
        this.simulation = simulation;
        this.status = StatusScript.NOT_LAUNCHED.getDescription();
        this.script = script;
    }

    public LogEntity(SimulationEntity simulation, String content, String status, String script) {
        this.simulation = simulation;
        this.content = content;
        this.status = status;
        this.script = script;
    }

    /**
     * Returns this log's ID.
     *
     * @return logId This log's ID. (Long)
     */
    public Long getLogId() {
        return logId;
    }

    /**
     * Sets a new ID for this log.
     *
     * @param logId This log's new ID. (Long)
     */
    public void setLogId(Long logId) {
        this.logId = logId;
    }

    /**
     * Returns this log's associated simulation.
     *
     * @return simulation This log's associated simulation. (SimulationEntity)
     */
    public SimulationEntity getSimulation() {
        return simulation;
    }

    /**
     * Sets a new associated simulation for this log.
     *
     * @param simulation This log's new associated simulation. (SimulationEntity)
     */
    public void setSimulation(SimulationEntity simulation) {
        this.simulation = simulation;
    }

    /**
     * Returns this log's status.
     *
     * @return status This log's status. (String)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets a new status for this log.
     *
     * @param status This log's new status. (String)
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns this log's content.
     *
     * @return content This log's content. (String)
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets a new content for this log.
     *
     * @param content This log's new content. (String)
     */
    public void setContent(String content) {
        this.content = content;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
