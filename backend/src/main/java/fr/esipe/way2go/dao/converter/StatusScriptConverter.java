package fr.esipe.way2go.dao.converter;

import fr.esipe.way2go.utils.StatusScript;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StatusScriptConverter implements AttributeConverter<StatusScript, String>  {
    @Override
    public String convertToDatabaseColumn(StatusScript statusScript) {
        return statusScript.name();
    }

    @Override
    public StatusScript convertToEntityAttribute(String status) {
        return StatusScript.valueOf(status);
    }
}
