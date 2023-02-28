package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.dao.ResultEntity;
import fr.esipe.way2go.repository.ResultRepository;
import fr.esipe.way2go.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResultServiceImpl implements ResultService {
    private ResultRepository resultRepository;

    @Autowired
    public ResultServiceImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }
    @Override
    public ResultEntity save(ResultEntity resultEntity) {
        return resultRepository.save(resultEntity);
    }
}
