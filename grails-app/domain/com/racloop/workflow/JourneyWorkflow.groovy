package com.racloop.workflow

class JourneyWorkflow {
	
	String requestUser
	String matchingUser
	String state
	String requestJourneyId
	String matchedJourneyId

    static constraints = {
		
		state inList : ['Initiated', 'Accepted', 'Rejected', 'Cancelled']
    }
}
