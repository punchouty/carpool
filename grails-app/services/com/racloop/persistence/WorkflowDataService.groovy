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
			
	}

    def serviceMethod() {

    }
}
