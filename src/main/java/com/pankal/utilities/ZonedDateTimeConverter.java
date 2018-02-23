package com.pankal.utilities;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(ZonedDateTime zoneDateTime) {
		LocalDateTime withoutTimezone = zoneDateTime.toLocalDateTime();
		Timestamp timestamp = Timestamp.valueOf(withoutTimezone);
		return (timestamp == null ? null : timestamp);
	}


	@Override
	public ZonedDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
		LocalDateTime withoutTimezone = sqlTimestamp.toLocalDateTime();
		ZonedDateTime withTimezone = withoutTimezone.atZone(ZoneId.systemDefault());
		return withTimezone != null ? withTimezone : null;
	}
}
