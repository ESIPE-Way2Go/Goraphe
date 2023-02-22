package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.dao.InviteEntity;
import fr.esipe.way2go.dao.UserEntity;
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
    @Value("${sever.url.dev}")
    private String uri;

    @Override
    public void sendInvitation(String email, InviteEntity inviteEntity) {
        var subject = "Invitation à créer un compte sur Goraphe";
        var link = uri + "/createAccount/" + inviteEntity.getToken();
        var content = """                                
                Bonjour,
                                
                Nous sommes heureux de vous inviter à créer un compte sur Goraphe, notre plateforme en ligne pour la visualisation et l'analyse de données. Avec Goraphe, vous pouvez créer des graphiques et des visualisations à partir de vos données, collaborer avec vos collègues et partager vos résultats avec d'autres.
                                
                Pour créer votre compte, veuillez cliquer sur le lien suivant: 
                """ + link + """
                                
                Nous vous demandons de bien vouloir remplir le formulaire d'inscription avec vos informations personnelles et professionnelles afin que nous puissions mieux comprendre vos besoins et vous offrir une expérience personnalisée sur Goraphe.                                
                Nous sommes impatients de vous voir sur Goraphe et de travailler avec vous pour tirer le meilleur parti de vos données.
                                
                Cordialement,
                L'équipe Goraphe
                """;
        mailSender.send(createEmail(email, subject, content));
    }

    @Override
    public void sendLinkForForgetPassword(UserEntity userEntity) {
        var subject = "Récupération de votre mot de passe";
        var link = uri + "/modifyPassword/" + userEntity.getToken();
        var body = "Bonjour " + userEntity.getUsername() + "," + """
                                
                Nous avons reçu une demande de réinitialisation de mot de passe pour votre compte. Si vous n'êtes pas à l'origine de cette demande, veuillez ignorer ce message.                               
                Si vous avez oublié votre mot de passe, nous sommes là pour vous aider. Pour réinitialiser votre mot de passe, veuillez cliquer sur le lien ci-dessous :                                
                """ + link + """
                
                Si le lien ne fonctionne pas, copiez-le et collez-le dans la barre d'adresse de votre navigateur.                               
                Après avoir cliqué sur le lien, vous serez redirigé vers une page où vous pourrez créer un nouveau mot de passe. Nous vous recommandons de choisir un mot de passe sécurisé en utilisant une combinaison de lettres, de chiffres et de caractères spéciaux.                                
                Si vous avez des difficultés pour réinitialiser votre mot de passe ou si vous avez d'autres questions, n'hésitez pas à contacter notre équipe d'assistance à l'adresse suivante : [Insérez l'adresse e-mail de l'assistance].
                                
                Merci de votre confiance en notre service.
                                
                Cordialement,
                                
                L'équipe de support Goraphe
                """;

        mailSender.send(createEmail(userEntity.getEmail(), subject, body));
    }

    private SimpleMailMessage createEmail(String receiver, String subject, String body) {
        var message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(receiver);
        message.setText(body);
        message.setSubject(subject);
        return message;
    }

}
