package com.racloop.workflow

import com.racloop.JourneyRequestCommand
import com.racloop.User
import com.racloop.journey.workkflow.WorkflowState
import com.racloop.journey.workkflow.model.WorkflowDetails

class JourneyWorkflowService {
	
	def elasticSearchService
	
    def initiateWorkflow(JourneyRequestCommand requestedJourney, JourneyRequestCommand matchedJourney) {
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
	
	def searchWorkflowRequestedByUserForAJourney(String journeyId, User user) {
		return elasticSearchService.searchWorkflowRequestedByUserForAJourney(journeyId, user)
	}
	
	def getWorkflowRequestedByUserForAJourney(String journeyId, User user) {
		def requestedWorkflowList = elasticSearchService.searchWorkflowRequestedByUserForAJourney(journeyId, user)
		return populateRequstedWorkflowDetails(requestedWorkflowList)
	}
	
	def searchWorkflowMatchedForUser(User user) {
		return elasticSearchService.searchWorkflowMatchedForUser(user)
	}
	
	def searchWorkflowMatchedForUserForAJourney(String journeyId,User user) {
		return elasticSearchService.searchWorkflowMatchedForUserForAJourney(journeyId, user)
	}
	
	def getWorkflowMatchedForUserForAJourney(String journeyId,User user) {
		def matchedWorkflowList = elasticSearchService.searchWorkflowMatchedForUserForAJourney(journeyId, user)
		return populateMatchededWorkflowDetails(matchedWorkflowList)
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
		workflow.state = WorkflowState.INITIATED.getState()
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
	
	public List populateRequstedWorkflowDetails(List requestedWorkflowList) {
		def requestWorkflowDetails =[]
		requestedWorkflowList.each {workflow ->
			WorkflowDetails workflowDetails = new WorkflowDetails()
			workflowDetails.workflow = workflow
			workflowDetails.otherUser = User.findByUsername(workflow.matchingUser)
			workflowDetails.state = (workflow.state ==WorkflowState.INITIATED.getState()?'Sent':workflow.state)
			workflowDetails.actionButtons.addAll(getAvailableActionForRequestSent(workflow.state))
			workflowDetails.showContactInfo = shouldDisplayOtherUserInfoForSentRequest(workflow.state)
			requestWorkflowDetails << workflowDetails
		}
		return requestWorkflowDetails
	}
	
	private List getAvailableActionForRequestSent(String state) {
		def actionButton =[]
		if(state == WorkflowState.ACCEPTED.getState() || state == WorkflowState.INITIATED.getState()){
			actionButton = ['Cancel']
		}
		return actionButton
	}
	
	private boolean shouldDisplayOtherUserInfoForSentRequest(String state) {
		boolean showContactInfo = false
		if(state == WorkflowState.ACCEPTED.getState()){
			showContactInfo = true
		}
		return showContactInfo
	}
	
	public List populateMatchededWorkflowDetails(List matchedWorkflowList) {
		def matchedWorkflowDetails =[]
		matchedWorkflowList.each {workflow ->
			WorkflowDetails workflowDetails = new WorkflowDetails()
			workflowDetails.workflow = workflow
			workflowDetails.otherUser = User.findByUsername(workflow.requestUser)
			workflowDetails.state = workflow.state
			workflowDetails.actionButtons.addAll(getAvailableActionForResponse(workflow.state))
			workflowDetails.showContactInfo = shouldDisplayOtherUserInfoForResponse(workflow.state)
			matchedWorkflowDetails << workflowDetails
		}
		return matchedWorkflowDetails
	}
	
	private List getAvailableActionForResponse(String state) {
		return WorkflowState.getAction(state)
	}
	
	private boolean shouldDisplayOtherUserInfoForResponse(String state) {
		boolean showContactInfo = false
		if(state == WorkflowState.ACCEPTED.getState()){
			showContactInfo = true
		}
		return showContactInfo
	}
	
	
}
	
