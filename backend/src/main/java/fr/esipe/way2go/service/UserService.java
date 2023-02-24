package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.UserEntity;


import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserEntity> getUserById(Long id);
    Optional<UserEntity> getUser(String username);
    Optional<UserEntity> getUserByEmail(String email);
    Optional<UserEntity> getUserByToken(String token);
    UserEntity saveUser(UserEntity user);
    List<UserEntity> getAllUsers();
    void deleteUser(UserEntity e);
}
