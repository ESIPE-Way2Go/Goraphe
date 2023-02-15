package fr.esipe.way2go.dao.converter;

import javax.persistence.AttributeConverter;
import java.time.ZoneId;
import java.util.Calendar;
import java.sql.Date;
import java.util.TimeZone;

public class CalendarConverter implements AttributeConverter<Calendar, Date> {
    @Override
    public Date convertToDatabaseColumn(Calendar calendar) {
        if (calendar == null)
            return  null;
        return new Date(calendar.getTimeInMillis());
    }

    @Override
    public Calendar convertToEntityAttribute(Date date) {
        if (date == null)
            return null;
        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return calendar;
    }
}
