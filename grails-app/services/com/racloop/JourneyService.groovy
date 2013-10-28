package com.racloop

import org.elasticsearch.common.joda.time.DateTime

class JourneyService {

    def elasticSearchService
	
	def saveJourney(User user, Journey journey) {
		journey.user = user
		user.addToActiveJourneys(journey);
		if(journey.validate()) {
			DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
			DateTime validStartTime = new DateTime(journey.validStartTime)
			journey.save(flush : true);
			//elasticSearchService.indexJourneyWithIndexCheck(journey);
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
	
	def makeSearchable(Journey journey) {
		elasticSearchService.indexJourney(journey);
	}
}
