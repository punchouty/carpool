package com.racloop.persistence

import grails.transaction.Transactional

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.racloop.domain.JourneyPair


@Transactional
class JourneyPairDataService {

	def awsService

	def createJourneyPair(JourneyPair journeyPair){
		JourneyPair savedPair = awsService.dynamoDBMapper.save(journeyPair);
		log.info("createJourney - ${savedPair}");
	}
	
	def saveJourneyPair(JourneyPair journeyPair) {
		awsService.dynamoDBMapper.save(journeyPair, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE));
	}
	
	def findPairById(String id) {
		log.info("findPairt - id : ${id}");
		JourneyPair journeyPair = awsService.dynamoDBMapper.load(JourneyPair.class, id);
		return journeyPair;
	}
	
	def List findPairsByIds(Set ids){
		def journeyPairs = []
		ids.each{it ->
			journeyPairs << findPairById(it)
			
		}
		return journeyPairs
	}
}
