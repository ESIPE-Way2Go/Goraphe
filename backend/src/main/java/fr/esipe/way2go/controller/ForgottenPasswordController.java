package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class ForgottenPasswordController {
    @GetMapping("/lostPassword")
    //Page de demande de réinitialisation de mot de passe
    public String lostPassword() {
        return "Page de demande de réinitialisation de mot de passe";
    }

    @GetMapping("/changePassword")
    //Page de changement de mot de passe
    public String changePassword() {
        return "Page de changement de mot de passe";
    }

    @PostMapping("/lostPassword")
    //Demande de réinitialisation de mot de passe
    public String lostedPassword() {
        return "Demande de réinitialisation de mot de passe";
    }

    @PostMapping("/changePassword")
    //Changement de mot de passe
    public String changedPassword() {
        return "Changement de mot de passe";
    }
}
