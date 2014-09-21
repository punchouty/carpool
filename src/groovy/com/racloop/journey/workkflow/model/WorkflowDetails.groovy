package com.racloop.journey.workkflow.model

import com.racloop.User
import com.racloop.workflow.JourneyWorkflow

class WorkflowDetails {
	
	JourneyWorkflow workflow
	User otherUser
	List actionButtons
	String state
	boolean showContactInfo
	String workflowId
	
	public WorkflowDetails () {
		this.actionButtons=[]
		this.showContactInfo = false
	}

}
