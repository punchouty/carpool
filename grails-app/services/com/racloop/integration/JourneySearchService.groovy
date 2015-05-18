package com.racloop.integration

import grails.transaction.Transactional

import com.racloop.GenericUtil;
import com.racloop.JourneyRequestCommand;
import com.racloop.domain.Journey
import com.racloop.domain.UserJourney;
import com.racloop.elasticsearch.IndexMetadata;
import com.racloop.mobile.data.response.MobileResponse;
import com.racloop.util.date.DateUtil

@Transactional
class JourneySearchService {

    def amazonWebService;
	def searchService
	def journeyDataService;
	
	def executeSearch(JourneyRequestCommand currentJourney){
		Date timeOfJourney = currentJourney.dateOfJourney
		Date validStartTime = currentJourney.validStartTime
		String mobile = currentJourney.mobile
		Double fromLat = currentJourney.fromLatitude
		Double fromLon = currentJourney.fromLongitude
		Double toLat = currentJourney.toLatitude
		Double toLon = currentJourney.toLongitude
		MobileResponse mobileResponse = new MobileResponse()
		List<Journey> journeys = journeyDataService.findMyJourneys(mobile, timeOfJourney);
		Journey existingJourney = getSimilarJourney(journeys, timeOfJourney)
		if(existingJourney != null) {
			mobileResponse.data = ['existingJourney':existingJourney.convert(),'currentJourney':currentJourney]
			mobileResponse.success = true
			mobileResponse.message = "Existing journey found!"
			mobileResponse.existingJourney = true
		}
		else {
			List<Journey> searchResults = searchService.search(IndexMetadata.JOURNEY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
			if(searchResults.size() > 0) {
				def returnJourneys = enrichResults(searchResults, journeys);
				mobileResponse.data = returnJourneys
				mobileResponse.success = true
				mobileResponse.total = returnJourneys.size()
				mobileResponse.message = "${returnJourneys.size()} results found"
			}
			else {
				List<Journey> dummyResults = searchService.search(IndexMetadata.DUMMY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
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
	
	private enrichResults(List<Journey> searchResults, List<Journey> myJourneys) {
		Set<String> ids = new HashSet<String>();
		HashMap<String, UserJourney> allMyJourneysAsMap = new HashMap<String, UserJourney>()
		for (Journey dbJourney: myJourneys) {
			UserJourney userJourney = journeyDataService.allJourneyData(dbJourney);
			ids.addAll(userJourney.getIds());
			allMyJourneysAsMap.put(dbJourney.id, userJourney);
		}
		def returnJourneys = []
		for (Journey result: searchResults) {
			if(ids.contains(result.id)) {
//				List<UserJourney> userJourneys = allMyJourneysAsMap.values();
//				for (UserJourney userJourney: userJourneys) {
//					Journey matchedJourney = userJourney.getAllRelatedJourneysAsMap().get(result.id);
//				}
			}
			else {
				returnJourneys << result.convert();
			}
		}
	}

	private Journey getSimilarJourney(List<Journey> journeys, Date timeOfJourney) {
		Journey existingJourney = null
		for (Journey dbJourney: journeys) {
			if(GenericUtil.isDateInRange(timeOfJourney, dbJourney.getDateOfJourney(), 60)) {
				existingJourney = dbJourney
				break;
			}
		}
		return existingJourney
	}
}