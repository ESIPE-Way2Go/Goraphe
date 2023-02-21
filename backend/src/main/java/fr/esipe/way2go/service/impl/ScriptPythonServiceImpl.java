package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.controller.MapController;
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
     * Executes a script with specified parameters.
     *
     * @param user : User generating the simulation.
     * @param simulation : Simulation generated.
     * @param coords : Coordinates of the center point of the graph.
     * @param dist : Generation distance for the graph.
     * @param desc : Description of the simulation.
     */
    public void executeScript(UserEntity user, SimulationEntity simulation, MapController.Point coords, int dist, String desc) {
        var sep=System.getProperty("file.separator");
        var pathGeneric = System.getProperty("user.dir") + sep+ "scripts" + sep;
        var pathLog = pathGeneric + user.getUsername() + sep + simulation.getName() + "_1.log";

        var builder = new ProcessBuilder("python3", pathGeneric+"test.py",
                "--dist", Integer.toString(dist),
                "--coords", coords.toString(),
                "--user", user.getUsername(),
                "--sim", simulation.getName(),
                "--desc", desc);


        try {
            var process = builder.start();
            var logEntity = logService.save(new LogEntity(simulation, "test.py"));
            int exitCode = process.waitFor();
            var content = readFile(Path.of(pathLog).toString());
            var status = exitCode == 0 ? "SUCCESS" : "ERROR";
            logEntity.setContent(content);
            logEntity.setStatus(status);
            logService.save(logEntity);
            simulation.setEndDate(Calendar.getInstance());
            simulationService.save(simulation);
        } catch (IOException e) {
            throw new RuntimeException();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}