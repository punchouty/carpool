package com.racloop.persistence

import grails.transaction.Transactional
import grails.util.Environment

import org.elasticsearch.common.joda.time.DateTime

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import com.racloop.domain.Journey

@Transactional
class JourneyDataService {
	
	def grailsApplication
	def amazonWebService
	def searchService
	def jmsService

    def createJourney(Journey journey) {
		String journeyId = searchService.indexJourney(journey);
		journey.setEsJourneyId(journeyId);
		amazonWebService.dynamoDBMapper.save(journey);
		//jmsService.send(queue: Constant.HISTORY_QUEUE, command.id);
    }
	
	/**
	 * Find Journey from Dynamo DB
	 */
	def findJourney(String mobile, Date dateOfJourney) {
		Journey journeyKey = new Journey();
		journeyKey.setMobile(mobile);
		journeyKey.setDateOfJourney(dateOfJourney);
		Journey currentJourney = amazonWebService.dynamoDBMapper.load(journeyKey);
		return currentJourney;
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
	def findMyJourneys(String mobile, String currentTimeStr) {
		log.info("mobile : ${mobile}, currentTimeStr : ${currentTimeStr}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT.toString()).withAttributeValueList(new AttributeValue().withS(currentTimeStr.toString()));
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withRangeKeyCondition("DateOfJourney", rangeKeyCondition);
		List<Journey> journeys = amazonWebService.dynamoDBMapper.query(Journey.class, queryExpression);
		def returnJourneys = [];
		for (Journey dbJourney: journeys) {
			log.info("Journey found in my journey: ${dbJourney}");
			returnJourneys << dbJourney.convert();
		}
		return returnJourneys;
	}
	
	/**
	 * My History from Dynamo DB
	 */
	def findMyHistory(String mobile, String currentTimeStr) {
		log.info("mobile : ${mobile}, currentTimeStr : ${currentTimeStr}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.LT.toString()).withAttributeValueList(new AttributeValue().withS(currentTimeStr.toString()));
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withRangeKeyCondition("DateOfJourney", rangeKeyCondition);
		List<Journey> journeys = amazonWebService.dynamoDBMapper.query(Journey.class, queryExpression);
		def returnJourneys = [];
		for (Journey dbJourney: journeys) {
			log.info("Journey found in my journey: ${dbJourney}");
			returnJourneys << dbJourney.convert();
		}
		return returnJourneys;
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
			log.info("Starting deleting all journey for user with mobile : ${mobile}");
			Journey journey = new Journey();
			journey.mobile = mobile
			DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journey);
			List<Journey> journeys = amazonWebService.dynamoDBMapper.query(Journey.class, queryExpression);
			for (Journey dbJourney: journeys) {
				log.info("Journey found : ${dbJourney} Going to delete it");
				amazonWebService.dynamoDBMapper.delete(dbJourney);
			}
		}
		else {
			log.warn("Cannot delete journey in non development enviornment");
		}
	}
}
