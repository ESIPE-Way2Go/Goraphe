package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
public class MapController {
    @GetMapping
    //Page de la carte interactive
    public String map() {
        return "Page de la carte interactive";
    }

    @GetMapping
    //Page de la carte interactive centrée sur une ville
    public String mapTownCentred(@RequestParam("townName") String townName) {
        return "Page de la carte interactive centrée sur la ville " + townName;
    }

    @GetMapping("/newSimulation")
    //Nouvelle simulation
    public String newSimulation() {
        return "Nouvelle simulation";
    }
}
