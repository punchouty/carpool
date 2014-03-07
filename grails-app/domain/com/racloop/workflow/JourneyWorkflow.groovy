package com.racloop.workflow


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
		
		state inList : ['Initiated', 'Accepted', 'Rejected', 'Cancelled']
    }
	
	static mapping = {
		id generator:'assigned' //disable primary key generation
	}

	
	static mapWith = "none"
	
}
