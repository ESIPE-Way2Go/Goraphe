package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/createAccount")
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

    @PostMapping("/lostPassword")
    //Demande de réinitialisation de mot de passe
    public String lostPassword() {
        return "Demande de réinitialisation de mot de passe pour l'email";
    }

    @PutMapping("/updateUser")
    //Changement de mot de passe
    public String changedPassword() {
        return "Changement de mot de passe";
    }
}
