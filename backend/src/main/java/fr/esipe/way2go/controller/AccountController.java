package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/createAccount")
public class AccountController {
    @GetMapping
    //Page de création de compte
    public String account() {
        return "Page de création de compte";
    }

    @PostMapping
    //Crée le compte utilisateur
    public String createAccount() {
        return "Création du compte";
    }
}
