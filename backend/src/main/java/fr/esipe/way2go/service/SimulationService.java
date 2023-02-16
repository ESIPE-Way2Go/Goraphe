package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SimulationService {

    SimulationEntity save(SimulationEntity simulationEntity);

    Optional<SimulationEntity> find(Long id);


    List<SimulationEntity> getSimulationsOfUser(UserEntity userEntity);

    void deleteSimulation(SimulationEntity simulation);


}
