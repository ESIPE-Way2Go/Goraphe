package fr.esipe.way2go.controller;

import fr.esipe.way2go.dto.simulation.request.SimulationRequest;
import fr.esipe.way2go.service.ScriptPythonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.io.File;
import java.nio.file.Path;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/simulation")
public class SimulationController {
    private final ScriptPythonService scriptPythonService;

    @Autowired
    public SimulationController(ScriptPythonService scriptPythonService) {
        this.scriptPythonService = scriptPythonService;
    }

    @PermitAll
    @GetMapping("/launch/{idSimulation}")
    public void launchSimulation(@PathVariable int idSimulation) {
        scriptPythonService.executeScript("jeremy", "sim1", "test");
    }

    @PermitAll
    @GetMapping("/getFile")
    public @ResponseBody  File getFile() {
        var p = System.getProperty("user.dir") + System.getProperty("file.separator")+ "scripts" + System.getProperty("file.separator") + "test.py";
        return new File(Path.of(p).toString());
    }
}
