package com.racloop.user



import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.UserBase

import org.junit.Test

import spock.lang.*

import com.racloop.Review
import com.racloop.User

/**
 *
 */
class UserRatingTestSpec extends Specification {
    def setup() {
		//Add Sample Driver and Sample Rider user here if they are not getting added from Bootstarp.groovy
    }

    def cleanup() {
    }

    @Test
	void testSavingUserReviewAsRider() {
		Review review = getSampleReview1()
		when:
		review.save()
		then: 
		review.id != null
		review.getAverageRating() == 3.0d
    }
	
	@Test
	void testSavingUserReviewAsDriver() {
		Review review = getSampleReview3()
		when:
		review.save()
		then:
		review.id != null
		review.getAverageRating() == 2.5d
	}
	
	@Test
	void testGetRatingForAUser() {
		Review review1 = getSampleReview1()
		Review review2 = getSampleReview2()
		Review review3 = getSampleReview3()
		when:
		review1.save()
		review2.save()
		review3.save()
		User user = User.findByUsername("sample.rider@racloop.com")
		then:
		def reviewList = user.getIncomingReviews()
		reviewList.size() == 3
		user.getUserRating() == 2.5d
		
		
	}
	
	@Test
	void testGetDefaultRatingForAUser() {
		when:
		User user = User.findByUsername("sample.rider@racloop.com")
		then:
		def reviewList = user.getIncomingReviews()
		reviewList.size() == 0
		user.getUserRating() == 0.0d
		
	}
	
	private Review getSampleReview1(){
		Review review = new Review()
		review.reviewer = User.findByUsername("sample.driver@racloop.com")
		review.reviewee = User.findByUsername("sample.rider@racloop.com")
		review.comment = "Test comment as Rider"
		review.workflowId = "JId"
		review.safety = 3.0d
		review.comfort = 3.0d
		review.punctualty = 3.0d
		review.overall = 3.0d
		return review
		
	}
	
	private Review getSampleReview2(){
		Review review = new Review()
		review.reviewer = User.findByUsername("sample.driver@racloop.com")
		review.reviewee = User.findByUsername("sample.rider@racloop.com")
		review.comment = "Test comment as Rider"
		review.workflowId = "JId"
		review.safety = 2.0d
		review.comfort = 2.0d
		review.punctualty = 2.0d
		review.overall = 2.0d
		return review
	
	}
	
	private Review getSampleReview3(){
		Review review = new Review()
		review.reviewer = User.findByUsername("sample.driver@racloop.com")
		review.reviewee = User.findByUsername("sample.rider@racloop.com")
		review.isReviewerADriver = true
		review.comment = "Test Comment as Driver"
		review.workflowId = "JId"
		review.safety = 2.0d
		review.comfort = 2.0d
		review.punctualty = 2.5d
		review.overall = 2.5d
		return review
	
	}
}

