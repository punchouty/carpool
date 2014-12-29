package com.racloop.notification

import grails.plugin.jms.Queue

import com.racloop.notification.model.EmailMessage

class EmailProcessorService {

	static exposes = ['jms']
	def mailService

	@Queue(name= "msg.email.notification.queue") //also defined in Constant.java. Known Grails issue
	def processEmail(def message) {
		log.info "Recieved a message for mail processing. Message is $message"
		try {
			EmailMessage emailMessage = message
			mailService.sendMail {
				async true
				multipart true
				to emailMessage.toAddress
				subject emailMessage.mailSubject
				from emailMessage.fromAddress
				//body (view: "error")
				html emailMessage.mailContent
			}
			log.info "Finsihsed sending email to ${emailMessage.toAddress} "
		}
		catch (Exception e) {
			log.info "Error while sending email", e
		}

	}




}
