package com.racloop.integration

import java.util.Date;

import grails.transaction.Transactional

import org.elasticsearch.common.geo.GeoHashUtils
import org.elasticsearch.common.joda.time.DateTime

import com.racloop.GenericUtil
import com.racloop.JourneyRequestCommand
import com.racloop.domain.Journey
import com.racloop.domain.JourneyPair;
import com.racloop.domain.UserJourney
import com.racloop.elasticsearch.IndexMetadata
import com.racloop.journey.workkflow.WorkflowStatus
import com.racloop.mobile.data.response.MobileResponse

@Transactional
class JourneySearchService {

    def awsService;
	def searchService
	def journeyDataService
	def journeyPairDataService
	def grailsApplication
	
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
				if(areJourneysSame(currentJourney, existingJourney)) {
					log.info("The journey serached is SAME as the one present in db. Search parameters are about same.");
					mobileResponse = straightThruSearch(existingJourney.convert(), true);
					mobileResponse.data['hideSaveButton'] = true;
					if(existingJourney.numberOfCopassengers >= 2) {
						mobileResponse.data['disableMoreRequests'] = true;
					}
				}
				else {
					log.info("The journey serached is SIMILAR as the one present in db. Search parameters are not same.");
					mobileResponse.data = ['existingJourney': existingJourney.convert(), 'currentJourney': currentJourney]
					mobileResponse.success = true
					mobileResponse.message = "Existing journey found!"
					mobileResponse.existingJourney = true;
				}
			}
			else {
				mobileResponse = straightThruSearch(currentJourney);
				mobileResponse.data['hideSaveButton'] = false;
			}
		}
		else {
			log.info("Search coming with user");
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
		Integer noOfExistingPassanger = 0
		boolean disableMoreRequests = false
		MobileResponse mobileResponse = new MobileResponse();
		List<Journey> searchResults = null;
		if(currentJourney.isMale) {
			searchResults = searchService.searchRidesForMales(IndexMetadata.JOURNEY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
		}
		else {
			if(currentJourney.femaleOnlySearch) {
				searchResults = searchService.searchFemaleOnlyRides(IndexMetadata.JOURNEY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
			}
			else {
				searchResults = searchService.search(IndexMetadata.JOURNEY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
			}
		}
		
		if(currentJourney.id) {
			disableMoreRequests = shoudlDisableMoreRequest(currentJourney.id)
			searchResults = enrichResult(searchResults,currentJourney.id )
		}
		if(searchResults.size() > 0) {
			int length = 0
			length = searchResults.size()
			mobileResponse.data = ['journeys': searchResults, 'disableMoreRequests':disableMoreRequests]
			mobileResponse.success = true
			mobileResponse.total = length
			mobileResponse.message = "${length} results found"
		}
		else {
			if(searchFromDummy && noOfExistingPassanger<1 && currentJourney.tripDistance<=40 && currentJourney.isMale) {// show dummy only in case of male
				mobileResponse = getGeneratedData(currentJourney.id, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
			}
			else {
				def emptyResults = []
				mobileResponse.data = ['journeys': emptyResults, 'disableMoreRequests':disableMoreRequests]
				mobileResponse.success = true
				mobileResponse.total = 0
				mobileResponse.message = "No matching results found"
			}
		}
		mobileResponse.currentJourney = currentJourney
		return mobileResponse;
	}
	
	private MobileResponse getGeneratedData(String currentJourneyId, Date timeOfJourney, Date validStartTime, String mobile, Double fromLat, Double fromLon, Double toLat, Double toLon) {
		MobileResponse mobileResponse = new MobileResponse()
		List<Journey> dummyResults = searchService.search(IndexMetadata.DUMMY_INDEX_NAME, timeOfJourney, validStartTime, mobile, fromLat, fromLon, toLat, toLon);
		if(dummyResults.size() <= 0) {
			dummyResults = searchService.generateData(timeOfJourney, mobile, fromLat, fromLon, toLat, toLon);
		}
		else {
			if(currentJourneyId) {
				dummyResults = enrichResult(dummyResults, currentJourneyId)
			}
		}
		def returnJourneys = []
		for (Journey dbJourney: dummyResults) {
			returnJourneys << dbJourney//dbJourney.convert();
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
			if(GenericUtil.isDateInRange(timeOfJourney, dbJourney.getDateOfJourney(), 60) && !WorkflowStatus.CANCELLED.getStatus().equals(dbJourney.getStatusAsParent())) {
				existingJourney = dbJourney
				break;
			}
		}
		return existingJourney
	}
	
	
	
	private boolean areJourneysSame(JourneyRequestCommand currentJourney, Journey dbJourney) {
		if(currentJourney.isTaxi == dbJourney.isTaxi && currentJourney.femaleOnlySearch == dbJourney.femaleOnlySearch) {
			String currentJourneyFromGeoHash = GeoHashUtils.encode(currentJourney.fromLatitude, currentJourney.fromLongitude, 6);
			String currentJourneyToGeoHash = GeoHashUtils.encode(currentJourney.toLatitude, currentJourney.toLongitude, 6);
			String fromGeohash = GeoHashUtils.encode(dbJourney.fromLatitude, dbJourney.fromLongitude, 6);
			String toGeohash = GeoHashUtils.encode(dbJourney.toLatitude, dbJourney.toLongitude, 6);
			if(fromGeohash.equals(currentJourneyFromGeoHash) && toGeohash.equals(currentJourneyToGeoHash)){//from and to are almost identical with in precision of 6
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false
		}
	}
	
	
	private List enrichResult1(List searchResult, String currentJourneyId){
		def returnSet = [] as Set	//Result coming from 2 different source, hence can be duplicate. Relying on equals of Journey object
		def excludedSet = [] as Set
		Journey currentJourney = journeyDataService.findChildJourneys(currentJourneyId)
		if(currentJourney.getRelatedJourneys()){
			currentJourney.getRelatedJourneys().each {it->
				excludedSet << it
			}
		}
		searchResult.each {it -> 
			if(!excludedSet.contains(it)){
				returnSet << it 
			}
		}
		return new ArrayList(returnSet)
	}
	
	private List enrichResult(List searchResult, String currentJourneyId){
		def returnSet = [] as Set	//Result coming from 2 different source, hence can be duplicate. Relying on equals of Journey object
		Set childJourneysId = [] as Set
		Journey acceptedJourney = journeyDataService.findAcceptedRequestForAJourney(currentJourneyId)
		Journey currentJourney = journeyDataService.findChildJourneys(currentJourneyId)
		if(currentJourney.getRelatedJourneys()){
			currentJourney.getRelatedJourneys().each {it->
				childJourneysId << it.getId()
				if(acceptedJourney) {
					if(acceptedJourney.getId().equals(it.getId())) {
						returnSet << it
					}
				}
				else {
					if(searchResult.contains(it)) {
						returnSet << it
					}
				} 
			}
		}
		searchResult.each {it ->
			if(!childJourneysId.contains(it.getId())) {
				returnSet << it
			}
			
		}
		return new ArrayList(returnSet)
	}
	
	def findAllJourneysBetweenDates(DateTime startDate, DateTime endDate){
		return searchService.findAllJourneysBetweenDates(startDate, endDate)
	}
	
	def nearByPoints(String mobile, Date timeOfJourney, Double fromLat, Double fromLon) {
		return searchService.searchNearByPoints(mobile, timeOfJourney, fromLat, fromLon)
	}
	
	private boolean shoudlDisableMoreRequest (String currentJourneyId) {
		Integer maxAllowedRequests = Integer.valueOf(grailsApplication.config.grails.max.active.requests)
		boolean shoudlDisableMoreRequest = false
		int countOfRequestMade = 0
		Journey currentJourney = journeyDataService.findChildJourneys(currentJourneyId)
		int noOfExistingPassanger = currentJourney.getNumberOfCopassengers()
		if(noOfExistingPassanger>=2){
			return true
		}
		for (Journey journey : currentJourney.getRelatedJourneys()){
			if(journey?.getMyStatus()?.equals(WorkflowStatus.ACCEPTED.getStatus())) {
				shoudlDisableMoreRequest = true
				break
			}
			else if(journey?.getMyStatus()?.equals(WorkflowStatus.REQUESTED.getStatus())) {
				countOfRequestMade++
				if(countOfRequestMade >= maxAllowedRequests) {
					shoudlDisableMoreRequest = true
					break
				}
			}
		}
		return shoudlDisableMoreRequest
	}
	
}
