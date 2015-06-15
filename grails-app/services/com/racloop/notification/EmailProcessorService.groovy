package com.racloop.notification

import grails.plugin.jms.Queue

import com.racloop.notification.model.EmailMessage

class EmailProcessorService {

	static exposes = ['jms']
	def mailService
	def grailsApplication

	@Queue(name= "msg.email.notification.queue") //also defined in Constant.java. Known Grails issue
	def processEmail(def message) {
		log.info "Recieved a message for mail processing. Message is $message"
		Boolean emailEnabled = grailsApplication.config.grails.email.enable
		if(emailEnabled) {
			try {
				EmailMessage emailMessage = message
				mailService.sendMail {
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
		else {
			log.info "------ SIMULATING EMAIL FOR DEVOLOPMENT ENVIRNMENT --------- \n Message is $message"
		}

	}




}
