package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.InviteEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface InviteService {
    InviteEntity save(InviteEntity inviteEntity);

    Optional<InviteEntity> findByToken(String token);
}
