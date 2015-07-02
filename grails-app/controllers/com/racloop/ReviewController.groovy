package com.racloop

import grails.converters.JSON

import com.racloop.workflow.JourneyWorkflow

class ReviewController {
	
	def userReviewService

	def saveUserReview(){
		def messageMap =[:]
		Double defaultRating = 0.0d
		Review review = new Review()
		
		User currentUser = getRacloopAuthenticatedUser();
		review.reviewer = currentUser
		review.comment = params.comments
		review.punctualty = params.punctualty ? params.double('punctualty'):defaultRating
		review.overall = params.overAll ? params.double('overAll'):defaultRating
		review.journeyId= params.journeyId
		String pairId = params.pairId
		userReviewService.saveUserRating(currentUser, review, pairId)
		
		forward controller: 'journey',action: 'history', model: [messageMap: messageMap]
	}
	
	
	def loadReviewPage() {
		def pairId = params.pairId
		
		render(view: "/userSession/userReview", model: ['pairId': pairId])
	}
}
