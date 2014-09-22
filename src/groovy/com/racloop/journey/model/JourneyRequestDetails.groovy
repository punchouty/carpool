package com.racloop.journey.model

import java.util.Date

import com.racloop.JourneyRequestCommand
import com.racloop.workflow.JourneyWorkflow

class JourneyRequestDetails {
	
	String user
	String journeyId
	String name 
	Boolean isMale
	Date dateOfJourney
	String fromPlace
	String toPlace
	Boolean isDriver
	String photoUrl
	List outgoingRequests
	List incomingRequests
	int numberOfIncomingRequests = 0
	int numberOfOutgoingRequests = 0
	
	public JourneyRequestDetails() {
		this.incomingRequests= []
		this.outgoingRequests =[]
	}
	
	public void setJourneyDetails (JourneyRequestCommand journey) {
		this.journeyId = journey.id
		this.user = journey.user
		this.name = journey.name
		this.isMale = journey.isMale
		this.isDriver = journey.isDriver
		this.fromPlace = journey.fromPlace
		this.toPlace = journey.toPlace
		this.photoUrl = journey.photoUrl
		this.dateOfJourney = journey.dateOfJourney
		
	}

}
