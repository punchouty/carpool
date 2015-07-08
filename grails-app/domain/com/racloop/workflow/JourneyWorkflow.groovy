package com.racloop.workflow

import com.racloop.journey.workkflow.WorkflowState;

@Deprecated
class JourneyWorkflow {
	
	String id
	String requestJourneyId
	String requestedFromPlace
	String requestedToPlace
	String requestUser
	Date requestedDateTime
	String state
	String matchingUser
	String matchedJourneyId
	String matchedFromPlace
	String matchedToPlace
	Date matchedDateTime
	boolean isRequesterDriving
	boolean isMatchedUserDriving

    static constraints = {
		
		state inList : WorkflowState.values()*.state
    }
	
	static mapping = {
		id generator: "com.racloop.util.domain.JourneyWorkflowIdGenerator", column:"id", unique:"true"
	}

	
	static mapWith = "none"
	
}
