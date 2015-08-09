package com.racloop.persistence

import grails.transaction.Transactional

import org.elasticsearch.common.joda.time.DateTime

import com.amazonaws.Response;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.racloop.domain.Journey
import com.racloop.domain.JourneyAutoMatch
import com.racloop.domain.JourneyPair;
import com.racloop.mobile.data.response.MobileResponse;
import com.sun.org.apache.bcel.internal.generic.RETURN;

@Transactional
class AutoMatcherService {
	
	def awsService
	def journeyDataService
	def journeySearchService

    private Map autoMatch() {
		
		Map autoMatchResult = [:]
		DateTime dateTime = new DateTime()
		DateTime futureDateTime = new DateTime().plusHours(12) //Read this from config
		List journeysToBeMatched= journeyDataService.findMyJourneysForAutoMatch(dateTime.toDate(), futureDateTime.toDate())
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
		List newMatches = []
		inputJourneyList.each {it ->
			if(!(primaryJourney.getJourneyPairIds()?.contains(it.id))) {
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
	
}
