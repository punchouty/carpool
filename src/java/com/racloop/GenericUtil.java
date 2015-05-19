package com.racloop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.elasticsearch.common.joda.time.DateTime;

public class GenericUtil {
	
	public static Date uiDateStringToJavaDate(String source) throws ParseException {
		SimpleDateFormat uiFormatter = new SimpleDateFormat(Constant.DATE_FORMAT_UI);
		Date date = uiFormatter.parse(source);
		return date;
	}
	public static Date uiDateStringToJavaDateForSearch(String source) throws ParseException {
		SimpleDateFormat uiFormatter = new SimpleDateFormat(Constant.DATE_FORMAT_UI);
		uiFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = uiFormatter.parse(source);
		return date;
	}
	
	public static String javaDateToDynamoDbDateString(Date date) throws ParseException {
		SimpleDateFormat dBFormatter = new SimpleDateFormat(Constant.DATE_FORMAT_DYNAMODB);
		dBFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String output = dBFormatter.format(date);
		return output;
	}
	
	public static boolean isDateInRange(Date source, Date target, int minutes){
		DateTime sourceDateTime = new DateTime(source);
		DateTime targetEndTime = new DateTime(target);
		DateTime start = targetEndTime.minusMinutes(minutes);
		DateTime end = targetEndTime.plusMinutes(minutes);
		if(sourceDateTime.isAfter(start) && sourceDateTime.isBefore(end)) {
			return true;
		}
		else {
			return false;
		}
	}

}
