package fr.esipe.way2go.controller;

import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.service.LogService;
import fr.esipe.way2go.service.ScriptPythonService;
import fr.esipe.way2go.service.SimulationService;
import fr.esipe.way2go.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;

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
    @PostMapping("/{name}")
    public void createSimulation(@PathVariable String name) {
        var user = userService.getUser("jeremy");
        var simulation = new SimulationEntity(name, user.get(), "Hello world");
        var simulationSave = simulationService.createSimulation(simulation);
        scriptPythonService.executeScript(user.get(), simulationSave, "test");
    }

    @GetMapping("/{id}")
    public String getLog(@PathVariable Long id) {
        var simulation = simulationService.find(id).get();
        return simulation.getLogs().get(0).getContent();
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
