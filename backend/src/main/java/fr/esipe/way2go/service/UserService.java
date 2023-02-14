package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.UserEntity;
import org.apache.catalina.User;

import java.util.Optional;

public interface UserService {
    Optional<UserEntity> getUser(String username);

    UserEntity saveUser(UserEntity user);
}
