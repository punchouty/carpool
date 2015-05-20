package com.racloop.persistence

import grails.transaction.Transactional
import grails.util.Environment

import org.elasticsearch.common.joda.time.DateTime

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import com.racloop.GenericUtil;
import com.racloop.domain.Journey
import com.racloop.domain.JourneyPair;
import com.racloop.domain.UserJourney;

@Transactional
class JourneyDataService {
	
	def grailsApplication
	def amazonWebService
	def searchService
	def jmsService
	def journeyPairDataService

    def createJourney(Journey journey) {
		amazonWebService.dynamoDBMapper.save(journey);
		log.info("createJourney - ${journey}");
		searchService.indexJourney(journey, journey.getId());
    }
	
	def allJourneyData(String id) {
		Journey currentJourney = amazonWebService.dynamoDBMapper.load(Journey.class, id);
		return allJourneyData(currentJourney)
	}
	
	def allJourneyData(Journey currentJourney){
		Set<JourneyPair> journeyPairs = new HashSet<JourneyPair>();
		HashMap<String, Journey> allRelatedJourneysAsMap = new HashMap<String, Journey>()
		Set<String> journeyPairIds = currentJourney.getJourneyPairIds();
		Set<String> ids = new HashSet<String>()
		journeyPairIds.each { pairId ->
			JourneyPair journeyPair = journeyPairDataService.findPairById(pairId);
			String journeyId = journeyPair.initiatorJourneyId;
			ids << journeyId;
			Journey journey = allRelatedJourneysAsMap.get(journeyId);
			if(journey == null) {
				Journey journeyDb = findJourney(journeyId)
				allRelatedJourneysAsMap.put(journeyId, journeyDb);
			}
			journeyId = journeyPair.recieverJourneyId
			ids << journeyId;
			journey = allRelatedJourneysAsMap.get(journeyId);
			if(journey == null) {
				Journey journeyDb = findJourney(journeyId)
				allRelatedJourneysAsMap.put(journeyId, journeyDb);
			}
			journeyPairs << journeyPair
		}
		UserJourney userJourney = new UserJourney(currentJourney, journeyPairs, allRelatedJourneysAsMap, ids);
		return userJourney;
	}
	
	/**
	 * Find Journey from Dynamo DB
	 */
	def findJourney(String id) {
		Journey currentJourney = amazonWebService.dynamoDBMapper.load(Journey.class, id);
		return currentJourney;
	}
	
	/**
	 * Find Journey from Dynamo DB
	 */
	def findAllJourneyForUser(String mobile) {
		log.info("findAllJourneyForUser - mobile : ${mobile}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withIndexName("Mobile-DateOfJourney-index").withConsistentRead(false);
		List<Journey> journeys = amazonWebService.dynamoDBMapper.query(Journey.class, queryExpression);
		return journeys;
	}
	
	/**
	 * Find Journey from Dynamo DB
	 */
	def findJourney(String mobile, Date dateOfJourney) {
		String currentTimeStr = GenericUtil.javaDateToDynamoDbDateString(dateOfJourney);
		log.info("findJourney - mobile : ${mobile}, currentTimeStr : ${currentTimeStr}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		journeyKey.dateOfJourney = dateOfJourney;
		Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(currentTimeStr.toString()));
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withRangeKeyCondition("DateOfJourney", rangeKeyCondition);
		List<Journey> journeys = amazonWebService.dynamoDBMapper.query(Journey.class, queryExpression);
		return journeys;
	}
	
	/**
	 * Find Journey from Dynamo DB
	 */
	def saveJourney(Journey currentJourney) {
		amazonWebService.dynamoDBMapper.save(currentJourney, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE));
	}
	
	/**
	 * Current Journey is from Elastic Search
	 */
	def findCurrentJourney(String mobile, DateTime current) {
		DateTime startDate = null
		DateTime endDate = null
		Integer rangeFactor = Integer.valueOf(grailsApplication.config.grails.approx.time.range);
		startDate = current.minusMinutes(rangeFactor);
		endDate = current.plusMinutes(rangeFactor);
		def journeys = searchService.findAllJourneysForUserBetweenDates(mobile, startDate, endDate);
		def currentJourney = null
		if(journeys.size() > 0) currentJourney = journeys[0]
		return currentJourney;
	}
	
	/**
	 * My Journey from DynamoDb
	 */
	def findMyJourneys(String mobile, Date currentTime) {
		Map<Journey, List<Journey>> returnMap = new HashMap<Journey, List<Journey>>();
		String currentTimeStr = GenericUtil.javaDateToDynamoDbDateString(currentTime);
		log.info("mobile : ${mobile}, currentTimeStr : ${currentTimeStr}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT.toString()).withAttributeValueList(new AttributeValue().withS(currentTimeStr.toString()));
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withRangeKeyCondition("DateOfJourney", rangeKeyCondition).withIndexName("Mobile-DateOfJourney-index").withConsistentRead(false);
		List<Journey> journeys = amazonWebService.dynamoDBMapper.query(Journey.class, queryExpression);
		def returnJourneys = [];
		journeys.each { indexJourney ->
			Journey enrichedJourney = enrichJourney(indexJourney);
			if(enrichedJourney != null) {
				returnJourneys << enrichedJourney
			}
			else {
				log.error("Invalid Journey : ${indexJourney}")
			}
		}
		return returnJourneys;
	}
	
	/**
	 * My History from Dynamo DB
	 */
	def findMyHistory(String mobile, Date currentTime) {
		String currentTimeStr = GenericUtil.javaDateToDynamoDbDateString(currentTime);
		log.info("mobile : ${mobile}, currentTimeStr : ${currentTimeStr}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.LT.toString()).withAttributeValueList(new AttributeValue().withS(currentTimeStr.toString()));
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withRangeKeyCondition("DateOfJourney", rangeKeyCondition).withIndexName("Mobile-DateOfJourney-index").withConsistentRead(false);
		List<Journey> journeys = amazonWebService.dynamoDBMapper.query(Journey.class, queryExpression);
		def returnJourneys = [];
		journeys.each { indexJourney ->
			Journey enrichedJourney = enrichJourney(indexJourney);
			if(enrichedJourney != null) {
				returnJourneys << enrichedJourney
			}
			else {
				log.error("Invalid Journey : ${indexJourney}")
			}
		}
		return returnJourneys;
	}
	
	def enrichJourney(Journey journey) {
		List<String> pairIds = journey.journeyPairIds
		pairIds.each { id ->
			JourneyPair journeyPair = journeyPairDataService.findPairById(id);
			if(journeyPair != null) {
				journey.getJourneyPairs().add(journeyPair);
				Journey otherJourney = null;
				if(journey.id == journeyPair.initiatorJourneyId) {
					String otherJourneyId = journeyPair.recieverJourneyId;
					otherJourney = findJourney(otherJourneyId);
					if(otherJourney != null) {
						journeyPair.initiatorJourney = journey;
						journeyPair.recieverJourney = otherJourney;
						journey.setMyStatus(journeyPair.getInitiatorStatus());
						journey.setMyDirection(journeyPair.getInitiatorDirection());
						journey.getRelatedJourneys().add(otherJourney);
					}
					else {
						log.error("Invalid state for journey : ${journey}. \nOther Journey id not valid ${otherJourneyId}");
						return null;
					}
				}
				else if(journey.id == journeyPair.recieverJourneyId) {
					String otherJourneyId = journeyPair.initiatorJourneyId;
					otherJourney = findJourney(otherJourneyId);
					if(otherJourney != null) {
						journeyPair.initiatorJourney = otherJourney;
						journeyPair.recieverJourney = journey;
						journey.setMyStatus(journeyPair.getRecieverStatus());
						journey.setMyDirection(journeyPair.getRecieverDirection());
						journey.getRelatedJourneys().add(otherJourney);
					}
					else {
						log.error("Invalid state for journey : ${journey}. \nOther Journey id not valid ${otherJourneyId}");
						return null;
					}
				}
				else {
					log.error("Invalid state for journey : ${journey}. \nNone of the ids in pair are related to current journey");
					return null;
				}
			}
			else {
				log.error("Invalid state for journey : ${journey}. \n JourneyPair provided for id does not exists ${id}");
				return null;
			}
		}
		return journey;
	}
	
	/**
	 * Search from elastic search
	 */
	def search() {
		
	}
	
	/**
	 * Used in TestDataService only
	 */
	def deleteJourneyForUser(String mobile) {
		if (Environment.current == Environment.DEVELOPMENT) {
			log.info("deleteJourneyForUser Starting deleting all journey for user with mobile : ${mobile}");
			Journey journeyKey = new Journey();
			journeyKey.mobile = mobile
			DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withIndexName("Mobile-DateOfJourney-index").withConsistentRead(false);
			List<Journey> journeys = amazonWebService.dynamoDBMapper.query(Journey.class, queryExpression);
			def journeyPairs = []
			for (Journey dbJourney: journeys) {
				Set pairIds = dbJourney.getJourneyPairIds()
				pairIds.each {it ->
					def pair = journeyPairDataService.findPairById(it)
					journeyPairs << pair
				}
			}
			if(journeyPairs){
				amazonWebService.dynamoDBMapper.batchDelete(journeyPairs);
			}
			amazonWebService.dynamoDBMapper.batchDelete(journeys);
		}
		else {
			log.warn("Cannot delete journey in non development enviornment");
		}
	}
}
