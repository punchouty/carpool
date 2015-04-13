package com.racloop.util.date

import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.format.DateTimeFormat
import org.elasticsearch.common.joda.time.format.DateTimeFormatter
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat

class DateUtil {
	
	private static final String JAVA_DATE_FORMAT = "dd MMM yy hh:mm a";
	private static final String NEW_JAVA_DATE_FORMAT = "dd MM yyyy    hh:mm a";
	private static final DateTimeFormatter UI_DATE_FORMAT = DateTimeFormat.forPattern(JAVA_DATE_FORMAT);
	private static final DateTimeFormatter NEW_UI_DATE_FORMAT = DateTimeFormat.forPattern(NEW_JAVA_DATE_FORMAT);
	private static final DateTimeFormatter BASIC_DATE_FORMAT = ISODateTimeFormat.dateOptionalTimeParser();
	private static final String INDEX_NAME_DATE_FORMAT = "YYYY-ww"
	
	
	public static DateTime convertUIDateToElasticSearchDate(String dateAsString) {
		DateTime dateTime = null
		try {
		 dateTime = UI_DATE_FORMAT.parseDateTime(dateAsString)
		}
		catch(IllegalArgumentException ex) {
			dateTime = NEW_UI_DATE_FORMAT.parseDateTime(dateAsString)
		}
		
		return dateTime
	}
	
	public static DateTime convertElasticSearchDateToDateTime(String elasticSearchDateAsString) {
		return BASIC_DATE_FORMAT.parseDateTime(elasticSearchDateAsString)
	}
	
	/**
	 * Converts Date object to YYYY-ww format i.e Year Month week of the year. So 6th Apr 2015 will be 2105-15
	 * @param java.util.Date
	 * @return
	 */
	public static String convertJavaDateToIndexNameFormat(Date myDate){
		return myDate.format(INDEX_NAME_DATE_FORMAT)
	}
	

}
