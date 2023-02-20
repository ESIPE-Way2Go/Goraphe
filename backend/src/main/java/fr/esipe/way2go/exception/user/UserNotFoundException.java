package fr.esipe.way2go.exception.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super(String.format("User with Id %d not found", id));
    }
}

