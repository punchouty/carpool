package com.racloop.journey.model

import com.racloop.JourneyRequestCommand
import com.racloop.workflow.JourneyWorkflow

class JourneyRequestDetails {
	
	JourneyRequestCommand journey
	List outgoingRequests
	List incomingRequests
	int numberOfIncomingRequests = 0
	int numberOfOutgoingRequests = 0
	
	public JourneyRequestDetails() {
		this.incomingRequests= []
		this.outgoingRequests =[]
	}

}
