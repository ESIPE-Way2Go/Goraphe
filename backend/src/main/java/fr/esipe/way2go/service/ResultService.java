package fr.esipe.way2go.service;

import fr.esipe.way2go.dao.ResultEntity;
import org.springframework.stereotype.Service;

@Service
public interface ResultService {
    ResultEntity save(ResultEntity resultEntity);
}
