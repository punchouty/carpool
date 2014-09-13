package com.racloop.util.date

import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.format.DateTimeFormat
import org.elasticsearch.common.joda.time.format.DateTimeFormatter
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat

class DateUtil {
	
	private static final String JAVA_DATE_FORMAT = "dd MMMM yyyy    hh:mm a";
	private static final DateTimeFormatter UI_DATE_FORMAT = DateTimeFormat.forPattern(JAVA_DATE_FORMAT);
	private static final DateTimeFormatter BASIC_DATE_FORMAT = ISODateTimeFormat.dateOptionalTimeParser();
	
	
	public static DateTime convertUIDateToElasticSearchDate(String dateAsString) {
		return UI_DATE_FORMAT.parseDateTime(dateAsString)
	}
	
	public static DateTime convertElasticSearchDateToDateTime(String elasticSearchDateAsString) {
		return BASIC_DATE_FORMAT.parseDateTime(elasticSearchDateAsString)
	}

}
