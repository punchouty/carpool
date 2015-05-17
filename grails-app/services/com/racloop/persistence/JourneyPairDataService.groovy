package com.racloop.persistence

import grails.transaction.Transactional

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.racloop.domain.JourneyPair


@Transactional
class JourneyPairDataService {

	def amazonWebService

	def createJourneyPair(JourneyPair journeyPair){
		JourneyPair savedPair = amazonWebService.dynamoDBMapper.save(journeyPair);
		log.info("createJourney - ${savedPair}");
	}
	
	def saveJourneyPair(JourneyPair journeyPair) {
		amazonWebService.dynamoDBMapper.save(journeyPair, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE));
	}
	
	def findPairById(String id) {
		log.info("findPairt - id : ${id}");
		JourneyPair journeyPair = amazonWebService.dynamoDBMapper.load(JourneyPair.class, id);
		//amazonWebService.dynamoDBMapper.b
		return currentJourney;
	}
	
	def List findPairsByIds(Set ids){
		def journeyPairs = []
		ids.each{it ->
			journeyPairs << findPairById(id)
			
		}
		return journeyPairs
	}
}
