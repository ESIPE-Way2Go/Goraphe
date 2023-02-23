package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.InviteEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface InviteService {
    InviteEntity save(InviteEntity inviteEntity);

    Optional<InviteEntity> findByToken(String token);
    Optional<InviteEntity> findById(Long id);

    List<InviteEntity> getAll();

    void delete(InviteEntity inviteEntity);
}
