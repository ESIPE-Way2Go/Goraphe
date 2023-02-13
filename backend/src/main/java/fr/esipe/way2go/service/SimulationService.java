package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.SimulationEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface SimulationService {

    SimulationEntity createSimulation(SimulationEntity simulationEntity);

    Optional<SimulationEntity> find(Long id);
}
