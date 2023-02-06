package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administration")
public class AdministrationController {
    @GetMapping
    //Page d'administration
    public String administrationPage() {
        return "Page d'administration";
    }

    @DeleteMapping("/user/remove/{userId}")
    //Supprime un utilisateur
    private String removeUser(@PathVariable long userId) {
        return "Supprime un utilisateur";
    }

    @PutMapping("/invite/send/{inviteId}")
    //Envoie une invitation
    private String sendInvite(@PathVariable long inviteId) {
        return "Envoie une invitation";
    }

    @PutMapping("/invite/resend/{inviteId}")
    //Re-envoie une invitation
    private String resendInvite(@PathVariable long inviteId) {
        return "Re-envoie une invitation";
    }

    @DeleteMapping("/invite/remove/{inviteId}")
    //Supprime une invitation
    private String removeInvite(@PathVariable long inviteId) {
        return "Supprime une invitation";
    }
}
