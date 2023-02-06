package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulation")
public class SimulationController {
    @GetMapping("/{simulationId}")
    //Regarde une simulation
    public String lookSimulation(@PathVariable long simulationId) {
        return "Regarde la simulation " + simulationId;
    }

    @DeleteMapping("/{simulationId}/deleteSimulation")
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
