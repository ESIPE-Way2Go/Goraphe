package fr.esipe.way2go.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendInvitation(String email, String link);
}
