package com.racloop

import org.elasticsearch.common.joda.time.DateTime

class JourneyService {

    def elasticSearchService
	def jmsService
	
	def saveJourney(Journey journey) {
		if(journey.validate()) {
			if(journey.save(flush: true)) {
				return true;
			}
			else {
				return false;
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
	
	/**
	 * @deprecated
	 */
	def search(User user, JourneyRequestCommand journey) {
		def journeys = elasticSearchService.search(user, journey);
		return journeys;
	}
	
	/**
	 * @deprecated
	 */
	def makeSearchable(Journey journey) {
		elasticSearchService.indexJourney(journey);
		jmsService.send(queue:'msg.history', journey)
	}
	
	/**
	 * @deprecated
	 */
	private Journey createJourneyInstance(JourneyRequestCommand command) {
		Journey journey = new Journey()
		journey.name = command.name
		journey.dateOfJourney = command.dateOfJourney
		journey.validStartTime = command.validStartTime
		journey.fromPlace = command.fromPlace
		journey.fromLatitude = command.fromLatitude
		journey.fromLongitude = command.fromLongitude
		journey.toPlace = command.toPlace
		journey.toLatitude = command.toLatitude
		journey.toLongitude = command.toLongitude
		journey.isDriver = command.isDriver
		journey.tripDistance = command.tripDistance
		journey.tripUnit = command.tripUnit
		journey.ip = command.ip
		return journey;
	}
}
