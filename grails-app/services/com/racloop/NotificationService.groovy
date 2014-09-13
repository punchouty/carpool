package com.racloop

import org.elasticsearch.common.geo.GeoPoint;

import com.racloop.workflow.JourneyWorkflow;

import grails.plugin.jms.Queue;

class NotificationService {
	
	static exposes = ['jms']
	def elasticSearchService
	
	@Queue(name= "msg.notification.workflow.state.change.queue") //also defined in Constant.java. Grails issue
    def processRequestNotifiactionWorkflow(def messageMap) {
		log.info "Recieved message with messageMap ${messageMap}"
		String workflowId = messageMap[Constant.WORKFLOW_ID_KEY]	
		String workflowState = messageMap[Constant.WORKFLOW_STATE_KEY]
		log.info "Recieved message with workflowId ${workflowId} and new state is ${workflowState}"
		JourneyWorkflow workflow = elasticSearchService.findWorkfowById(workflowId)
		log.info "Workflow details ${workflow}"
		
		
    }
	
	
}
