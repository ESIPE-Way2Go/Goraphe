package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.dao.InviteEntity;
import fr.esipe.way2go.repository.InviteRepository;
import fr.esipe.way2go.service.InviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InviteServiceImpl implements InviteService {

    private InviteRepository inviteRepository;

    @Autowired
    public InviteServiceImpl(InviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    @Override
    public InviteEntity save(InviteEntity inviteEntity) {
        return inviteRepository.save(inviteEntity);
    }

    @Override
    public Optional<InviteEntity> findByToken(String token) {
        return  inviteRepository.findByToken(token);
    }



    @Override
    public Optional<InviteEntity> findById(Long id) {
        return inviteRepository.findById(id);
    }

    @Override
    public List<InviteEntity> getAll() {
        return inviteRepository.findAll();
    }

    @Override
    public void delete(InviteEntity inviteEntity) {
        inviteRepository.delete(inviteEntity);
    }
}
