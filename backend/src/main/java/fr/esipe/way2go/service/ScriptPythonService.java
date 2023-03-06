
package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.dto.simulation.request.SimulationRequest;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
public interface ScriptPythonService {
    public record Point(double x, double y){
        public Point(double[] point){
            this(point[0], point[1]);
        }
        @Override
        public String toString() {
            return x+","+y;
        }
        public static Point midPoint(Point a, Point b){
            return new Point((a.x+b.x)/2,(a.y+ b.y)/2);
        }
    }

    void executeScript(UserEntity user, SimulationEntity simulation, Point coords, SimulationRequest simulationRequest);

    /**
     * Get the content of the files
     * @param path of the file
     * @return the content on file in the string
     */
    default String readFile(String path) {
        var logs = new StringBuilder();
        try (var br = new BufferedReader(new FileReader(path))){
            var line = "";
            while ((line = br.readLine()) != null) {
                logs.append(line).append("\n");
            }
        } catch (IOException e) {
           return "";
        }
        return logs.toString();
    }
}