package fr.esipe.way2go.controller;

import fr.esipe.way2go.configuration.jwt.JwtUtils;
import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dto.simulation.request.SimulationRequest;
import fr.esipe.way2go.dto.simulation.response.SimulationHomeResponse;
import fr.esipe.way2go.dto.simulation.response.SimulationIdResponse;
import fr.esipe.way2go.dto.simulation.response.SimulationMapResponse;
import fr.esipe.way2go.dto.simulation.response.SimulationResponse;
import fr.esipe.way2go.exception.simulation.*;
import fr.esipe.way2go.repository.SimulationRepository;
import fr.esipe.way2go.service.ScriptPythonService;
import fr.esipe.way2go.service.SimulationService;
import fr.esipe.way2go.service.UserService;
import fr.esipe.way2go.utils.StatusSimulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.Resource;


@RestController
@RequestMapping("/api/simulation")
public class SimulationController {
    private final SimulationService simulationService;
    private final UserService userService;
    private final ScriptPythonService scriptPythonService;
    private final JwtUtils jwtUtils;

    @Value("${nbThread}")
    private int nbThreads;

    public static final Map<Long, Thread> THREAD_SIMULATIONS = new HashMap<>();
    private final SimulationRepository simulationRepository;

    @Autowired
    public SimulationController(SimulationService simulationService, UserService userService, ScriptPythonService scriptPythonService, JwtUtils jwtUtils,
                                SimulationRepository simulationRepository) {
        this.simulationService = simulationService;
        this.userService = userService;
        this.scriptPythonService = scriptPythonService;
        this.jwtUtils = jwtUtils;
        this.simulationRepository = simulationRepository;
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
        if (simulationRequest.getName().matches("^\\d"))
            throw new SimulationNameNotBeginByNumberException();

        var userName = jwtUtils.getUsersFromHeaders(headers);
        var userOptional = userService.getUser(userName);
        if (userOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        var user = userOptional.get();
        if(simulationRequest.getDistance()<100 || !simulationRequest.checkBounds() || simulationRequest.getRoadTypes().isEmpty() || simulationRequest.getName().isBlank() || simulationRequest.getRandomPoints() <2 || simulationRequest.getRandomPoints()> 100){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        var simulation = new SimulationEntity(simulationRequest.getName(), user, simulationRequest.getDesc(), simulationRequest.getDistance(),  simulationRequest.getScript(), simulationRequest.getRoadTypes());
        var simulationSave = simulationService.save(simulation);
        var midPoint= new ScriptPythonService.Point(simulationRequest.getCenter());
        var thread = new Thread(() -> {
            simulationSave.setBeginDate(Calendar.getInstance());
            simulationSave.setStatus(StatusSimulation.LOAD);
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

    /**
     * Function to delete a simulation
     * @param headers : Token of the user, to make sure he has the right to delete the simulation
     * @param id : ID of the simulation to delete
     * @return Either OK or an exception (not found, forbidden)
     */

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

    /**
     * Provides the necessary data to display the graph, road and random points on the map of the result
     * @param headers : Token of the user
     * @param id : id of the simulation
     * @return SimulationMapResponse & status 200 OR simulation not found / forbidden
     */
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

    /**
     * Cancels a simulation that is currently running, doesn't remove it from the database
     * @param headers : Token of the connected user, to make sure he has the right to cancel the simulation
     * @param id : ID of the simulation
     * @return OK
     */
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Object> cancelSimulation(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
        var thread = THREAD_SIMULATIONS.get(id);
        if (thread == null) {
            var simulationEntityOptional = simulationService.find(id);
            if (simulationEntityOptional.isEmpty()) {
                throw new SimulationNotFoundException();
            }
            var simulation = simulationEntityOptional.get();
            simulation.setStatus(StatusSimulation.CANCEL);
            simulation.setEndDate(Calendar.getInstance());
            simulationService.save(simulation);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        thread.interrupt();
        THREAD_SIMULATIONS.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Downloads a Zip file with all the Excel files generated by a simulation
     * @param id : id of the simulation
     * @return : Zip with the excels generated
     * @throws IOException In case there is an issue accessing the excels
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadExcels(@PathVariable Long id) throws IOException {
        var simulationOptional = simulationService.find(id);
        if (simulationOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        var simulation=simulationOptional.get();
        var user=simulation.getUser();
        var formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        var sep = System.getProperty("file.separator");
        var pathGeneric = System.getProperty("user.dir") + sep + "scripts" + sep;
        var simulationName = simulation.getName() + "_" + formatter.format(simulation.getBeginDate().getTime());
        var excelPath = pathGeneric + user.getUsername() + sep + simulationName;
        var zipFileName = simulationName+".zip";

        var baos = new ByteArrayOutputStream();
        try (var zos = new ZipOutputStream(baos);
        var files = Files.list(Paths.get(excelPath))) {
            files.filter(path -> path.getFileName().toString().endsWith(".xlsx"))
                    .forEach(path -> {
                        try {
                            var zipEntry = new ZipEntry(path.getFileName().toString());
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException();
                        }
                    });
        }
        var resource = new ByteArrayResource(baos.toByteArray());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName.split("_")[0] + ".zip\"")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}
