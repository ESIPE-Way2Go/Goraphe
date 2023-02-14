package fr.esipe.way2go.controller;

import fr.esipe.way2go.configuration.jwt.JwtUtils;
import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dto.simulation.request.SimulationRequest;
import fr.esipe.way2go.dto.simulation.response.SimulationIdResponse;
import fr.esipe.way2go.dto.simulation.response.SimulationResponse;
import fr.esipe.way2go.service.ScriptPythonService;
import fr.esipe.way2go.service.SimulationService;
import fr.esipe.way2go.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {
    private final SimulationService simulationService;
    private final UserService userService;
    private final ScriptPythonService scriptPythonService;

    private final JwtUtils jwtUtils;

    @Autowired
    public SimulationController(SimulationService simulationService, UserService userService, ScriptPythonService scriptPythonService, JwtUtils jwtUtils) {
        this.simulationService = simulationService;
        this.userService = userService;
        this.scriptPythonService = scriptPythonService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Create simulation
     * @param headers : params where it is the token
     * @param simulationRequest : information of the simulation
     * @return the id of the simulation
     */

    @PostMapping
    public ResponseEntity<SimulationIdResponse> createSimulation(@RequestHeader HttpHeaders headers, @RequestBody SimulationRequest simulationRequest) {
        var userName = jwtUtils.getUsersFromHeaders(headers);
        var userOptional = userService.getUser(userName);
        if (userOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        var user = userOptional.get();
        var simulation = new SimulationEntity(simulationRequest.getName(), user, "Hello world", "test");
        var simulationSave = simulationService.createSimulation(simulation);
        scriptPythonService.executeScript(user, simulationSave, "test");
        return new ResponseEntity<>(new SimulationIdResponse(simulationSave.getSimulationId()), HttpStatus.ACCEPTED);
    }

    /**
     * Get information of the simulation
     * @param headers : params where it is the token
     * @param id id of the simulation
     * @return
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<SimulationResponse> getSimulation(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
        var simulationEntityOptional = simulationService.find(id);
        if (simulationEntityOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        var simulation = simulationEntityOptional.get();
        var userEntityOptional = userService.getUser(jwtUtils.getUsersFromHeaders(headers));
        if (userEntityOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        var user = userEntityOptional.get();
        if (!simulation.getUser().equals(user)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(new SimulationResponse(simulation), HttpStatus.OK);
    }
}
