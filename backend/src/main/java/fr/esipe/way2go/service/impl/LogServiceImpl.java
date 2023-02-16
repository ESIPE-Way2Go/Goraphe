package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.dao.LogEntity;
import fr.esipe.way2go.repository.LogRepository;
import fr.esipe.way2go.service.LogService;
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
    public LogEntity save(LogEntity logEntity) {
        return logRepository.save(logEntity);
    }
}
