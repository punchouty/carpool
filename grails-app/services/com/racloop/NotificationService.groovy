package com.racloop

import grails.plugin.jms.Queue

import com.racloop.journey.workkflow.WorkflowState;
import com.racloop.workflow.JourneyWorkflow

class NotificationService {

	static exposes = ['jms']
	def elasticSearchService
	def mailService
	def grailsApplication
	def emailService
	def smsService

	@Queue(name= "msg.notification.workflow.state.change.queue") //also defined in Constant.java. Grails issue
	def processRequestNotifiactionWorkflow(def messageMap) {
		log.info "Received message with messageMap ${messageMap}"
		String workflowId = messageMap[Constant.WORKFLOW_ID_KEY]
		String workflowState = messageMap[Constant.WORKFLOW_STATE_KEY]
		log.info "Received message with workflowId ${workflowId} and new state is ${workflowState}"
		JourneyWorkflow workflow = elasticSearchService.findWorkfowById(workflowId)
		log.info "Workflow details ${workflow}"
		switch(workflowState) {
			case WorkflowState.INITIATED.state : // send request from search result - resultant user should receive email and sms and push
				sendNotificationForNewRequest(workflow)
				break
			case WorkflowState.ACCEPTED.state : // other user accepted request
				sendNotificationForAcceptRequest(workflow)
				break
			case WorkflowState.REJECTED.state : // other user rejected request
				sendNotificationForRejectequest(workflow)
				break
			case WorkflowState.CANCELLED.state : // i am canceling previous accepted request
				sendNotificationForCancelRequest(workflow)
				break
			case WorkflowState.CANCELLED_BY_REQUESTER.state : // i am canceling my own earlier request 
				sendNotificationForCancelRequestByRequester(workflow)
				break
			default :
				log.error "No state worklfow detected $workflowId"

		}

	}

	private User getUserDeatils(String userName) {
		User user = User.findByUsername(userName)
		return user
	}

	private sendNotificationForNewRequest(JourneyWorkflow workflow) {
		User requestIntiator = getUserDeatils(workflow.requestUser)
		User requestTo = getUserDeatils(workflow.matchingUser)
		if(validateUser(requestIntiator) && validateUser(requestTo)) {
			String mailToRequester = "Your request to share a ride has been sent to ${requestTo?.profile?.fullName}"
			emailService.sendMail(requestIntiator.profile.email, "Your request has been sent", mailToRequester) 
			
			String mailToReciever = "You have received a request to share a ride with ${requestIntiator?.profile?.fullName}"
			emailService.sendMail(requestTo.profile.email, "Your have received a new request", mailToReciever)
		}
		
	}

	private sendNotificationForAcceptRequest(JourneyWorkflow workflow) {
		User requestIntiator = getUserDeatils(workflow.requestUser)
		User requestTo = getUserDeatils(workflow.matchingUser)
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String mailToRequester = "${requestTo?.profile?.fullName} has accepted the request to share the ride with you"
			emailService.sendMail(requestIntiator.profile.email, "Your request has been accepted", mailToRequester)
			
		}
		
	}

	private sendNotificationForRejectequest(JourneyWorkflow workflow) {
		User requestIntiator = getUserDeatils(workflow.requestUser)
		User requestTo = getUserDeatils(workflow.matchingUser)
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String mailToRequester = "${requestTo?.profile?.fullName} has rejected the request to share the ride with you"
			emailService.sendMail(requestIntiator.profile.email, "Your request has been rejected", mailToRequester)
			
		}

	}
	private sendNotificationForCancelRequest(JourneyWorkflow workflow) {
		User requestIntiator = getUserDeatils(workflow.requestUser)
		User requestTo = getUserDeatils(workflow.matchingUser)
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String mailMessage = "Your journey request has been cancelled"
			emailService.sendMail(requestIntiator.profile.email, "Your request has been cancelled", mailMessage + " by ${requestTo?.profile?.fullName}")
			emailService.sendMail(requestTo.profile.email, "Your request has been cancelled", mailMessage)
			
		}

	}
	
	private sendNotificationForCancelRequestByRequester(JourneyWorkflow workflow) {
		User requestIntiator = getUserDeatils(workflow.requestUser)
		User requestTo = getUserDeatils(workflow.matchingUser)
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String mailMessageForRequester = "Your journey request has been cancelled"
			String mailMessage = mailMessageForRequester + " by ${requestIntiator?.profile?.fullName}"
			emailService.sendMail(requestIntiator.profile.email, "Your request has been cancelled", mailMessageForRequester)
			emailService.sendMail(requestTo.profile.email, "Your request has been cancelled", mailMessage)
			
		}

	}
	
	private validateUser(User user) {
		return user && user.profile && user.profile.fullName && user.profile.email
	}


}
