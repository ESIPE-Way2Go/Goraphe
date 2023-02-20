package fr.esipe.way2go.repository;

import fr.esipe.way2go.dao.InviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<InviteEntity, Long> {
    @Query("SELECT i FROM InviteEntity i WHERE token = :token")
    Optional<InviteEntity> findByToken(String token);
}
