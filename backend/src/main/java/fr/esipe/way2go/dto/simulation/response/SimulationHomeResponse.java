package fr.esipe.way2go.dto.simulation.response;

import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.utils.StatusSimulation;

import java.util.Calendar;

public class SimulationHomeResponse {
    private Long id;
    private String title;
    private Calendar beginDate;
    private Calendar endDate;
    private StatusSimulation status;

    public SimulationHomeResponse(SimulationEntity simulationEntity) {
        id = simulationEntity.getSimulationId();
        title = simulationEntity.getName();
        beginDate = simulationEntity.getBeginDate();
        endDate = simulationEntity.getEndDate();
        status = simulationEntity.getStatus();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Calendar getBeginDate() {
        return beginDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBeginDate(Calendar beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public StatusSimulation getStatus() {
        return status;
    }

    public void setStatus(StatusSimulation status) {
        this.status = status;
    }
}
