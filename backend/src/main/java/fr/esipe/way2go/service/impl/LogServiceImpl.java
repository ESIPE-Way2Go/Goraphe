package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.dao.LogEntity;
import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.repository.LogRepository;
import fr.esipe.way2go.repository.SimulationRepository;
import fr.esipe.way2go.service.LogService;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;
    @Autowired
    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }
    @Override
    public void createLog(LogEntity logEntity) {
        logRepository.save(logEntity);
    }
}
