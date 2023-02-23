package fr.esipe.way2go.exception.user;

public class UsernameExistAlreadyException extends RuntimeException {
    public UsernameExistAlreadyException() {
        super("Votre pseudo est déjà utilisez, veuillez le changer");
    }
}
