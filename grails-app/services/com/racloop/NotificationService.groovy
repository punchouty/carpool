
package com.racloop

import grails.plugin.jms.Queue

import com.racloop.domain.Journey
import com.racloop.journey.workkflow.WorkflowStatus
import com.racloop.workflow.JourneyWorkflow

class NotificationService {

	static exposes = ['jms']
	def journeyDataService
	def grailsApplication
	def emailService
	def jmsService
	def groovyPageRenderer
	

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
			case WorkflowStatus.ACCEPTED.status : // other user accepted request
				sendNotificationForAcceptRequest(sourceJourney,targetJourney)
				break
			case WorkflowStatus.REJECTED.status : // other user rejected request
				sendNotificationForRejectRequest(sourceJourney,targetJourney)
				break
			case WorkflowStatus.CANCELLED.status : // i am canceling previous accepted request
				sendNotificationForCancelRequest(sourceJourney,targetJourney)
				break
			case WorkflowStatus.CANCELLED_BY_REQUESTER.status : // i am canceling my own earlier request 
				sendNotificationForCancelRequestByRequester(sourceJourney,targetJourney)
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
			def dateOfJourneyString = getFormattedDate(targetJourney.dateOfJourney)
			//String mailToRequester = "Your request to share a ride has been sent to ${requestTo?.profile?.fullName?.capitalize()}"
			//emailService.sendMail(requestIntiator.profile.email, "Your request has been sent", mailToRequester) 
			String emailContent = groovyPageRenderer.render(template: "/templates/email/requestReceived", model: [reciever: requestTo?.profile?.fullName?.capitalize(), destination:targetJourney.getTo(), sender:requestIntiator?.profile?.fullName?.capitalize(), journeyDate: dateOfJourneyString]).toString()
			emailService.sendMail(requestTo.profile.email, "Your have received a new OddlyEven request", emailContent)
			
			
			def  messageMap =[
				to: requestTo.profile.mobile, 
				name:requestIntiator?.profile?.fullName?.capitalize(), 
				journeyDate: dateOfJourneyString, 
				state: WorkflowStatus.REQUESTED.status
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
		def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
		User requestIntiator = getUserDeatils(sourceJourney.getEmail())
		User requestTo = getUserDeatils(targetJourney.getEmail())
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String emailContent = groovyPageRenderer.render(template: "/templates/email/requestAccepted", model: [reciever: requestTo?.profile?.fullName?.capitalize(), destination:targetJourney.getTo(), accepter:requestIntiator?.profile?.fullName?.capitalize(), journeyDate: getFormattedDate(targetJourney.dateOfJourney)]).toString()
			//String mailToRequester = "${requestIntiator?.profile?.fullName?.capitalize()} has accepted the request to share the ride with you"
			emailService.sendMail(requestTo.profile.email, "Your request has been accepted", emailContent)
			
			def  messageMap =[
				to: requestTo.profile.mobile, 
				name:requestIntiator?.profile?.fullName?.capitalize(), 
				journeyDate: getFormattedDate(targetJourney.dateOfJourney), 
				state: WorkflowStatus.ACCEPTED.status,
				mobile: requestIntiator.profile.mobile
				]
			jmsService.send(queue: Constant.NOTIFICATION_SMS_QUEUE, messageMap);
		}
		
	}

	private sendNotificationForRejectRequest(Journey sourceJourney, Journey targetJourney) {
		//rejecter
		User requestIntiator = getUserDeatils(sourceJourney.getEmail())
		User requestTo = getUserDeatils(targetJourney.getEmail())
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String emailContent = groovyPageRenderer.render(template: "/templates/email/requestRejected", model: [reciever: requestTo?.profile?.fullName?.capitalize(), destination:targetJourney.getTo(), rejecter:requestIntiator?.profile?.fullName?.capitalize(), journeyDate: getFormattedDate(targetJourney.dateOfJourney)]).toString()
			emailService.sendMail(requestTo.profile.email, "Your OddlyEven request has been rejected", emailContent)
			
			
			def  messageMap =[
				to: requestTo.profile.mobile, 
				name:requestIntiator?.profile?.fullName?.capitalize(), 
				journeyDate: getFormattedDate(targetJourney.dateOfJourney), 
				state: WorkflowStatus.REJECTED.status
				]
			jmsService.send(queue: Constant.NOTIFICATION_SMS_QUEUE, messageMap);
		}

	}
	private sendNotificationForCancelRequest(Journey sourceJourney, Journey targetJourney) {
		User requestIntiator = getUserDeatils(sourceJourney.getEmail())
		User requestTo = getUserDeatils(targetJourney.getEmail())
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String emailContent = groovyPageRenderer.render(template: "/templates/email/requestCancel", model: [reciever: requestTo?.profile?.fullName?.capitalize(), destination:targetJourney.getTo(), user:requestIntiator?.profile?.fullName?.capitalize(), journeyDate: getFormattedDate(targetJourney.dateOfJourney)]).toString()
			emailService.sendMail(requestTo.profile.email, "Your OddlyEven request has been cancelled", emailContent)
			
			//emailService.sendMail(requestIntiator.profile.email, "Your request has been cancelled", mailMessage)
			
			def  messageMap =[
				to: requestTo.profile.mobile, 
				name:requestIntiator?.profile?.fullName?.capitalize(),
				journeyDate: getFormattedDate(targetJourney.dateOfJourney), 
				state: WorkflowStatus.CANCELLED.status
				]
			jmsService.send(queue: Constant.NOTIFICATION_SMS_QUEUE, messageMap);
		}

	}
	
	private sendNotificationForCancelRequestByRequester(Journey sourceJourney, Journey targetJourney) {
		User requestIntiator = getUserDeatils(sourceJourney.getEmail())
		User requestTo = getUserDeatils(targetJourney.getEmail())
		if(validateUser(requestIntiator) && validateUser(requestTo)){
			String emailContent = groovyPageRenderer.render(template: "/templates/email/requestCancel", model: [reciever: requestTo?.profile?.fullName?.capitalize(), destination:targetJourney.getTo(), user:requestIntiator?.profile?.fullName?.capitalize(), journeyDate: getFormattedDate(targetJourney.dateOfJourney)]).toString()
			emailService.sendMail(requestTo.profile.email, "Your OddlyEven request has been cancelled", emailContent)
			
			
			def  messageMap =[
				to: requestTo.profile.mobile, 
				name:requestIntiator?.profile?.fullName?.capitalize(), 
				journeyDate:  getFormattedDate(targetJourney.dateOfJourney), 
				state: WorkflowStatus.CANCELLED_BY_REQUESTER.status
				]
			jmsService.send(queue: Constant.NOTIFICATION_SMS_QUEUE, messageMap);
		}

	}
	
	private validateUser(User user) {
		return user && user.profile && user.profile.fullName && user.profile.email
	}
	
	private String getFormattedDate(Date inputDate){
		return inputDate.format('dd MMM yy HH:mm')
	}


}
