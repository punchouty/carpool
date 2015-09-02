package com.racloop.persistence

import grails.transaction.Transactional

import org.elasticsearch.common.joda.time.DateTime

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.racloop.Constant
import com.racloop.domain.Journey
import com.racloop.domain.JourneyAutoMatch
import com.racloop.mobile.data.response.MobileResponse

@Transactional
class AutoMatcherService {
	
	def awsService
	def journeyDataService
	def journeySearchService
	def jmsService
	def journeyPairDataService

    private Map autoMatch() {
		
		Map autoMatchResult = [:]
		DateTime dateTime = new DateTime()
		DateTime futureDateTime = new DateTime().plusHours(18) //Read this from config
		Set journeysToBeMatched= getListOfJourneysToBeAutoMatched(dateTime, futureDateTime)
		for (Journey inputJourney : journeysToBeMatched) {
			MobileResponse mobileResponse = journeySearchService.straightThruSearch(inputJourney.convert(), false)
			List matchedResult = mobileResponse.data.get("journeys")
			List newMatches = removeAlreadyRequestJourenys(matchedResult, inputJourney)
			JourneyAutoMatch jourenyAutoMatch = findAutoMatchJourneyByJourneyId(inputJourney.getId())
			Map resultMap = seggergateNewMatches(jourenyAutoMatch, newMatches)
			autoMatchResult.put(inputJourney.getId(), resultMap)
			if(resultMap.get('newJourneys')) {
				saveAutoMatchResult(jourenyAutoMatch, inputJourney.getId(), resultMap.get('newJourneys'))
			}
		}
		return autoMatchResult
    }
	
	def sendAutoMatchNotificaiton() {
		Map autoMatchResult = this.autoMatch()
		autoMatchResult.each{ journeyId, autoMatchResultMap -> 
			Set newMatches = autoMatchResultMap.get("newJourneys")
			if(newMatches) {
				//Send SMS
				Journey journey = journeyDataService.findJourney(journeyId)
				def  messageMap =[
					mobile: journey.getMobile(),
					journeyDate: journey.dateOfJourney.format('dd MMM yy HH:mm'),
					to:journey.getTo(),
					exsitingMatches : autoMatchResultMap.get("existingJourneys")?.size(),
					newMatches : autoMatchResultMap.get("newJourneys")?.size()
					]
				jmsService.send(queue: Constant.NOTIFICATION_AUTOMATCH_MESSAGE_QUEUE, messageMap)
			}
		}
	}
	
	private JourneyAutoMatch findAutoMatchJourneyByJourneyId(String journeyId) {
		JourneyAutoMatch autoMatch = awsService.dynamoDBMapper.load(JourneyAutoMatch.class, journeyId);
	}
	
	private void updateAutoMatchJourney(JourneyAutoMatch journeyAutoMatch) {
		awsService.dynamoDBMapper.save(journeyAutoMatch, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE));
	}
	
	private void createAutoMatchJourney(JourneyAutoMatch journeyAutoMatch) {
		awsService.dynamoDBMapper.save(journeyAutoMatch);
	}
	
	private List removeAlreadyRequestJourenys(List inputJourneyList, Journey primaryJourney) {
		primaryJourney = journeyDataService.findChildJourneys(primaryJourney.getId())
		List newMatches = []
		inputJourneyList.each {it ->
			if(!(primaryJourney.getRelatedJourneys()?.contains(it))) {
				newMatches.add(it)
			}
		}
		return newMatches
	}
	
	private Map seggergateNewMatches(JourneyAutoMatch autoMatch, List matchedJourneys){
		Map resultMap = [:]
		Set<String> previouslyMatchedIds = autoMatch?.getAutoMatchedJourneyIds()
		Set<String> oldMatches = [] as Set
		Set<String> newMatches = [] as Set
		for(Journey journey : matchedJourneys){
			if(previouslyMatchedIds?.contains(journey.getId())) {
				oldMatches << journey.getId()
			}
			else {
				newMatches << journey.getId()
			}
		}
		resultMap.put('newJourneys', newMatches)
		resultMap.put('existingJourneys', oldMatches)
		return resultMap
	}
	
	private void saveAutoMatchResult(JourneyAutoMatch autoMatch,String journeyId, Set matchedJourenyId) {
		if(autoMatch) {
			autoMatch.addAutoMatchedJourneyIds(matchedJourenyId )
			this.updateAutoMatchJourney(autoMatch)
		}
		else {
			JourneyAutoMatch journeyAutoMatch = new JourneyAutoMatch()
			journeyAutoMatch.setPrimaryJourneyId(journeyId)
			journeyAutoMatch.setAutoMatchedJourneyIds(matchedJourenyId)
			this.createAutoMatchJourney(journeyAutoMatch)
		}
	}
	
	private Set getListOfJourneysToBeAutoMatched (DateTime startDateTime, DateTime futureDateTime) {
		
		List journeysFromES = journeySearchService.findAllJourneysBetweenDates(startDateTime, futureDateTime)
		def returnList = [] as Set
		for(Journey journey : journeysFromES){
			Journey journeyFromDb = journeyDataService.findJourney(journey.getId())
			if(journeyFromDb) {
				returnList << journeyFromDb
				for(String pairedJourneyId :journeyFromDb.getJourneyPairIds()) {
					Journey journeyFromPair = journeyDataService.findJourney(pairedJourneyId)
					if(journeyFromPair) {
						returnList << journeyFromPair
					}
				}
			}
		}
		return returnList
	}
	
}
