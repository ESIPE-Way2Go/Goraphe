package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.dao.InviteEntity;
import fr.esipe.way2go.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendInvitation(HttpServletRequest request, String email, InviteEntity inviteEntity) {
        var message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(email);
        message.setSubject("Invitation à créer un compte sur Goraphe");
        var link = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/createAccount/" + inviteEntity.getInviteId() + "?token=" + inviteEntity.getToken();
        message.setText(link);
        mailSender.send(message);
    }
}
