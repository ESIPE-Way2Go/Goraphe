
package fr.esipe.way2go.service;

import fr.esipe.way2go.controller.MapController;
import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
public interface ScriptPythonService {

    void executeScript(UserEntity user, SimulationEntity simulation, MapController.Point coords, int dist, String desc);

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
            throw new RuntimeException(e);
        }
        return logs.toString();
    }
}