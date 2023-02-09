package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.repository.SimulationRepository;
import fr.esipe.way2go.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;
    @Autowired
    public SimulationServiceImpl(SimulationRepository simulationRepository) {
        this.simulationRepository = simulationRepository;
    }
    @Override
    public SimulationEntity createSimulation(SimulationEntity simulationEntity) {
        return simulationRepository.save(simulationEntity);
    }

    @Override
    public Optional<SimulationEntity> find(Long id) {
        return simulationRepository.findById(id);
    }
}
