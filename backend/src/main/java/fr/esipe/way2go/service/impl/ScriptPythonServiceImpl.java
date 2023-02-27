package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.controller.MapController;
import fr.esipe.way2go.dao.LogEntity;
import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.dto.simulation.request.SimulationRequest;
import fr.esipe.way2go.service.LogService;
import fr.esipe.way2go.service.ScriptPythonService;
import fr.esipe.way2go.service.SimulationService;
import fr.esipe.way2go.utils.StatusScript;
import fr.esipe.way2go.utils.StatusSimulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static fr.esipe.way2go.controller.SimulationController.THREAD_SIMULATIONS;

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
     * @param user              : User generating the simulation.
     * @param simulation        : Simulation generated.
     * @param coords            : Coordinates of the center point of the graph.
     * @param simulationRequest : contains parameters of the simulation such as the distance and the description, and the road types selected.
     */
    public void executeScript(UserEntity user, SimulationEntity simulation, MapController.Point coords, SimulationRequest simulationRequest) {
        var sep = System.getProperty("file.separator");
        var pathGeneric = System.getProperty("user.dir") + sep + "scripts" + sep;
        var formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        var simulationName = simulation.getName() + "_" + formatter.format(new Date());
        var genericPathLog = pathGeneric + user.getUsername() + sep + simulationName;
        var logs = new ArrayList<LogEntity>();
        var filter = "filter";
        var random = "random";
        var compute = "compute";
        var logEntity1 = logService.save(new LogEntity(simulation, filter));
        var logEntity2 = logService.save(new LogEntity(simulation, random));
        var logEntity3 = logService.save(new LogEntity(simulation, compute));

        logs.add(logEntity1);
        logs.add(logEntity2);
        logs.add(logEntity3);

        var builder = new ProcessBuilder("python3", pathGeneric + "filter.py",
                "--dist", Integer.toString(simulationRequest.getDistance()),
                "--coords", coords.toString(),
                "--user", user.getUsername(),
                "--sim", simulationName,
                "--roads", String.join(",", simulationRequest.getRoadTypes()),
                "--point1", new MapController.Point(simulationRequest.getStart()).toString(),
                "--point2", new MapController.Point(simulationRequest.getEnd()).toString()
        );

        String errorLogs = null;
        try {
            var process = builder.start();
            var logMap = new HashMap<String, LogEntity>();
            logMap.put(filter, logEntity1);
            logMap.put(random, logEntity2);
            logMap.put(compute, logEntity3);

            var thread = new Thread(() -> {
                readLogFile(Paths.get(genericPathLog), logMap);
            });
            thread.start();
            int exitCode = process.waitFor();
            thread.interrupt();
            var status = exitCode == 0 ? StatusSimulation.SUCCESS : StatusSimulation.ERROR;
            errorLogs = new String(process.getErrorStream().readAllBytes());
            updateStatus(simulation, status, logs, errorLogs);
            var stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            var randomPoints = stdInput.readLine();
            var shortestPath = stdInput.readLine();

            simulation.setRandomPoints(randomPoints);
            simulation.setShortestPath(shortestPath);
            simulation.setStatus(status.getDescription());
            endSimulation(simulation, status);
        } catch (IOException | InterruptedException e) {
            endSimulation(simulation, StatusSimulation.CANCEL);
            updateStatus(simulation, StatusSimulation.CANCEL, logs, errorLogs);
            Thread.currentThread().interrupt();
        }
    }

    private void readLogFile(Path path, Map<String, LogEntity> logs) {
        try {
            Files.createDirectory(path);
            var watchService = path.getFileSystem().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (var event : key.pollEvents()) {
                    var filename = event.context().toString();
                    if (filename.endsWith(".log")) {
                        if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                            LogEntity log;
                            switch (event.context().toString().replace(".log", "")) {
                                case "random" -> {
                                    log = logs.get("filter");
                                    log.setStatus(StatusScript.SUCCESS.getDescription());
                                    logService.save(log);
                                }
                                case "compute" -> {
                                    log = logs.get("random");
                                    log.setStatus(StatusScript.SUCCESS.getDescription());
                                    logService.save(log);
                                }
                            }
                        }

                        var filenameModify = (Path) event.context();
                        var content = readFile(path + "/" + filenameModify);
                        var logEntity = logs.get(filenameModify.toString().replace(".log", ""));
                        logEntity.setContent(content);
                        logEntity.setStatus(StatusScript.LOAD.getDescription());
                        logService.save(logEntity);
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void endSimulation(SimulationEntity simulation, StatusSimulation status) {
        THREAD_SIMULATIONS.remove(simulation.getSimulationId());
        simulation.setEndDate(Calendar.getInstance());
        simulation.setStatus(status.getDescription());
        simulationService.save(simulation);
    }

    private void updateStatus(SimulationEntity simulation, StatusSimulation status, List<LogEntity> logs, String errorLogs) {
        logs.forEach(log -> {
            if (log.getStatus().equals(StatusScript.LOAD.getDescription())) {
                switch (status) {
                    case SUCCESS:
                        log.setStatus(StatusScript.SUCCESS.getDescription());
                        break;
                    case ERROR:
                        log.setStatus(StatusScript.ERROR.getDescription());
                        log.setContent(log.getContent() + errorLogs);
                        break;
                    case CANCEL:
                        log.setStatus(StatusScript.ERROR.getDescription());
                        log.setContent(log.getContent() + " " + status.getDescription());
                }
            }
            logService.save(log);
        });
        endSimulation(simulation, status);
    }
}