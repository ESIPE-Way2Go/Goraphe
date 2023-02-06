package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AuthentificationController {
    @GetMapping("/login")
    //Page de connexion
    public String login() {
        return "Page de connexion";
    }

    @PostMapping("/connect")
    //Connexion de l'utilisateur
    public String connect() {
        return "Connexion de l'utilisateur";
    }
}
