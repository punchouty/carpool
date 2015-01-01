package com.racloop

import grails.converters.JSON

import com.racloop.workflow.JourneyWorkflow

class ReviewController {
	
	def journeyWorkflowService

	def saveUserReview(){
		println params.inspect()
		Review review = new Review()
		def workflowId = params.workflowId
		boolean isReviewForDriver = true
		JourneyWorkflow workflow = journeyWorkflowService.getWorkflowById(workflowId)
		User currentUser = getAuthenticatedUser();
		if(currentUser.username.equalsIgnoreCase(workflow.requestUser)){
			review.reviewee = User.findByUsername(workflow.matchingUser)
		}
		else {
			review.reviewee = User.findByUsername(workflow.requestUser)
		}
		
		review.reviewer = currentUser
		review.comment = params.comments
		review.safety = params.double('safety')
		review.comfort = params.double('comfort')
		review.punctualty = params.double('punctualty')
		review.overall = params.double('overAll')
		review.workflowId = workflowId
		review.save()
		render "Review Added"
		
	}
	
	
	def loadReviewPage() {
		def workflowId = params.workflowId
		boolean isReviewForDriver = true
		JourneyWorkflow workflow = journeyWorkflowService.getWorkflowById(workflowId)
		User currentUser = getAuthenticatedUser();
		if(currentUser.username.equalsIgnoreCase(workflow?.requestUser)){
			if(workflow.isRequesterDriving){
				isReviewForDriver = false
			}
		}
		else {
			if(!workflow.isRequesterDriving){
				isReviewForDriver = false
			}
		}
		render(view: "/userSession/userReview", model: ['workflowId': workflowId, 'isReviewForDriver':isReviewForDriver])
	}
}
