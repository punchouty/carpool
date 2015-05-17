package com.racloop.persistence

import grails.transaction.Transactional

import com.racloop.domain.Journey
import com.racloop.util.date.DateUtil;

@Transactional
class WorkflowDataService {
	
	def amazonWebService;
	def searchService
	def journeyDataService;
	
	def processSearch(Date timeOfJourney, Date validStartTime, String mobile, Double fromLat, Double fromLon, Double toLat, Double toLon){
		List<Journey> journeys = journeyDataService.findMyJourneys(mobile, timeOfJourney);
		def myJourneys = [:]
		for (Journey dbJourney: journeys) {
			if(DateUtil.isDateInRange(timeOfJourney, dbJourney.getDateOfJourney(), 60)) {
				// Already journey exists
				return dbJourney
			}
		}
			
		List<Journey> searchResults = searchService.search(searchService.JOURNEY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
		if(searchResults.size() > 0) {
			return searchResults;
		}
		else {
			List<Journey> dummyResults = searchService.search(searchService.DUMMY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
			if(dummyResults.size() <= 0) {
				dummyResults = searchService.generateData(timeOfJourney, mobile, fromLat, fromLon, toLat, toLon);
			}
			return dummyResults;
		}
			
	}

    def serviceMethod() {

    }
}
