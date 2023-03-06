package fr.esipe.way2go.exception.user;

public class WrongPasswordFormatException extends RuntimeException{
    public WrongPasswordFormatException() {
        super("Password must be at least 8 characters long, and must include at least 1 uppercase letter, 1 lowercase letter and 1 digit.");
    }
}
