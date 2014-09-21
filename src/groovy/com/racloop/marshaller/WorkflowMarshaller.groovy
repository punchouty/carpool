package com.racloop.marshaller

import grails.converters.JSON

import com.racloop.workflow.JourneyWorkflow

class WorkflowMarshaller {
	
	void register() {
		JSON.registerObjectMarshaller(JourneyWorkflow) { JourneyWorkflow workflow ->
			return [
				id : workflow.id.toString(),
				requestJourneyId : workflow.requestJourneyId,
				requestedFromPlace : workflow.requestedFromPlace,
				requestedToPlace : workflow.requestedToPlace,
				requestUser : workflow.requestUser,
				requestedDateTime : workflow.requestedDateTime,
				state : workflow.state,
				matchingUser: workflow.matchingUser,
				matchedJourneyId : workflow.matchedJourneyId,
				matchedFromPlace : workflow.matchedFromPlace,
				matchedToPlace : workflow.matchedToPlace,
				matchedDateTime : workflow.matchedDateTime,
				isRequesterDriving : workflow.isRequesterDriving
				
			]
		}
		
	}

}
