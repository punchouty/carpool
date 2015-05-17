package com.racloop.domain;

import java.util.Map;
import java.util.Set;

public class UserJourney {
	
	private Journey userJourney;
	private Set<JourneyPair> journeyPairs;
	private Map<String, Journey> allRelatedJourneysAsMap;
	
	public UserJourney(Journey userJourney, Set<JourneyPair> journeyPairs,
			Map<String, Journey> allRelatedJourneysAsMap) {
		this.userJourney = userJourney;
		this.journeyPairs = journeyPairs;
		this.allRelatedJourneysAsMap = allRelatedJourneysAsMap;
	}
	
	public Journey getForId(String id) {
		return allRelatedJourneysAsMap.get(id);
	}

	public Journey getUserJourney() {
		return userJourney;
	}

	public Set<JourneyPair> getJourneyPairs() {
		return journeyPairs;
	}

	public Map<String, Journey> getAllRelatedJourneysAsMap() {
		return allRelatedJourneysAsMap;
	}

}
