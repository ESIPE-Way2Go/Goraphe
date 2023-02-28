package fr.esipe.way2go.dao;

import javax.persistence.*;

@Entity
@Table(name = "result", schema = "public", catalog = "goraphe")
public class ResultEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "result_id")
    private Long id;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name="simulation", nullable = false)
    private SimulationEntity simulation;

    public ResultEntity() {
    }

    public ResultEntity(String key, String content, SimulationEntity simulation) {
        this.key = key;
        this.content = content;
        this.simulation = simulation;
    }

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getContent() {
        return content;
    }

    public SimulationEntity getSimulation() {
        return simulation;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSimulation(SimulationEntity simulation) {
        this.simulation = simulation;
    }
}
