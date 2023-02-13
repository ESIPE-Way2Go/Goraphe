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
        var path = pathGeneric  + "test.py";
        var pathLog = pathGeneric + user.getUsername() + sep + simulation.getName() + "_1.log";

        var builder = new ProcessBuilder("python3", pathGeneric+"test.py",
                "--dist", Integer.toString(dist),
                "--coords", coords.toString(),
                "--user", user.getUsername(),
                "--sim", simulation.getName(),
                "--desc", desc);
        System.out.println(pathGeneric);


        try {
            var process = builder.start();
            var error = new String(process.getErrorStream().readAllBytes());
            System.out.println("error: " + error);
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

    public static void main(String[] args) {
        var dist = 100;
        var coords ="48.8397252449997,2.5860595253148726";
        var pathGeneric = System.getProperty("user.dir") + System.getProperty("file.separator")+ "scripts" + System.getProperty("file.separator");
        var path = pathGeneric  + "test.py";
        var pathLog = pathGeneric + "admin" + System.getProperty("file.separator") + "test" + "_1.log";

        var builder = new ProcessBuilder("python3", pathGeneric+"test.py",
                "--dist", Integer.toString(dist),
                "--coords", coords,
                "--user", "admin",
                "--sim", "test",
                "--desc", "hahaha");
        try {
            var process = builder.start();
            var error = new String(process.getErrorStream().readAllBytes());
            System.out.println("error: " + error);
            var res = new String(process.getInputStream().readAllBytes());
            System.out.println(res);
            int exitCode = process.waitFor();
            System.out.println(exitCode);

        } catch (IOException e) {
            throw new RuntimeException();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}