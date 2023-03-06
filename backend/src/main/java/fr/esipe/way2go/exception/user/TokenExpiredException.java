package fr.esipe.way2go.exception.user;

public class TokenExpiredException extends  RuntimeException {
    public TokenExpiredException() {
        super("Token expiré");
    }
}
