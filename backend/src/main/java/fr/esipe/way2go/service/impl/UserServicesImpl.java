package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.repository.UserRepository;
import fr.esipe.way2go.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServicesImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServicesImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> getUser(String userId) {
        return userRepository.findByUsername(userId);
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        var list= new ArrayList<UserEntity>();
        userRepository.findAll().forEach(list::add);
        return list;
    }

    @Override
    public void deleteUser(UserEntity e) {
        userRepository.delete(e);
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> getUserByToken(String token) {
        return userRepository.findByToken(token);
    }
}
