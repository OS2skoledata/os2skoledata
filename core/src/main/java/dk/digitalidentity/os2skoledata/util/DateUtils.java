package dk.digitalidentity.os2skoledata.util;

import java.time.LocalDate;

import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtils {

	public static LocalDate fromXMLGregorianCalendar(XMLGregorianCalendar date) {
		if (date == null) {
			return null;
		}

		return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
	}
}
