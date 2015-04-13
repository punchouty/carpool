package com.racloop

import grails.util.Environment

import org.elasticsearch.common.joda.time.DateTime

import com.racloop.elasticsearch.WorkflowIndexFields
import com.racloop.journey.model.JourneyRequestDetails
import com.racloop.journey.model.MatchedJourneyResult
import com.racloop.journey.model.SearchResult

class JourneyService {

    def elasticSearchService
	def jmsService
	def journeyWorkflowService
	def grailsApplication
	
	// for dummy data
	def possibleMinutes = [0, 15, 30, 45]
	def random = new Random();
	
	def saveJourney(Journey journey) {
		if(journey.validate()) {
			if(journey.save(flush: true)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			journey.errors.each {
				println it
			}
		}
		return false
	}
	
	/**
	 * @deprecated
	 */
	def saveJourney(User user, Journey journey) {
		journey.user = user
		//user.addToActiveJourneys(journey);
		if(journey.validate()) {
			DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
			DateTime validStartTime = new DateTime(journey.validStartTime)
			journey.save();
			return true;
		}
		else {
			log.error "Erorr created journey - ${journey}"
			journey.errors.each {
				log.error "Error in domain : ${it}"
			}
			return false;
		}
    }
	
	/**
	 * @deprecated
	 */
	def createJourney(User user, JourneyRequestCommand command) {
		Journey journey = createJourneyInstance(command)
		journey.user = user
		user.addToActiveJourneys(journey);
		if(journey.validate()) {
			DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
			DateTime validStartTime = new DateTime(journey.validStartTime)
			journey.save(flush : true);
			return true;
		}
		else {
			log.error "Erorr created journey - ${journey}"
			journey.errors.each {
				log.error "Error in domain : ${it}"
			}
			return false;
		}
    }
	
	def search(User user, JourneyRequestCommand journey) {
		def journeys = elasticSearchService.search(user, journey);
		return journeys;
	}
	
	def searchPossibleExistingJourneyForUser(User user, JourneyRequestCommand journey) {
		def journeys = elasticSearchService.searchPossibleExistingJourneyForUser(user, journey);
		if(journeys) {
			return journeys.first()
		}
		else {
			return null
		}
	}
	
	def getDummyData(User user, JourneyRequestCommand command) {
		DateTime tempDate = new DateTime(command.dateOfJourney);
		if(Environment.current.getName() == "production") {
			if(tempDate.getHourOfDay() > 20 || tempDate.getHourOfDay() < 7) return [];
		}
		def journeys = elasticSearchService.searchGeneratedData(command);
		if(journeys.size == 0) {
			Random randomGenerator = new Random();
			Integer numberOfRecords = randomGenerator.nextInt(5);
			if(numberOfRecords < 2) numberOfRecords = 2;//expecting 2,3,4 number of records
			journeys = [];
			def names = [];
			if(command.isDriver) {
				if(user) {
					names = NamesUtil.getRandomNames(numberOfRecords);
				}
				else {//User is not authenticated
					names = NamesUtil.getRandomBoyNames(numberOfRecords);
				}
			}
			else {
				names = NamesUtil.getRandomBoyNames(numberOfRecords);
			}
			def maxDistance = command.tripDistance / (ElasticSearchService.DISTANCE_FACTOR + 1)
			
			Place [] fromPlaces = elasticSearchService.searchNearLocations(maxDistance, command.fromLatitude, command.fromLongitude, numberOfRecords)
			Place [] toPlaces = elasticSearchService.searchNearLocations(maxDistance, command.toLatitude, command.toLongitude, numberOfRecords)
			Random randomMinutesGenerator = new Random();
			int index = 0;
			if(fromPlaces.size() <= toPlaces.size()) {
				fromPlaces.each {
//					int fiveMinuteIntervalIndex = randomMinutesGenerator.nextInt(12);
//					int randomMinutes = 15;
//					if(fiveMinuteIntervalIndex > 3) randomMinutes = fiveMinuteIntervalIndex * 5;
//					tempDate = tempDate.plusMinutes(randomMinutes);
					def i = random.nextInt(possibleMinutes.size())
					tempDate = tempDate.plusMinutes(possibleMinutes[i]);
					def name = names[index]
					def fromPlace = it;
					def toPlace = toPlaces[index]
					JourneyRequestCommand journey = new JourneyRequestCommand()
					journey.name = name;
					journey.user= name;
					journey.isDriver = !command.isDriver;
					journey.dateOfJourney = tempDate.toDate()
					journey.fromLatitude = fromPlace.location.lat();
					journey.fromLongitude = fromPlace.location.lon();
					journey.fromPlace = fromPlace.name
					journey.toLatitude = toPlace.location.lat();
					journey.toLongitude = toPlace.location.lon();
					journey.toPlace = toPlace.name
					journey.tripDistance = command.tripDistance
					journey.id = elasticSearchService.indexGeneratedJourney(user, journey)
					journeys << journey
					index++
				}
			}
			else {
				toPlaces.each {
//					int fiveMinuteIntervalIndex = randomMinutesGenerator.nextInt(12);
//					int randomMinutes = 15;
//					if(fiveMinuteIntervalIndex > 3) randomMinutes = fiveMinuteIntervalIndex * 5;
//					tempDate = tempDate.plusMinutes(randomMinutes);
					def i = random.nextInt(possibleMinutes.size())
					tempDate = tempDate.plusMinutes(possibleMinutes[i]);
					def name = names[index]
					def toPlace = it;
					def fromPlace = fromPlaces[index]
					JourneyRequestCommand journey = new JourneyRequestCommand()
					journey.name = name;
					journey.user= name;
					journey.isDriver = command.isDriver;
					journey.dateOfJourney = tempDate.toDate()
					journey.fromLatitude = fromPlace.location.lat();
					journey.fromLongitude = fromPlace.location.lon();
					journey.fromPlace = fromPlace.name
					journey.toLatitude = toPlace.location.lat();
					journey.toLongitude = toPlace.location.lat();
					journey.toPlace = toPlace.name
					elasticSearchService.indexGeneratedJourney(user, journey);
					journeys << journey
					index++
				}				
			}
		}
		return journeys;
	}
	
	def JourneyRequestCommand findMatchedJourneyById (String matchedJourneyId,  boolean isDummy = false) {
		def journey = elasticSearchService.findJourneyById(matchedJourneyId, isDummy)
		return journey
	}
	
	def Map findCountOfAllWorkflowRequestForAJourney(String journeyId) {
		List outgoing = elasticSearchService.searchActiveWorkflowByMatchedJourneyId(WorkflowIndexFields.REQUEST_JOURNEY_ID, journeyId)
		List incoming = elasticSearchService.searchActiveWorkflowByMatchedJourneyId(WorkflowIndexFields.MATCHED_JOURNEY_ID, journeyId)
		return [outgoingCount : outgoing?.size(), incomingCount:incoming?.size(), totalCount: outgoing?.size()+ incoming?.size()]
		
	}
	
	def List findAllFutureJourneyForTheUser (User user, DateTime currentDate) {
		def journeys = elasticSearchService.findAllJourneysForUserAfterADate(user, currentDate)
		return journeys
	}
	
	def List findAllActiveJourneyDetailsForUser(User user) {
		DateTime currentDate = new DateTime()
		currentDate = currentDate.minusMinutes(Integer.valueOf(grailsApplication.config.grails.approx.time.to.match))
		def journeys = elasticSearchService.findAllJourneysForUserAfterADate(user, currentDate)
		return populateJourneysWithWorkflowDetails(journeys, user)
	}
	
	def List findCurrentJourneyForUser(User user, DateTime current) {
		DateTime startDate = null
		DateTime endDate = null
		Integer rangeFactor = Integer.valueOf(grailsApplication.config.grails.approx.time.range)
		startDate = current.minusMinutes(rangeFactor)
		endDate = current.plusMinutes(rangeFactor)
		def journeys = elasticSearchService.findAllJourneysForUserBetweenDates(user, startDate, endDate)
		return populateJourneysWithWorkflowDetails(journeys, user)
	}
	
	def List findHistoricJourneyDetailsForUser(User user) {
		DateTime currentDate = new DateTime()
		def journeys = elasticSearchService.findAllJourneysForUserBeforeADate(user, currentDate)
		return populateJourneysWithWorkflowDetails(journeys, user)
	}
	
	private List populateJourneysWithWorkflowDetails(List journeys, User user) {
		def journeyDetails =[]
		journeys.each {journey ->
			if(journey.id) {
				JourneyRequestDetails journeyRequestDetails = new JourneyRequestDetails()
				journeyRequestDetails.setJourneyDetails(journey)
				def requestWorkflowDetails = journeyWorkflowService.getWorkflowRequestedByUserForAJourney(journey.id, user)
				journeyRequestDetails.outgoingRequests.addAll(requestWorkflowDetails)
				def matchedWorkflowDetails = journeyWorkflowService.getWorkflowMatchedForUserForAJourney(journey.id, user)
				journeyRequestDetails.incomingRequests.addAll(matchedWorkflowDetails)
				journeyRequestDetails.numberOfIncomingRequests = journeyRequestDetails.incomingRequests.size()
				journeyRequestDetails.numberOfOutgoingRequests = journeyRequestDetails.outgoingRequests.size()
				journeyDetails<<journeyRequestDetails
			}
		}
		
		return journeyDetails
	}
	
	def findJourneyById(String journeyId) {
		return elasticSearchService.findJourneyById(journeyId)
	}
	
	def findJourneyById(String journeyId, boolean isDummy) {
		return elasticSearchService.findJourneyById(journeyId, isDummy)
	}
	
	public void markJourneyAsDeleted(String journeyId) {
		JourneyRequestCommand currentJourney = elasticSearchService.findJourneyById(journeyId)
		elasticSearchService.markJourneyAsDeleted(currentJourney)
	}
	
	public SearchResult getSearchResults(User currentUser, JourneyRequestCommand currentJourney) {
		currentJourney = getExisitngJourneyForUser(currentUser, currentJourney)
		def alreadySelectedJourneyMap = getAlreadySelectedJourneysForCurrentJourney(currentJourney)
		def matchedJourney = searchJourneys(currentUser, currentJourney)
		List<MatchedJourneyResult> matchedJourneyResult = enrichMatchedJourneyResult(matchedJourney.matchedJourneys, alreadySelectedJourneyMap) 
		SearchResult searchResult = new SearchResult(currentUser:currentUser, currentJourney:currentJourney,matchedJourneys:matchedJourneyResult, isDummyData:matchedJourney.isDummyData, numberOfRecords:matchedJourney.numberOfRecords)
		return searchResult
	}
	
	List<MatchedJourneyResult> enrichMatchedJourneyResult(matchedJourneyList, alreadySelectedJourneyMap) {
		List<MatchedJourneyResult> matchedJourneyResultList = []
		matchedJourneyList.each{it ->
			matchedJourneyResultList.add(new MatchedJourneyResult(matchedJourney:it, workflow:alreadySelectedJourneyMap?.get(it.id)))
		}
		return matchedJourneyResultList
		
	}
	
	private getExisitngJourneyForUser(User currentUser, JourneyRequestCommand currentJourneyFromWeb) {
		if(currentUser && currentJourneyFromWeb.isNewJourney()) {
			def possibleExisitingJourney = searchPossibleExistingJourneyForUser(currentUser, currentJourneyFromWeb)
			if(possibleExisitingJourney) {
				currentJourneyFromWeb = possibleExisitingJourney
			}
		}
		return currentJourneyFromWeb
	}
	
	private getAlreadySelectedJourneysForCurrentJourney(JourneyRequestCommand currentJourney) {
		def selectedJourneyMap = journeyWorkflowService.getAlreadySelectedJourneyMapForCurrentJourney(currentJourney)
		return selectedJourneyMap
	}
	
	private Map searchJourneys(User currentUser, JourneyRequestCommand currentJourney) {
		def matchedJourney =[:]
		boolean isDummyData =false
		def journeys = search(currentUser, currentJourney)
		int numberOfRecords = 0;
		if(journeys.size > 0) {
			numberOfRecords = journeys.size
		}
		else {
			journeys = getDummyData(currentUser, currentJourney)
			numberOfRecords = journeys.size
			isDummyData = true;
		}
		matchedJourney.matchedJourneys = journeys
		matchedJourney.numberOfRecords = numberOfRecords
		matchedJourney.isDummyData = isDummyData
		return matchedJourney
	}
	
}
