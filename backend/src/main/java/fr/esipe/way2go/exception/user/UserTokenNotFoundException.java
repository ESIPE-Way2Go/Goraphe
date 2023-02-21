package fr.esipe.way2go.exception.user;

public class UserTokenNotFoundException extends RuntimeException {
    public UserTokenNotFoundException() {
        super("Token of user is not found");
    }
}
