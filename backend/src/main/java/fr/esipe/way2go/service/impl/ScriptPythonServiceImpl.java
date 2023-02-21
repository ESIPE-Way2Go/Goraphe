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
import java.nio.file.*;
import java.util.Calendar;

@Service
public class ScriptPythonServiceImpl implements ScriptPythonService {

    private static String SCRIPT1 = "Filtrage";
    private static String SCRIPT2 = "Points aléatoires";

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
     * @param user       : User generating the simulation.
     * @param simulation : Simulation generated.
     * @param coords     : Coordinates of the center point of the graph.
     * @param dist       : Generation distance for the graph.
     * @param desc       : Description of the simulation.
     */
    public void executeScript(UserEntity user, SimulationEntity simulation, MapController.Point coords, int dist, String desc) {
        var sep = System.getProperty("file.separator");
        var pathGeneric = System.getProperty("user.dir") + sep + "scripts" + sep;
        var pathLog1 = pathGeneric + user.getUsername() + sep + simulation.getName() + "_1.log";
        var pathLog2 = pathGeneric + user.getUsername() + sep + simulation.getName() + "_2.log";
        var pathLog3 = pathGeneric + user.getUsername() + sep + simulation.getName() + "_3.log";

        var logEntity1 = new LogEntity(simulation, SCRIPT1);
        logEntity1.setStatus("EN COURS");
        var logEntity2 = new LogEntity(simulation, SCRIPT2);
        var logEntity3 = new LogEntity(simulation, "computing");

        var builder = new ProcessBuilder("python3", pathGeneric + "test.py",
                "--dist", Integer.toString(dist),
                "--coords", coords.toString(),
                "--user", user.getUsername(),
                "--sim", simulation.getName(),
                "--desc", desc);
        try {
            var watchService = FileSystems.getDefault().newWatchService();
            var pathWatch = Paths.get(pathGeneric + sep + user.getUsername());
            pathWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            var process = builder.start();
            // Catch the output of the python
            var errorLogs = new String(process.getErrorStream().readAllBytes());

            var threadWatch = new Thread(() -> {
                WatchKey key;
                for (; ; ) {
                    try {
                        if ((key = watchService.take()) == null)
                            break;
                    } catch (InterruptedException e) {
                        break;
                    }

                    for (var event : key.pollEvents()) {
                        if (event.context().toString().equals(pathLog2)) {
                            logEntity1.setContent(readFile(Path.of(pathLog1).toString()));
                            logEntity1.setStatus("Finished");
                            logEntity2.setStatus("En cours");
                            logService.save(logEntity1);
                        }
                        if (event.context().toString().equals(pathLog3)) {
                            logEntity2.setContent(readFile(Path.of(pathLog2).toString()));
                            logEntity2.setStatus("Finished");
                            logService.save(logEntity2);
                            logEntity3.setStatus("EN cours");
                        }
                    }
                }
            });
            threadWatch.start();
            var exitCode = process.waitFor();
            threadWatch.interrupt();
            logEntity3.setStatus("FINISHED");
            logEntity3.setContent(readFile(Path.of(pathLog3).toString()));
            logService.save(logEntity3);
            var status = exitCode == 0 ? "SUCCESS" : "ERROR";
            simulation.setStatus(status);
            simulation.setEndDate(Calendar.getInstance());
            simulationService.save(simulation);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}