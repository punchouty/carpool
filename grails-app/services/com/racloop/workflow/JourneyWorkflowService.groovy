package com.racloop.workflow

import com.racloop.JourneyRequestCommand

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
		}
		return workflow
	}
	
	def acceptJourneyRequest(){
		
	}
	
	def rejectJourneyRequest(){
		
	}
	
	def cancelJourneyRequest(){
	
	}
	
	private JourneyWorkflow createAndSaveWorkflow(JourneyRequestCommand requestedJourney, JourneyRequestCommand matchedJourney){
		JourneyWorkflow workflow = new JourneyWorkflow()
		workflow.matchedJourneyId=matchedJourney.id
		workflow.requestJourneyId=requestedJourney.id
		workflow.state = INTIIAL_STATE
		workflow.requestUser = requestedJourney.name
		workflow.matchingUser = matchedJourney.name
		if(workflow.validate()) {
			workflow.save()
		}
		else {
			log.error "Unable to create workflow ${workflow}"
			return null
		}
		return workflow
		
	}
	private indexWorkflow (JourneyWorkflow workflow) {
		//elasticSearchService.saveWorkflow(workflow)
	}
}
