package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendInvitation(String email, String link) {
        var message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo("jeremy.valade@edu.univ-eiffel.fr");
        message.setText("this is message");
        message.setSubject("TEST");
        mailSender.send(message);
    }
}
