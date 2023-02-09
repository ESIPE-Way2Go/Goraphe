package fr.esipe.way2go.repository;

import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SimulationRepository extends JpaRepository<SimulationEntity, Long> {
    Optional<SimulationEntity> findById(Long id);
}
