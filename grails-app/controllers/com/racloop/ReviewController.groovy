package com.racloop

import grails.converters.JSON

import com.racloop.workflow.JourneyWorkflow

class ReviewController {
	
	def journeyWorkflowService

	def saveUserReview(){
		def messageMap =[:]
		Double defaultRating = 0.0d
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
		review.safety = params.safety ? params.double('safety'):defaultRating
		review.comfort = params.comfort ? params.double('comfort'):defaultRating
		review.punctualty = params.punctualty ? params.double('punctualty'):defaultRating
		review.overall = params.overAll ? params.double('overAll'):defaultRating
		review.workflowId = workflowId
		if(!review.save()) {
			messageMap.error = "Something went wrong while saving the review."
		}
		else {
			messageMap.message = "Review added sucessfully"
		}
		forward controller: 'journey',action: 'history', model: [messageMap: messageMap]
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
