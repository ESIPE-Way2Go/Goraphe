package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.LogEntity;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Service;

@Service
public interface LogService {
    LogEntity createLog(LogEntity logEntity);
}
