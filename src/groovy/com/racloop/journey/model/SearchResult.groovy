package com.racloop.journey.model

import com.racloop.JourneyRequestCommand
import com.racloop.User

class SearchResult {
	
	JourneyRequestCommand currentJourney
	List<MatchedJourneyResult> matchedJourneys
	boolean isDummyData = false
	int numberOfRecords = 0
	User currentUser
	
	public JourneyRequestDetails() {
		this.matchedJourneys= []
	}

}
