package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.InviteEntity;
import fr.esipe.way2go.dao.UserEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface EmailService {
    void sendInvitation(String email, InviteEntity inviteEntity);

    void sendLinkForForgetPassword(UserEntity userEntity);
}
