package com.racloop.workflow


class JourneyWorkflow {
	
	String requestJourneyId
	String requestedFromPlace
	String requestedToPlace
	String requestUser
	String requestedDateTime
	String state
	String matchingUser
	String matchedJourneyId
	String matchedFromPlace
	String matchedToPlace
	String matchedDateTime
	boolean isRequesterDriving

    static constraints = {
		
		state inList : ['Initiated', 'Accepted', 'Rejected', 'Cancelled']
    }
	
	static mapWith = "none"
	
}
