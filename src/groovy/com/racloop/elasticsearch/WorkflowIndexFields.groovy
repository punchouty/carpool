package com.racloop.elasticsearch

/**
 * Defines the field names for Workflow index. Equivalent to column names to a workflow table in a RDBMS
 * @author Rohit Arora
 *
 */

class WorkflowIndexFields {
	
	public static final String REQUEST_JOURNEY_ID ='requestJourneyId'
	public static final String REQUEST_FROM_PLACE ='requestedFromPlace'
	public static final String REQUEST_TO_PLACE ='requestedToPlace'
	
	public static final String REQUEST_USER ='requestUser'
	public static final String REQUEST_DATE_TIME ='requestedDateTime'
	public static final String STATE ='state'
	
	public static final String MACTHING_USER ='matchingUser'
	public static final String MATCHED_JOURNEY_ID ='matchedJourneyId'
	public static final String MATCHED_FROM_PLACE ='matchedFromPlace'
	
	public static final String MATCHED_TO_PLACE ='matchedToPlace'
	public static final String MATCHED_DATE_TIME ='matchedDateTime'
	public static final String IS_REQUESTER_DRIVING ='isRequesterDriving'
	public static final String IS_MATCHED_USER_DRIVING ='isMatchedUserDriving'

}
