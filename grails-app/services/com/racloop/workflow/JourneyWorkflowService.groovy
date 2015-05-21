package com.racloop.workflow

import com.racloop.Constant
import com.racloop.JourneyRequestCommand
import com.racloop.User
import com.racloop.elasticsearch.WorkflowIndexFields
import com.racloop.journey.workkflow.WorkflowState
import com.racloop.journey.workkflow.model.WorkflowDetails
import com.racloop.util.domain.JourneyWorkflowIdGenerator

class JourneyWorkflowService {
	
	def elasticSearchService
	
	def jmsService
	
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
	
	def searchWorkflowRequestedByUserForAJourney(String journeyId, User user) {
		return elasticSearchService.searchWorkflowRequestedByUserForAJourney(journeyId, user)
	}
	
	def getWorkflowRequestedByUserForAJourney(String journeyId, User user) {
		def requestedWorkflowList = elasticSearchService.searchWorkflowRequestedByUserForAJourney(journeyId, user)
		return populateWorkflowDetails(requestedWorkflowList)
	}
	
	
	def searchWorkflowMatchedForUserForAJourney(String journeyId,User user) {
		return elasticSearchService.searchWorkflowMatchedForUserForAJourney(journeyId, user)
	}
	
	def getWorkflowMatchedForUserForAJourney(String journeyId,User user) {
		def matchedWorkflowList = elasticSearchService.searchWorkflowMatchedForUserForAJourney(journeyId, user)
		return populateMatchededWorkflowDetails(matchedWorkflowList)
	}
	
	def getAlreadySelectedJourneyMapForCurrentJourney(JourneyRequestCommand currentJourney){
		Map selectedJourneyMap = [:]
		if(currentJourney.id ) {
			def selectedWorkflows = elasticSearchService.searchWorkflowByRequestedJourney(currentJourney)
			selectedWorkflows.each {it->
				selectedJourneyMap.put(it.matchedJourneyId, it)
			}
			
			def incomingWorkflow = elasticSearchService.searchWorkflowByMatchedJourney(currentJourney)
			incomingWorkflow.each {it->
				selectedJourneyMap.put(it.requestJourneyId, it)
			}
		}
		
		return selectedJourneyMap

	}
	
	private JourneyWorkflow createAndSaveWorkflow(JourneyRequestCommand requestedJourney, JourneyRequestCommand matchedJourney){
		JourneyWorkflow workflow = new JourneyWorkflow()
		workflow.matchedJourneyId=matchedJourney.id
		workflow.requestJourneyId=requestedJourney.id
		workflow.requestedFromPlace = requestedJourney.from
		workflow.requestedToPlace = requestedJourney.to
		workflow.requestedDateTime = requestedJourney.dateOfJourney
		workflow.state = WorkflowState.INITIATED.getState()
		workflow.requestUser = requestedJourney.user
		workflow.matchingUser = matchedJourney.user
		workflow.matchedFromPlace = matchedJourney.from
		workflow.matchedToPlace = matchedJourney.to
		workflow.matchedDateTime = matchedJourney.dateOfJourney
		workflow.isRequesterDriving = requestedJourney.isDriver
		workflow.isMatchedUserDriving = matchedJourney.isDriver
		workflow.id = new JourneyWorkflowIdGenerator().generate(null, workflow)
		//workflow.save()
		return workflow
		
	}
	private indexWorkflow (JourneyWorkflow workflow) {
		elasticSearchService.indexWorkflow(workflow)
	}
	
	private sendConfirmationToBothUsers(JourneyWorkflow workflow) {
		sendNotificationForWorkflowStateChange(workflow.id.toString(), WorkflowState.INITIATED.state)
		sendConfirmationEmail(workflow)
		sendConfirmationSMS(workflow)
	}
	
	private sendConfirmationEmail(JourneyWorkflow workflow) {
		
	}
	
	private sendConfirmationSMS(JourneyWorkflow workflow) {
		
	}
	
	public List populateWorkflowDetails(List requestedWorkflowList) {
		def requestWorkflowDetails =[]
		requestedWorkflowList.each {workflow ->
			WorkflowDetails workflowDetails = new WorkflowDetails()
			workflowDetails.otherUser = User.findByUsername(workflow.matchingUser, [readOnly:true])
			workflowDetails.actionButtons.addAll(getAvailableActionForRequestSent(workflow.state))
			setWorkflowDetails(workflow, workflowDetails)
			requestWorkflowDetails << workflowDetails
		}
		return requestWorkflowDetails
	}
	
	public List populateMatchededWorkflowDetails(List requestedWorkflowList) {
		def requestWorkflowDetails =[]
		requestedWorkflowList.each {workflow ->
			WorkflowDetails workflowDetails = new WorkflowDetails()
			workflowDetails.otherUser = User.findByUsername(workflow.requestUser, [readOnly:true])
			workflowDetails.actionButtons.addAll(getAvailableActionForResponse(workflow.state))
			setWorkflowDetails(workflow, workflowDetails)
			requestWorkflowDetails << workflowDetails
		}
		return requestWorkflowDetails
	}
	
	private setWorkflowDetails(JourneyWorkflow workflow, WorkflowDetails workflowDetails) {
		
			workflowDetails.workflow = workflow
			workflowDetails.state = workflow.state//(workflow.state ==WorkflowState.INITIATED.getState()?'Sent':workflow.state)
			workflowDetails.showContactInfo = shouldDisplayOtherUserInfoForSentRequest(workflow.state)
			if(workflowDetails.showContactInfo) {
				workflowDetails.otherUserMobileNummber = workflowDetails?.otherUser?.profile?.mobile
			}
			
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
	
	public updateWorkflowState (String workflowId, String newState) {
		elasticSearchService.updateWorkflowState(workflowId, newState)
		sendNotificationForWorkflowStateChange(workflowId, newState)
	}
	
	public getWorkFlowByJounreyTuple (String requestJourneyId, String matchedJourneyId) {
		return elasticSearchService.searchWorkflowByJourneyTuple(requestJourneyId, matchedJourneyId)
	}
	
	public void cancelAllWorkflowsForAJounrey(String journeyId) {
		cancelOutgoingRequestForAJourney(journeyId)
		cancelIncomingRequestForAJourney(journeyId)
	}
	
	private cancelOutgoingRequestForAJourney (String requestJourneyId) {
		def workflowList = elasticSearchService.searchActiveWorkflowByMatchedJourneyId(WorkflowIndexFields.REQUEST_JOURNEY_ID, requestJourneyId)
		cancelWorkflowsAndSendNotification(workflowList, false)
		
	}
	
	private cancelIncomingRequestForAJourney (String matchedJourneyId) {
		def workflowList = elasticSearchService.searchActiveWorkflowByMatchedJourneyId(WorkflowIndexFields.MATCHED_JOURNEY_ID, matchedJourneyId)
		cancelWorkflowsAndSendNotification(workflowList, true)
	}
	
	private void cancelWorkflowsAndSendNotification(List workflowList, boolean isIncoming) {
		workflowList.each {workflow ->
			String newState
			if(isIncoming) {
				newState = WorkflowState.CANCELLED.state
			}
			else {
				newState = WorkflowState.CANCELLED_BY_REQUESTER.state
			}
			elasticSearchService.updateWorkflowState(workflow.id.toString(), newState)
			sendNotificationForWorkflowStateChange(workflow.id.toString(), newState)
		}
	}
	
	private void sendNotificationForWorkflowStateChange (String workflowId, String state){
		def  messageMap =[(Constant.WORKFLOW_ID_KEY):workflowId, (Constant.WORKFLOW_STATE_KEY):state] 
		jmsService.send(queue: Constant.NOTIFICATION_WORKFLOW_STATE_CHANGE_QUEUE, messageMap)
	}
	
	public JourneyWorkflow getWorkflowById(String workflowId){
		JourneyWorkflow workflow = elasticSearchService.findWorkfowById(workflowId)
		return workflow
	}
	
	
}
	
