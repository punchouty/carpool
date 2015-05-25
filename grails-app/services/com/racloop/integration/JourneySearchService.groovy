package com.racloop.integration

import org.elasticsearch.common.joda.time.DateTime;

import grails.transaction.Transactional

import com.racloop.GenericUtil
import com.racloop.JourneyRequestCommand
import com.racloop.domain.Journey
import com.racloop.domain.UserJourney
import com.racloop.elasticsearch.IndexMetadata
import com.racloop.mobile.data.response.MobileResponse

@Transactional
class JourneySearchService {

    def awsService;
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
		MobileResponse mobileResponse = new MobileResponse();
		if(mobile != null) {
			DateTime minimumValidSearchTime = new DateTime(timeOfJourney).minusHours(1);
			List<Journey> myJourneys = journeyDataService.findMyJourneys(mobile, minimumValidSearchTime.toDate());
			Journey existingJourney = getSimilarJourney(myJourneys, timeOfJourney);
			if(existingJourney != null) {
				mobileResponse.data = ['existingJourney': existingJourney.convert(), 'currentJourney': currentJourney]
				mobileResponse.success = true
				mobileResponse.message = "Existing journey found!"
				mobileResponse.existingJourney = true;
			}
			else {
				mobileResponse = straightThruSearch(currentJourney);
			}
		}
		else {
			mobileResponse = straightThruSearch(currentJourney);
		}
		return mobileResponse;
	}
	
	def straightThruSearch(JourneyRequestCommand currentJourney, boolean searchFromDummy = true) {
		Date timeOfJourney = currentJourney.dateOfJourney
		Date validStartTime = currentJourney.validStartTime
		String mobile = currentJourney.mobile
		Double fromLat = currentJourney.fromLatitude
		Double fromLon = currentJourney.fromLongitude
		Double toLat = currentJourney.toLatitude
		Double toLon = currentJourney.toLongitude
		MobileResponse mobileResponse = new MobileResponse();
		List<Journey> searchResults = searchService.search(IndexMetadata.JOURNEY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
		if(searchResults.size() > 0) {
			def returnJourneys = []
			for (Journey dbJourney: searchResults) {
				returnJourneys << dbJourney.convert();
			}
			int length = 0
			if(returnJourneys !=  null) length = returnJourneys.size()
			mobileResponse.data = ['journeys': returnJourneys]
			mobileResponse.success = true
			mobileResponse.total = length
			mobileResponse.message = "${length} results found"
		}
		else {
			if(searchFromDummy) {
				mobileResponse = getGeneratedData(timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
			}
			else {
				def emptyResults = []
				mobileResponse.data = ['journeys': emptyResults]
				mobileResponse.success = true
				mobileResponse.total = 0
				mobileResponse.message = "No matching results found"
			}
		}
		mobileResponse.currentJourney = currentJourney
		return mobileResponse;
	}
	
	private MobileResponse getGeneratedData(Date timeOfJourney, Date validStartTime, String mobile, Double fromLat, Double fromLon, Double toLat, Double toLon) {
		MobileResponse mobileResponse = new MobileResponse()
		List<Journey> dummyResults = searchService.search(IndexMetadata.DUMMY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
		if(dummyResults.size() <= 0) {
			dummyResults = searchService.generateData(timeOfJourney, mobile, fromLat, fromLon, toLat, toLon);
		}
		def returnJourneys = []
		for (Journey dbJourney: dummyResults) {
			returnJourneys << dbJourney.convert();
		}
		mobileResponse.data = ['journeys': returnJourneys]
		mobileResponse.success = true
		mobileResponse.total = returnJourneys?.size()
		mobileResponse.message = "${returnJourneys?.size()} results found"
		mobileResponse.isDummy = true;
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
		return returnJourneys;
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
