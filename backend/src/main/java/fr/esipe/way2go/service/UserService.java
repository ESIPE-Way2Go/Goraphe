package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.UserEntity;

import java.util.Optional;

public interface UserService {
    Optional<UserEntity> getUser(String userId);
}
