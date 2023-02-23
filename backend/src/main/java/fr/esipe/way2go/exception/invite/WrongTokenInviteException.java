package fr.esipe.way2go.exception.invite;

public class WrongTokenInviteException extends RuntimeException {
    public WrongTokenInviteException() {
        super("Mauvais token");
    }
}
