package fr.esipe.way2go.exception.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super(String.format("User not found"));
    }
}

