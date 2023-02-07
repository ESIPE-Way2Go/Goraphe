package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
public class HomeController {
    @GetMapping
    //Page d'accueil
    public String home() {
        return "Page d'accueil";
    }

    @GetMapping
    //Filtre l'affichage des simulations
    public String homeSearch(@RequestParam("search") String search) {
        return "Page d'accueil filtrée avec filtre = " + search;
    }
}
