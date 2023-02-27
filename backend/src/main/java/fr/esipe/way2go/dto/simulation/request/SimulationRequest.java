package fr.esipe.way2go.dto.simulation.request;

import java.util.Arrays;
import java.util.List;

public class SimulationRequest {

    String name;
    double[] start;
    double[] end;
    int distance;
    String desc;
    String script;
    List<String> roadTypes;
    double[] center;
    int randomPoints;

    public int getRandomPoints() {
        return randomPoints;
    }

    public void setRandomPoints(int randomPoints) {
        this.randomPoints = randomPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getStart() {
        return start;
    }

    public void setStart(double[] start) {
        this.start = start;
    }

    public double[] getEnd() {
        return end;
    }

    public void setEnd(double[] end) {
        this.end = end;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
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

    public double[] getCenter() {
        return center;
    }

    public void setCenter(double[] center) {
        this.center = center;
    }


    @Override
    public String toString() {
        return "SimulationRequest{" +
                "name='" + name + '\'' +
                ", start=" + Arrays.toString(start) +
                ", end=" + Arrays.toString(end) +
                ", distance=" + distance +
                ", desc='" + desc + '\'' +
                ", script='" + script + '\'' +
                ", roadTypes=" + roadTypes +
                ", center=" + Arrays.toString(center) +
                ", randomPoints=" + randomPoints +
                '}';
    }

    public boolean checkBounds() {
        // Check if each coordinate is within the world bounds
        return start[0]>=-90 && start[0]<= 90 && start[1]>=-180 && start[1]<=180 &&
                end[0]>=-90 && end[0]<= 90 && end[1]>=-180 && end[1]<=180 &&
                center[0]>=-90 && center[0]<= 90 && center[1]>=-180 && center[1]<=180;
    }
}
