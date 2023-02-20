package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.InviteEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface EmailService {
    void sendInvitation(HttpServletRequest request, String email, InviteEntity inviteEntity);
}
