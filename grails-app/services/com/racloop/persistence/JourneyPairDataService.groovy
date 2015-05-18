package com.racloop.persistence

import grails.transaction.Transactional

import com.racloop.domain.JourneyPair


@Transactional
class JourneyPairDataService {

	def amazonWebService

	def createJourneyPair(JourneyPair journeyPair){
		JourneyPair savedPair = amazonWebService.dynamoDBMapper.save(journeyPair);
		log.info("createJourney - ${savedPair}");
	}
	
	def findPairById(String id) {
		log.info("findPairt - id : ${id}");
		JourneyPair journeyPair = amazonWebService.dynamoDBMapper.load(JourneyPair.class, id);
		//amazonWebService.dynamoDBMapper.b
		return journeyPair;
	}
	
	def List findPairsByIds(Set ids){
		def journeyPairs = []
		ids.each{it ->
			journeyPairs << findPairById(id)
			
		}
		return journeyPairs
	}
}