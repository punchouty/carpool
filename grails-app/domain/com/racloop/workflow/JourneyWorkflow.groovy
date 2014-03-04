package com.racloop.workflow


class JourneyWorkflow {
	
	UUID id
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
	
	static mapping = {
		id generator:'assigned' //disable primary key generation
	}

	
	static mapWith = "none"
	
}
