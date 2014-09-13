package com.racloop

import grails.plugin.jms.Queue

import com.racloop.workflow.JourneyWorkflow

class NotificationService {
	
	static exposes = ['jms']
	def elasticSearchService
	def mailService
	def grailsApplication
	
	@Queue(name= "msg.notification.workflow.state.change.queue") //also defined in Constant.java. Grails issue
    def processRequestNotifiactionWorkflow(def messageMap) {
		log.info "Recieved message with messageMap ${messageMap}"
		String workflowId = messageMap[Constant.WORKFLOW_ID_KEY]	
		String workflowState = messageMap[Constant.WORKFLOW_STATE_KEY]
		log.info "Recieved message with workflowId ${workflowId} and new state is ${workflowState}"
		JourneyWorkflow workflow = elasticSearchService.findWorkfowById(workflowId)
		log.info "Workflow details ${workflow}"
		
    }
	
	def sendMail(String toMail, String emailSubject, String emailContent){
		String fromEmail = grailsApplication.config.grails.mail.username;
		try {
			mailService.sendMail {
				multipart true
				to toMail
				subject emailSubject
				from fromEmail
				//body (view: "error")
				html emailContent
			}
		}
		catch (Exception e) {
			log.info "Erro while sending email", e
		}
	}
	
	
}
