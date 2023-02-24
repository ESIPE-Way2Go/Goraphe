package fr.esipe.way2go.exception;

public class UserEmailFound extends RuntimeException {
    public UserEmailFound(String email) {
        super("Un utilisateur a déjà cet email : " + email + ". Deux utilisateurs ne peuvent pas avoir le même mail.");
    }
}
