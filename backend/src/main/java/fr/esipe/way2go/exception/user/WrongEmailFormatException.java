package fr.esipe.way2go.exception.user;

public class WrongEmailFormatException extends RuntimeException {
    public WrongEmailFormatException() {
        super("Format incorrect. Le format de l'email doit être le suivant : name@domain.example");
    }
}
