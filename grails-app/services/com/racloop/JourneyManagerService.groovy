package com.racloop

import org.elasticsearch.common.geo.GeoPoint

import com.racloop.workflow.JourneyWorkflow;

class JourneyManagerService {

	def elasticSearchService
	def journeyService
	def jmsService
	def journeyWorkflowService

	def createJourney(User user, JourneyRequestCommand command) {
		Journey journey = new Journey()
		journey.user = user
		journey.dateOfJourney = command.dateOfJourney
		journey.isDriver = command.isDriver
		if(journeyService.saveJourney(journey)) {
			log.info("Journey created with id ${journey.id}")
			command.id = journey.id.toString()
			command.isSaved = true
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
		User user  = User.findByUsername(command.user);
		createJourney(user, command)
	}
	
	def JourneyWorkflow saveJourneyAndInitiateWorkflow(JourneyRequestCommand requestedJourney, JourneyRequestCommand matchedJourney){
		if(!requestedJourney.isSaved) {
			createJourney(requestedJourney)
		}
		JourneyWorkflow workflow = journeyWorkflowService.initiateWorkflow(requestedJourney, matchedJourney)
		return workflow
	}
}
