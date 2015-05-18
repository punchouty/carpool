package com.racloop.domain;

import java.util.Map;
import java.util.Set;

public class UserJourney {
	
	private final Journey userJourney;
	private final Set<JourneyPair> journeyPairs;
	private final Map<String, Journey> allRelatedJourneysAsMap;
	private final Set<String> ids;
	
	public UserJourney(Journey userJourney, Set<JourneyPair> journeyPairs,
			Map<String, Journey> allRelatedJourneysAsMap, Set<String> ids) {
		this.userJourney = userJourney;
		this.journeyPairs = journeyPairs;
		this.allRelatedJourneysAsMap = allRelatedJourneysAsMap;
		this.ids = ids;
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

	public Set<String> getIds() {
		return ids;
	}

}
