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
	String from
	Double fromLatitude = -1;
	Double fromLongitude = -1;
	String to
	Double toLatitude = -1;
	Double toLongitude = -1;
	Boolean isDriver
	String photoUrl
	Double tripDistance = -1;
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
		this.from = journey.from
		this.fromLatitude = journey.fromLatitude
		this.fromLongitude = journey.fromLongitude
		this.to = journey.to
		this.toLatitude = journey.toLatitude
		this.toLongitude = journey.toLongitude
		this.photoUrl = journey.photoUrl
		this.dateOfJourney = journey.dateOfJourney
		this.tripDistance = journey.tripDistance
	}

}
