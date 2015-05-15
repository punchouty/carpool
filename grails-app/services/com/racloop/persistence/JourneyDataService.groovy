package com.racloop.persistence

import liquibase.util.csv.opencsv.CSVReader;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.racloop.Constant;
import com.racloop.User;
import com.racloop.domain.Journey;
import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach;

import grails.transaction.Transactional
import grails.util.Environment;

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
