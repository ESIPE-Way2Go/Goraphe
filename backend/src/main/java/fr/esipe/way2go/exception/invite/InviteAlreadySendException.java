package fr.esipe.way2go.exception.invite;

public class InviteAlreadySendException extends RuntimeException {
    public InviteAlreadySendException(String email) {
        super("Invitation déja créée pour l'email : "+email);
    }
}
