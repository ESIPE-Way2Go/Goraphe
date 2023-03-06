package fr.esipe.way2go.repository;

import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SimulationRepository extends JpaRepository<SimulationEntity, Long> {
    Optional<SimulationEntity> findById(Long id);

    @Query("SELECT s FROM SimulationEntity s WHERE user = :user")
    List<SimulationEntity> getSimulationFromUser(@Param("user") UserEntity user);
}
