package fr.esipe.way2go.controller;

import fr.esipe.way2go.service.impl.ScriptPythonServiceImpl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/simulation")
public class SimulationController {
    @PermitAll
    @GetMapping("/launch")
    public void launchSimulation() {
        ScriptPythonServiceImpl.executeScript("jeremy", "sim1", "test");
    }
}
