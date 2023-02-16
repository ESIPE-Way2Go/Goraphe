package fr.esipe.way2go.dto.simulation.request;

import java.util.List;

public class SimulationRequest {

    String name;
    double startX;
    double startY;
    double endX;
    double endY;
    int distance;
    String desc;
    String script;
    List<String> roadTypes;


    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public List<String> getRoadTypes() {
        return roadTypes;
    }

    public void setRoadTypes(List<String> roadTypes) {
        this.roadTypes = roadTypes;
    }

    @Override
    public String toString() {
        return "SimulationRequest{" +
                "name='" + name + '\'' +
                ", startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                ", distance=" + distance +
                ", desc='" + desc + '\'' +
                ", script='" + script + '\'' +
                ", roadTypes=" + roadTypes +
                '}';
    }
}
