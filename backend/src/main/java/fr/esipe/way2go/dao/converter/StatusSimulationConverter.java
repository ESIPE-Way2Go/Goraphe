package fr.esipe.way2go.dao.converter;

import fr.esipe.way2go.utils.StatusSimulation;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StatusSimulationConverter implements AttributeConverter<StatusSimulation, String>  {

    @Override
    public String convertToDatabaseColumn(StatusSimulation statusSimulation) {
        return statusSimulation.name();
    }

    @Override
    public StatusSimulation convertToEntityAttribute(String status) {
        return StatusSimulation.valueOf(status);
    }
}


