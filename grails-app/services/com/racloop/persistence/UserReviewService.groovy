package com.racloop.persistence

import grails.transaction.Transactional

@Transactional
class UserReviewService {

    def markUsersForPendingReview(){
		//Find All journeys between T and T-1
		//Find all user for above journeys
		//mark all users above for pending review
	}
	
	def findJourneysToBeReviewdForAUser(){
		//Find last journey for the user (use my history)
		//Find all related journeys
	}
	
	def clearUserForReview(){
		
	}
	
}
