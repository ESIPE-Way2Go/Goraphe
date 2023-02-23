package fr.esipe.way2go.repository;

import fr.esipe.way2go.dao.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u FROM UserEntity u WHERE email = :email")
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE token = :token")
    Optional<UserEntity> findByToken(String token);

    @Query("SELECT u FROM UserEntity u WHERE  username is not null")
    List<UserEntity> findUsersAuthenticate();


}
