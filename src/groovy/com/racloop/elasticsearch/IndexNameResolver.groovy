package com.racloop.elasticsearch

import org.elasticsearch.common.joda.time.DateTime

import com.racloop.util.date.DateUtil

class IndexNameResolver {
	
	public static final String INDEX_DELIMETER = '@'
	private static final String WORKFLOW_INDEX_PREFIX = "workflow"
	
	private final int SATURDAY = 6
	private final int SUNDAY = 7
	
	public String getIndexNameFromId(String idString){
		def splitted = idString.split(INDEX_DELIMETER)
		String indexName = splitted[0]
		return indexName
		
	}
	
	public String generateIndexNameFromDate (DateTime inputDate = null){
		return getFormattedDate(inputDate)
	}
	
	public String[] getPossibleIndexNameFromDate(DateTime currentDate){
		int dayOfWeek = currentDate.dayOfWeek
		if(dayOfWeek == SATURDAY){
			def indexNames = []
			indexNames << generateIndexNameFromDate(currentDate)
			indexNames << generateIndexNameFromDate(currentDate.plusDays(1))
			return indexNames as String[]
		}
		else {
			return [generateIndexNameFromDate(currentDate)] as String[]
		}
	}
	
	public String generateWorkflowIndexNameFromDate(DateTime inputDate = null){
		return WORKFLOW_INDEX_PREFIX + getFormattedDate(inputDate)
	}
	
	public String[] getPossibleWorkflowIndexNameFromDate(DateTime currentDate){
		int dayOfWeek = currentDate.dayOfWeek
		if(dayOfWeek == SATURDAY){
			def indexNames = []
			indexNames << generateWorkflowIndexNameFromDate(currentDate)
			indexNames << generateWorkflowIndexNameFromDate(currentDate.plusDays(1))
			return indexNames as String[]
		}
		else {
			return [generateWorkflowIndexNameFromDate(currentDate)] as String[]
		}
		
	}
	
	public String getWorkflowIndexNameFromId(String idString){
		return WORKFLOW_INDEX_PREFIX + getIndexNameFromId(idString)
	}
	
	private String getFormattedDate(DateTime inputDate) {
		Date currentDate = inputDate ? inputDate.toDate() : new Date()
		String formattedDate = DateUtil.convertJavaDateToIndexNameFormat(currentDate)
		return formattedDate
	}

}
