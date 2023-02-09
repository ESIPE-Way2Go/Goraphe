package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/map")
public class MapController {
    @GetMapping
    //Page de la carte interactive
    public String map() {
        return "Page de la carte interactive";
    }



    @PostMapping("/newSimulation")
    //Nouvelle simulation
    public String newSimulation() {
        return "Nouvelle simulation";
    }
}
