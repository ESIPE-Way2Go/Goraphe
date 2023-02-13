package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/administration")
public class AdministrationController {
    @GetMapping
    //Page d'administration
    public String administration() {
        return "Page d'administration";
    }

    @DeleteMapping("/user/{userId}")
    //Supprime un utilisateur
    public String removeUser(@PathVariable long userId) {
        return "Supprime l'utilisateur " + userId;
    }

    @PostMapping("/invite/{inviteId}")
    //Envoie une invitation
    public String sendInvite(@PathVariable long inviteId) {
        return "Envoie l'invitation " + inviteId;
    }

    @PutMapping("/invite/{inviteId}")
    //Re-envoie une invitation
    public String resendInvite(@PathVariable long inviteId) {
        return "Re-envoie l'invitation " + inviteId;
    }

    @DeleteMapping("/invite/{inviteId}")
    //Supprime une invitation
    public String removeInvite(@PathVariable long inviteId) {
        return "Supprime l'invitation " + inviteId;
    }
}
