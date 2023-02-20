package fr.esipe.way2go.exception.invite;

public class InviteNotFoundException extends RuntimeException {
    public InviteNotFoundException() {
        super("Invitation pas trouvé");
    }
}
