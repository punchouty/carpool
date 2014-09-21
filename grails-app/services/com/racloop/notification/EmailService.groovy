package com.racloop.notification

import grails.plugin.jms.Queue

import com.racloop.Constant
import com.racloop.User
import com.racloop.journey.workkflow.WorkflowState
import com.racloop.notification.model.EmailMessage;
import com.racloop.workflow.JourneyWorkflow

class EmailService {

	def grailsApplication
	def jmsService

	def sendMail(String toMail, String emailSubject, String emailContent, String fromMail = null){
		log.info "Trying to send message to email processor $toMail"
		EmailMessage email = new EmailMessage(toAddress: toMail, fromAddress: fromMail, mailSubject: emailSubject, mailContent: emailContent)
		if(!fromMail) {
			email.fromAddress = grailsApplication.config.grails.mail.username;
		}
		jmsService.send(queue: Constant.NOTIFICATION_EMAIL_QUEUE, email)
		log.info "Sent a message for email processing. Email to be sent to $toMail"
	}
}