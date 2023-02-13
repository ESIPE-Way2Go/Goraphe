package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.dao.LogEntity;
import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.service.LogService;
import fr.esipe.way2go.service.ScriptPythonService;
import fr.esipe.way2go.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

@Service
public class ScriptPythonServiceImpl implements ScriptPythonService {

    private final LogService logService;
    private final SimulationService simulationService;


    @Autowired
    public ScriptPythonServiceImpl(LogService logService, SimulationService simulationService) {
        this.logService = logService;
        this.simulationService = simulationService;
    }
    /**
     *
     * @param user : identifiant de la simulation
     * @param simulation : id of the simulation
     * @param nameScript : name of the script
     */
    public void executeScript(UserEntity user, SimulationEntity simulation, String nameScript) {
        var pathGeneric = System.getProperty("user.dir") + System.getProperty("file.separator")+ "scripts" + System.getProperty("file.separator");
        var path = pathGeneric  + "test.py";
        var pathLog = pathGeneric + "/" + user.getUsername() + "/" + simulation.getName() + "_1.log";

        var builder = new ProcessBuilder("python3", Path.of(path).toString(), user.getUsername(), simulation.getName(), "10");

        try {
            var process = builder.start();
            var res = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();
            var content = readFile(Path.of(pathLog).toString());
            var listLog = new ArrayList<LogEntity>();
            listLog.add(new LogEntity(simulation, content));
            simulation.setLogs(listLog);
            logService.createLog(new LogEntity(simulation, content));
        } catch (IOException e) {
            throw new RuntimeException();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}