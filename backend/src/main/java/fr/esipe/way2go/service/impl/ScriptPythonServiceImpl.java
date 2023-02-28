package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.controller.MapController;
import fr.esipe.way2go.dao.LogEntity;
import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.dto.simulation.request.SimulationRequest;
import fr.esipe.way2go.service.LogService;
import fr.esipe.way2go.service.ScriptPythonService;
import fr.esipe.way2go.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Calendar;

@Service
public class ScriptPythonServiceImpl implements ScriptPythonService {
    private enum State{SUCCESS,ERROR}

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
        var genericPathLog = pathGeneric + user.getUsername() + sep + simulation.getName();
        var pathLog1 = genericPathLog + "_1.log";
        var pathLog2 = genericPathLog + "_2.log";
        var pathLog3 = genericPathLog + "_3.log";
        var logEntity1 = logService.save(new LogEntity(simulation, "filter"));
        var logEntity2 = logService.save(new LogEntity(simulation, "random_nodes"));
        var logEntity3 = logService.save(new LogEntity(simulation, "compute"));

        var builder = new ProcessBuilder("python3", pathGeneric + "filter.py",
                "--dist", Integer.toString(simulationRequest.getDistance()),
                "--coords", coords.toString(),
                "--user", user.getUsername(),
                "--sim", simulation.getName(),
                "--roads", String.join(",", simulationRequest.getRoadTypes()),
                "--point1", new MapController.Point(simulationRequest.getStart()).toString(),
                "--point2", new MapController.Point(simulationRequest.getEnd()).toString()
                ,"--random", Integer.toString(simulationRequest.getRandomPoints())
        );
        try {
            var process = builder.start();
            int exitCode = process.waitFor();
            var status = exitCode == 0 ? State.SUCCESS : State.ERROR;
            var errorLogs = new String(process.getErrorStream().readAllBytes());

            logEntity1.setContent(readFile(Path.of(pathLog1).toString()));
            logEntity2.setContent(readFile(Path.of(pathLog2).toString()));
            logEntity3.setContent(readFile(Path.of(pathLog3).toString()));

            if (logEntity2.getContent().equals("")) {
                logEntity1.setStatus(status.name());
                logEntity1.setContent(logEntity1.getContent() + errorLogs);
            }
            else if (logEntity3.getContent().equals("")) {
                logEntity1.setStatus(State.SUCCESS.name());
                logEntity2.setStatus(status.name());
                logEntity2.setContent(logEntity2.getContent() + errorLogs);
            } else {
                logEntity1.setStatus(State.SUCCESS.name());
                logEntity2.setStatus(State.SUCCESS.name());
                logEntity3.setStatus(status.name());
                logEntity3.setContent(logEntity3.getContent() + errorLogs);
            }

            logService.save(logEntity1);
            logService.save(logEntity2);
            logService.save(logEntity3);

            var stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            var randomPoints = stdInput.readLine();
            var shortestPath = stdInput.readLine();
            simulation.setEndDate(Calendar.getInstance());
            simulation.setRandomPoints(randomPoints);
            simulation.setShortestPath(shortestPath);
            simulation.setStatus(status.name());
            simulationService.save(simulation);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}