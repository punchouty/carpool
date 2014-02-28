package com.racloop.workflow

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query.FilterBuilder
import org.elasticsearch.index.query.FilterBuilders
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.indices.IndexMissingException
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits

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
	
	private JourneyWorkflow createAndSaveWorkflow(JourneyRequestCommand requestedJourney, JourneyRequestCommand matchedJourney){
		JourneyWorkflow workflow = new JourneyWorkflow()
		workflow.matchedJourneyId=matchedJourney.id
		workflow.requestJourneyId=requestedJourney.id
		workflow.requestedFromPlace = requestedJourney.fromPlace
		workflow.requestedToPlace = requestedJourney.toPlace
		workflow.requestedDateTime = requestedJourney.dateOfJourneyString
		workflow.state = INTIIAL_STATE
		workflow.requestUser = requestedJourney.user
		workflow.matchingUser = matchedJourney.user
		workflow.matchedFromPlace = matchedJourney.fromPlace
		workflow.matchedToPlace = matchedJourney.toPlace
		workflow.matchedDateTime = matchedJourney.dateOfJourneyString
		workflow.isRequesterDriving = requestedJourney.isDriver
		if(workflow.validate()) {
			workflow.id = System.currentTimeMillis()
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
	
