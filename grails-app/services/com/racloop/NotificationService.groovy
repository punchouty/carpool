package com.racloop

import grails.plugin.jms.Queue

import com.racloop.domain.Journey;
import com.racloop.journey.workkflow.WorkflowState;
import com.racloop.journey.workkflow.WorkflowStatus;
import com.racloop.workflow.JourneyWorkflow

class NotificationService {

	static exposes = ['jms']
	def journeyDataService
	def grailsApplication
	def emailService
	def jmsService

	@Queue(name= "msg.notification.workflow.state.change.queue") //also defined in Constant.java. Grails issue
	def processRequestNotificationWorkflow(def messageMap) {
		log.info "Received message with messageMap ${messageMap}"
		String journeySourceId = messageMap[Constant.JOURNEY_WORKFLOW_SOURCE_ID]
		String journeyTargetId = messageMap[Constant.JOURNEY_WORKFLOW_TARGET_ID]
		String workflowState = messageMap[Constant.WORKFLOW_STATE_KEY]
		log.info "Received message with source ${journeySourceId} and target ${journeyTargetId} and new state is ${workflowState}"
		Journey sourceJourney = journeyDataService.findJourney(journeySourceId)
		Journey targetJourney = journeyDataService.findJourney(journeyTargetId)
		switch(workflowState) {
			case WorkflowStatus.REQUESTED.status : // send request from search result - resultant user should receive email and sms and push
				sendNotificationForNewRequest(sourceJourney,targetJourney)
				break
			case WorkflowState.ACCEPTED.state : // other user accepted request
				sendNotificationForAcceptRequest(sourceJourney,targetJourney)
				break
			case WorkflowState.REJECTED.state : // other user rejected request
				sendNotificationForRejectequest(sourceJourney,targetJourney)
				break
			case WorkflowState.CANCELLED.state : // i am canceling previous accepted request
				//sendNotificationForCancelRequest(workflow)
				break
			case WorkflowState.CANCELLED_BY_REQUESTER.state : // i am canceling my own earlier request 
				//sendNotificationForCancelRequestByRequester(workflow)
				break
			default :
				log.error "No state worklfow detected "

		}

	}

	private User getUserDeatils(String userName) {
		User user = User.findByUsername(userName)
		return user
	}

	private sendNotificationForNewRequest(Journey sourceJourney, Journey targetJourney) {
		User requestIntiator = getUserDeatils(sourceJourney.getEmail())
		User requestTo = getUserDeatils(targetJourney.getEmail())
		if(validateUser(requestIntiator) && validateUser(requestTo)) {
			String mailToRequester = "Your request to share a ride has been sent to ${requestTo?.profile?.fullName}"
			emailService.sendMail(requestIntiator.profile.email, "Your request has been sent", mailToRequester) 
			
			String mailToReciever = "You have received a request to share a ride with ${requestIntiator?.profile?.fullName}"
			emailService.sendMail(requestTo.profile.email, "Your have received a new request", mailToReciever)
			
			def  messageMap =[
				to: requestTo.profile.mobile, 
				name:requestIntiator.profile.fullName, 
				journeyDate: sourceJourney.dateOfJourney.format('dd MMM yy HH:mm'), 
				state: WorkflowState.INITIATED.state 
				]
			jmsService.send(queue: Constant.NOTIFICATION_SMS_QUEUE, messageMap);
		}
		/*if(targetJourney.getNumberOfCopassengers()>1){
			Journey thirdJourney  = getThirdJourney(targetJourney, sourceJourney)
			User thirdUser = getUserDeatils(thirdJourney.getEmail())
			if(validateUser(thirdUser)){
				String mail = "Another request to share a ride with you has been raised by ${requestIntiator?.profile?.fullName}"
				emailService.sendMail(thirdUser.profile.email, "Another passenger in your journey", mail)
			}
		}*/
		
	}
	
	private Journey getThirdJourney(Journey targetJourney, Journey sourceJourney){
		Journey thirdJourney = null
		if(targetJourney.getMobileFirst().equals(sourceJourney.getMobile())){
			thirdJourney = journeyDataService.findJourney(targetJourney.getMobileSecond(),targetJourney.getDateOfJourneySecond())
		}
		else {
			thirdJourney = journeyDataService.findJourney(targetJourney.getMobileFirst(),targetJourney.getDateOfJourneyFirst())
		}
		return thirdJourney
	}

	private sendNotificationForAcceptRequest(Journey sourceJourney, Journey targetJourney) {
		User requestIntiator = getUserDeatils(sourceJourney.getEmail())
		User requestTo = getUserDeatils(targetJourney.getEmail())
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String mailToRequester = "${requestTo?.profile?.fullName} has accepted the request to share the ride with you"
			emailService.sendMail(requestIntiator.profile.email, "Your request has been accepted", mailToRequester)
			
			def  messageMap =[
				to: requestIntiator.profile.mobile, 
				name:requestTo.profile.fullName, 
				journeyDate: sourceJourney.dateOfJourney.format('dd MMM yy HH:mm'), , 
				state: WorkflowState.ACCEPTED.state,
				mobile: requestTo.profile.mobile
				]
			jmsService.send(queue: Constant.NOTIFICATION_SMS_QUEUE, messageMap);
		}
		
	}

	private sendNotificationForRejectequest(Journey sourceJourney, Journey targetJourney) {
		User requestIntiator = getUserDeatils(sourceJourney.getEmail())
		User requestTo = getUserDeatils(targetJourney.getEmail())
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String mailToRequester = "${requestTo?.profile?.fullName} has rejected the request to share the ride with you"
			emailService.sendMail(requestIntiator.profile.email, "Your request has been rejected", mailToRequester)
			
			def  messageMap =[
				to: requestIntiator.profile.mobile, 
				name:requestTo.profile.fullName, 
				journeyDate: sourceJourney.dateOfJourney.format('dd MMM yy HH:mm'), , 
				state: WorkflowState.REJECTED.state
				]
			jmsService.send(queue: Constant.NOTIFICATION_SMS_QUEUE, messageMap);
		}

	}
	private sendNotificationForCancelRequest(JourneyWorkflow workflow) {
		User requestIntiator = getUserDeatils(workflow.requestUser)
		User requestTo = getUserDeatils(workflow.matchingUser)
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String mailMessage = "Your journey request has been cancelled"
			emailService.sendMail(requestIntiator.profile.email, "Your request has been cancelled", mailMessage + " by ${requestTo?.profile?.fullName}")
			emailService.sendMail(requestTo.profile.email, "Your request has been cancelled", mailMessage)
			
			def  messageMap =[
				to: requestIntiator.profile.mobile, 
				name:requestTo.profile.fullName, 
				journeyDate: workflow.requestedDateTime.format('dd MMM yy HH:mm'), 
				state: WorkflowState.CANCELLED.state
				]
			jmsService.send(queue: Constant.NOTIFICATION_SMS_QUEUE, messageMap);
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
			
			def  messageMap =[
				to: requestTo.profile.mobile, 
				name:requestIntiator.profile.fullName, 
				journeyDate: workflow.requestedDateTime.format('dd MMM yy HH:mm'), 
				state: WorkflowState.CANCELLED_BY_REQUESTER.state
				]
			jmsService.send(queue: Constant.NOTIFICATION_SMS_QUEUE, messageMap);
		}

	}
	
	private validateUser(User user) {
		return user && user.profile && user.profile.fullName && user.profile.email
	}


}
