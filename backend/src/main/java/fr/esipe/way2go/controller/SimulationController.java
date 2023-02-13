package fr.esipe.way2go.controller;

import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.dto.simulation.request.SimulationRequest;
import fr.esipe.way2go.dto.simulation.response.LogResponse;
import fr.esipe.way2go.dto.simulation.response.SimulationResponse;
import fr.esipe.way2go.service.LogService;
import fr.esipe.way2go.service.ScriptPythonService;
import fr.esipe.way2go.service.SimulationService;
import fr.esipe.way2go.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationService simulationService;
    private final UserService userService;
    private final ScriptPythonService scriptPythonService;

    @Autowired
    public SimulationController(SimulationService simulationService, UserService userService, ScriptPythonService scriptPythonService) {
        this.simulationService = simulationService;
        this.userService = userService;
        this.scriptPythonService = scriptPythonService;
    }

    @PermitAll
    @PostMapping
    public ResponseEntity<Long> createSimulation(@RequestBody SimulationRequest simulationRequest) {
        var user = userService.getUser("admin");
        var simulation = new SimulationEntity(simulationRequest.getName(), user.orElseThrow(), simulationRequest.getDesc());
        var simulationSave = simulationService.createSimulation(simulation);
        var midPoint= MapController.Point.midPoint(new MapController.Point(simulationRequest.getStartX(), simulationRequest.getStartY()),
                new MapController.Point(simulationRequest.getEndX(),simulationRequest.getEndY()));
        scriptPythonService.executeScript(user.orElseThrow(), simulationSave, midPoint,simulationRequest.getDistance(),simulationRequest.getDesc());
        return new ResponseEntity<>(simulationSave.getSimulationId(), HttpStatus.ACCEPTED);
    }

    @PermitAll
    @GetMapping(value = "/{id}")
    public ResponseEntity<LogResponse> getLogs(@PathVariable Long id) {
        var simulation = simulationService.find(id);
        if (simulation.isEmpty())
            return new ResponseEntity<>(new LogResponse(), HttpStatus.NOT_FOUND);

        var logs = new StringBuilder();
        simulation.get().getLogs().forEach(x -> logs.append(x.getContent()).append("\n"));
        var log = simulation.get().getLogs().get(0);
        var t = log.getContent().split("\n");
        var test = new LogResponse();
        test.setContent(t);
        return new ResponseEntity<>(test, HttpStatus.OK);
    }


    @GetMapping("/test/{simulationId}")
    //Regarde une simulation
    public String lookSimulation(@PathVariable long simulationId) {
        return "Regarde la simulation " + simulationId;
    }

    @DeleteMapping("/{simulationId}")
    //Regarde une simulation
    public String removeSimulation(@PathVariable long simulationId) {
        return "Supprime la simulation " + simulationId;
    }

    @GetMapping("/{simulationId}/stats")
    //Regarde une simulation
    public String statsSimulation(@PathVariable long simulationId) {
        return "Regarde les stats de la simulation " + simulationId;
    }
}
