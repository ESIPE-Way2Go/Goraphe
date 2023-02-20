package fr.esipe.way2go.exception;

public class EmailFormatWrongException extends RuntimeException {
    public EmailFormatWrongException() {
        super("Mauvais format d'email. Le mail doit être écrit sous la forme nom@domain.com");
    }
}
