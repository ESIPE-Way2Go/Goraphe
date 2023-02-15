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
import java.util.Calendar;

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
            var logEntity = logService.createLog(new LogEntity(simulation, nameScript));
            var res = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();
            var content = readFile(Path.of(pathLog).toString());
            var status = exitCode == 0 ? "SUCCESS" : "ERROR";
            logEntity.setContent(content);
            logEntity.setStatus(status);
            logService.createLog(logEntity);
            simulation.setEndDate(Calendar.getInstance());
            simulationService.save(simulation);
        } catch (IOException e) {
            throw new RuntimeException();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}