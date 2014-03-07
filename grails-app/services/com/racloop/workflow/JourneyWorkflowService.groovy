package com.racloop.workflow

import com.racloop.JourneyRequestCommand
import com.racloop.User

class JourneyWorkflowService {
	
	def elasticSearchService
	def journeyManagerService
	
	public static final INTIIAL_STATE ='Initiated'

    def JourneyWorkflow saveJourneyAndInitiateWorkflow(JourneyRequestCommand requestedJourney, JourneyRequestCommand matchedJourney){
		if(!requestedJourney.isSaved) {
			journeyManagerService.createJourney(requestedJourney)
		}
		JourneyWorkflow workflow = createAndSaveWorkflow(requestedJourney, matchedJourney)
		if(workflow) {
			indexWorkflow(workflow)
			sendConfirmationToBothUsers(workflow)
		}
		return workflow
	}
	
	def acceptJourneyRequest(){
		
	}
	
	def rejectJourneyRequest(){
		
	}
	
	def cancelJourneyRequest(){
	
	}
	
	def searchWorkflowRequestedByUser(User user) {
		return elasticSearchService.searchWorkflowRequestedByUser(user)
	}
	
	def getAlreadySelectedJourneyIdsForCurrentJourney(JourneyRequestCommand currentJourney){
		List selectedJourneyIds = []
		if(currentJourney.id && currentJourney.isSaved) {
			def selectedWorkflows = elasticSearchService.searchWorkflowByRequestedJourney(currentJourney)
			selectedWorkflows.each {it->
				selectedJourneyIds<<it.matchedJourneyId
			}
		}
		
		return selectedJourneyIds

	}
	
	private JourneyWorkflow createAndSaveWorkflow(JourneyRequestCommand requestedJourney, JourneyRequestCommand matchedJourney){
		JourneyWorkflow workflow = new JourneyWorkflow()
		workflow.matchedJourneyId=matchedJourney.id
		workflow.requestJourneyId=requestedJourney.id
		workflow.requestedFromPlace = requestedJourney.fromPlace
		workflow.requestedToPlace = requestedJourney.toPlace
		workflow.requestedDateTime = requestedJourney.dateOfJourney
		workflow.state = INTIIAL_STATE
		workflow.requestUser = requestedJourney.user
		workflow.matchingUser = matchedJourney.user
		workflow.matchedFromPlace = matchedJourney.fromPlace
		workflow.matchedToPlace = matchedJourney.toPlace
		workflow.matchedDateTime = matchedJourney.dateOfJourney
		workflow.isRequesterDriving = requestedJourney.isDriver
		if(workflow.validate()) {
			workflow.id = UUID.randomUUID()
			return workflow
		}
		else {
			log.error "Unable to create workflow ${workflow}"
			return null
		}
		return workflow
		
	}
	private indexWorkflow (JourneyWorkflow workflow) {
		elasticSearchService.indexWorkflow(workflow)
	}
	
	private sendConfirmationToBothUsers(JourneyWorkflow workflow) {
		sendConfirmationEmail(workflow)
		sendConfirmationSMS(workflow)
	}
	
	private sendConfirmationEmail(JourneyWorkflow workflow) {
		
	}
	
	private sendConfirmationSMS(JourneyWorkflow workflow) {
		
	}
	
	
}
	
