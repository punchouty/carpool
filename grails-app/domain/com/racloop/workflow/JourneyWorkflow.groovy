package com.racloop.workflow

import com.racloop.journey.workkflow.WorkflowState;


class JourneyWorkflow {
	
	UUID id
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

    static constraints = {
		
		state inList : WorkflowState.values()*.state
    }
	
	static mapping = {
		id generator:'assigned' //disable primary key generation
	}

	
	static mapWith = "none"
	
}
