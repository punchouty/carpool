package com.racloop.notification

import com.racloop.Constant
import com.racloop.notification.model.EmailMessage

class EmailService {

	def grailsApplication
	def jmsService

	def sendMail(String toMail, String emailSubject, String emailContent, String fromMail = null){
		log.info "Trying to send message to email processor $toMail"
		EmailMessage email = new EmailMessage(toAddress: [toMail].toArray(), fromAddress: fromMail, mailSubject: emailSubject, mailContent: emailContent)
		if(!fromMail) {
			setEmailFrom(email)
		}
		jmsService.send(queue: Constant.NOTIFICATION_EMAIL_QUEUE, email)
		log.info "Sent a message for email processing. Email to be sent to $toMail"
	}
	
	def sendMailToMultipleRecipients(List toMails, String emailSubject, String emailContent, String fromMail = null){
		log.info "Trying to send message to email processor $toMails"
		EmailMessage email = new EmailMessage(toAddress: toMails.toArray(), fromAddress: fromMail, mailSubject: emailSubject, mailContent: emailContent)
		if(!fromMail) {
			setEmailFrom(email)
		}
		jmsService.send(queue: Constant.NOTIFICATION_EMAIL_QUEUE, email)
		log.info "Sent a message for email processing. Email to be sent to $toMails"
	}
	
	private setEmailFrom(EmailMessage message) {
		message.fromAddress = grailsApplication.config.grails.email.mail.from;
	}
}
