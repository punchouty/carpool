package com.racloop.integration

import grails.transaction.Transactional

import com.racloop.JourneyRequestCommand;
import com.racloop.domain.Journey
import com.racloop.mobile.data.response.MobileResponse;
import com.racloop.util.date.DateUtil

@Transactional
class JourneySearchService {

    def amazonWebService;
	def searchService
	def journeyDataService;
	
	def processSearch(JourneyRequestCommand currentJourney){
		Date timeOfJourney = currentJourney.dateOfJourney
		Date validStartTime = currentJourney.validStartTime
		String mobile = currentJourney.mobile
		Double fromLat = currentJourney.fromLatitude
		Double fromLon = currentJourney.fromLongitude
		Double toLat = currentJourney.toLatitude
		Double toLon = currentJourney.toLongitude
		MobileResponse mobileResponse = new MobileResponse()
		List<Journey> journeys = journeyDataService.findMyJourneys(mobile, timeOfJourney);
		Journey existingJourney = null;
		existingJourney = getSimilarJourney(journeys, timeOfJourney)
		if(existingJourney != null) {
			mobileResponse.data = ['existingJourney':existingJourney.convert(),'currentJourney':currentJourney]
			mobileResponse.success = true
			mobileResponse.message = "Existing journey found!"
			mobileResponse.existingJourney = true
		}
		else {
			List<Journey> searchResults = searchService.search(searchService.JOURNEY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
			if(searchResults.size() > 0) {
				def returnJourneys = []
				for (Journey dbJourney: journeys) {
					returnJourneys << dbJourney.convert();
				}
				mobileResponse.data = returnJourneys
				mobileResponse.success = true
				mobileResponse.total = returnJourneys.size()
				mobileResponse.message = "${returnJourneys.size()} results found"
			}
			else {
				List<Journey> dummyResults = searchService.search(searchService.DUMMY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
				if(dummyResults.size() <= 0) {
					dummyResults = searchService.generateData(timeOfJourney, mobile, fromLat, fromLon, toLat, toLon);
				}
				def returnJourneys = []
				for (Journey dbJourney: dummyResults) {
					returnJourneys << dbJourney.convert();
				}
				mobileResponse.data = returnJourneys
				mobileResponse.success = true
				mobileResponse.total = returnJourneys.size()
				mobileResponse.message = "${returnJourneys.size()} results found"
				mobileResponse.isDummy = true;
			}
		}	
		return mobileResponse;
	}

	private Journey getSimilarJourney(List<Journey> journeys, Date timeOfJourney) {
		Journey existingJourney = null
		for (Journey dbJourney: journeys) {
			if(DateUtil.isDateInRange(timeOfJourney, dbJourney.getDateOfJourney(), 60)) {
				existingJourney = dbJourney
				break;
			}
		}
		return existingJourney
	}
}
