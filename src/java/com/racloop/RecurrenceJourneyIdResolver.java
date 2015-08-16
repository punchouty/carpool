package com.racloop;

import static org.joda.time.DateTimeConstants.MINUTES_PER_DAY;
import static org.joda.time.DateTimeConstants.MINUTES_PER_HOUR;

import java.util.Date;

import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.LocalDate;


public class RecurrenceJourneyIdResolver {
	
	public static String generateIdFromDate(Date inputDate) {
		DateTime inputDateTime = new DateTime(inputDate.getTime());
		return String.valueOf((inputDateTime.getDayOfWeek()* MINUTES_PER_DAY) + inputDateTime.getMinuteOfDay());
	}
	
	
	public static String generateIdFromDate(Date inputDate, String dayOfWeek) {
		Integer dayOfWeekInt = Integer.valueOf(dayOfWeek);
		DateTime inputDateTime = new DateTime(inputDate.getTime());
		return String.valueOf((dayOfWeekInt* MINUTES_PER_DAY) + inputDateTime.getMinuteOfDay());
	}
	
	public static DateTime resolveDateFromId(String inputIdString) {
		Integer inputId = Integer.valueOf(inputIdString);
		int dayOfWeek = inputId / MINUTES_PER_DAY;
		int hour = (inputId % MINUTES_PER_DAY) / MINUTES_PER_HOUR;
		int min = (inputId % MINUTES_PER_DAY) % MINUTES_PER_HOUR;
		LocalDate myDate = getUpcomingDayOfWeek(dayOfWeek);
		DateTime myDateTime = myDate.toDateTimeAtStartOfDay().plusHours(hour).plusMinutes(min);
		return myDateTime;
		
	}
	
	private static LocalDate getUpcomingDayOfWeek(int dayOfWeek){
		DateTime currentDate = new DateTime();
		while (currentDate.getDayOfWeek() != dayOfWeek) {
			currentDate = currentDate.plusDays(1);
		}
		return currentDate.toLocalDate();
	}
	
	public static DateTime getFloorDateTimeFromCurrentDate() {
		DateTime dt = new DateTime().hourOfDay().roundCeilingCopy();
		return dt;
		
	}

}
