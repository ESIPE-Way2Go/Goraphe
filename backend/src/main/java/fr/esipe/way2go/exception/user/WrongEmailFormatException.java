package fr.esipe.way2go.exception.user;

public class WrongEmailFormatException extends RuntimeException {
    public WrongEmailFormatException() {
        super("Wrong email format. Email must match the following format : name@domain.example");
    }
}
