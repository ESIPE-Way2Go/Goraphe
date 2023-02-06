package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
public class HomeController {
    @GetMapping
    //Regarde une simulation
    public String home() {
        return "Page d'accueil";
    }

    @GetMapping
    //Filtre l'affichage des simulations
    public String homeSearch(@RequestParam("search") String search) {
        return "Page d'accueil filtrée avec filtre = " + search;
    }
}
