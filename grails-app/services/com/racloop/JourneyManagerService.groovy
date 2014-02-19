package com.racloop

import org.elasticsearch.common.geo.GeoPoint

class JourneyManagerService {

	def elasticSearchService
	def journeyService
	def jmsService

	def createJourney(User user, JourneyRequestCommand command) {
		Journey journey = new Journey()
		journey.user = user
		journey.dateOfJourney = command.dateOfJourney
		journey.isDriver = command.isDriver
		if(journeyService.saveJourney(journey)) {
			log.info("Journey created with id ${journey.id}")
			command.id = journey.id;
			elasticSearchService.indexJourney(user, command);
			jmsService.send(queue: Constant.HISTORY_QUEUE, command.id);
			return true;
		}
		else {
			log.warn "Unable to create journey ${journey}"
			return false;
		}
	}
	def createJourney(JourneyRequestCommand command) {
		User user  = User.findByUsername(command.name);
		createJourney(user, command)
	}
}
