package com.racloop.journey.model

import com.racloop.JourneyRequestCommand
import com.racloop.workflow.JourneyWorkflow

class JourneyRequestDetails {
	
	JourneyRequestCommand journey
	List requestedJourneys
	List matchedJourneys
	
	public JourneyRequestDetails() {
		this.matchedJourneys= []
		this.requestedJourneys =[]
	}

}
