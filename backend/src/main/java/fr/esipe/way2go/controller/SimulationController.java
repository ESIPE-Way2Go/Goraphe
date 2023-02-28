package fr.esipe.way2go.controller;

import fr.esipe.way2go.configuration.jwt.JwtUtils;
import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dto.simulation.request.SimulationRequest;
import fr.esipe.way2go.dto.simulation.response.SimulationHomeResponse;
import fr.esipe.way2go.dto.simulation.response.SimulationIdResponse;
import fr.esipe.way2go.dto.simulation.response.SimulationMapResponse;
import fr.esipe.way2go.dto.simulation.response.SimulationResponse;
import fr.esipe.way2go.exception.simulation.SimulationForbidden;
import fr.esipe.way2go.exception.simulation.SimulationNameFormatWrong;
import fr.esipe.way2go.exception.simulation.SimulationNotFoundException;
import fr.esipe.way2go.exception.simulation.SimulationTooLaunch;
import fr.esipe.way2go.service.ScriptPythonService;
import fr.esipe.way2go.service.SimulationService;
import fr.esipe.way2go.service.UserService;
import fr.esipe.way2go.utils.StatusSimulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {
    private final SimulationService simulationService;
    private final UserService userService;
    private final ScriptPythonService scriptPythonService;
    private final JwtUtils jwtUtils;

    @Value("${nbThread}")
    private int nbThreads;

    public static Map<Long, Thread> THREAD_SIMULATIONS = new HashMap<>();

    @Autowired
    public SimulationController(SimulationService simulationService, UserService userService, ScriptPythonService scriptPythonService, JwtUtils jwtUtils) {
        this.simulationService = simulationService;
        this.userService = userService;
        this.scriptPythonService = scriptPythonService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Create simulation
     *
     * @param headers           : params where it is the token
     * @param simulationRequest : information of the simulation
     * @return the id of the simulation
     */

    @PostMapping
    public ResponseEntity<SimulationIdResponse> createSimulation(@RequestHeader HttpHeaders headers, @RequestBody SimulationRequest simulationRequest) {
        if (THREAD_SIMULATIONS.size() == nbThreads)
            throw new SimulationTooLaunch();
        if (simulationRequest.getName().contains(" "))
            throw new SimulationNameFormatWrong();

        var userName = jwtUtils.getUsersFromHeaders(headers);
        var userOptional = userService.getUser(userName);
        if (userOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        var user = userOptional.get();
        if(simulationRequest.getDistance()<100 || !simulationRequest.checkBounds() || simulationRequest.getRoadTypes().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        var simulation = new SimulationEntity(simulationRequest.getName(), user, simulationRequest.getDesc(), simulationRequest.getDistance(),  simulationRequest.getScript(), simulationRequest.getRoadTypes());
        var simulationSave = simulationService.save(simulation);
        var midPoint= new MapController.Point(simulationRequest.getCenter());
        var thread = new Thread(() -> {
            simulationSave.setBeginDate(Calendar.getInstance());
            simulationSave.setStatus(StatusSimulation.LOAD.getDescription());
            simulationService.save(simulationSave);
            scriptPythonService.executeScript(user, simulationSave, midPoint, simulationRequest);
        });
        thread.start();
        THREAD_SIMULATIONS.putIfAbsent(simulationSave.getSimulationId(), thread);
        return new ResponseEntity<>(new SimulationIdResponse(simulationSave.getSimulationId()), HttpStatus.ACCEPTED);
    }

    /**
     * Get information of the simulation
     *
     * @param headers : params where it is the token
     * @param id      id of the simulation
     * @return the details of the simulation
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

    /**
     * Get all simulations of users
     * @param headers : token
     * @return list of simulation for the front
     */
    @GetMapping
    public ResponseEntity<List<SimulationHomeResponse>> getSimulations(@RequestHeader HttpHeaders headers) {
        var userEntityOptional = userService.getUser(jwtUtils.getUsersFromHeaders(headers));
        if (userEntityOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        var user = userEntityOptional.get();
        var simulations = simulationService.getSimulationsOfUser(user);
        var simulationsResponse = new ArrayList<SimulationHomeResponse>();
        simulations.forEach(simulation -> simulationsResponse.add(new SimulationHomeResponse(simulation)));
        return new ResponseEntity<>(simulationsResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteSimulation(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
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
        simulationService.deleteSimulation(simulation);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @GetMapping("/{id}/map")
    public ResponseEntity<SimulationMapResponse> getSimulationForMap(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
       var userEntityOptional = userService.getUser(jwtUtils.getUsersFromHeaders(headers));
       if (userEntityOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        var user = userEntityOptional.get();
        var simulationEntityOptional = simulationService.find(id);
        if (simulationEntityOptional.isEmpty())
            throw new SimulationNotFoundException();
        var simulation = simulationEntityOptional.get();
        if (!simulation.getUser().equals(user))
            throw new SimulationForbidden();

        return new ResponseEntity<>(new SimulationMapResponse(simulation), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Object> cancelSimulation(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
        var thread = THREAD_SIMULATIONS.get(id);
        thread.interrupt();
        THREAD_SIMULATIONS.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
