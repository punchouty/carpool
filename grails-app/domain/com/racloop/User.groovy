
package com.racloop

import com.racloop.vechicle.Vehicle

class User extends grails.plugin.nimble.core.UserBase {
	@Deprecated
	private static final String PENDING_REVIEW = "pending"
	@Deprecated
	private static final String REVIEW_COMPLETE = "completed"
	@Deprecated
	private static final String BLANK = null
	//Integer asDriverTotalVotes = 0
	//Integer asRiderTotalVotes = 0
	int safety // 1-5 scale - average of all reviews
	int punctualty // 1-5 scale - average of all reviews
	int overall // 1-5 scale - average of all reviews
	String facebookId
	//http://graph.facebook.com/[UID]/picture
	String getFacebookProfilePic() { Constant.FACEBOOK_URL + facebookId + Constant.PICTURE}
	@Deprecated
	String pendingReview
	String journeyIdForReview
	String userCode
	
	static transients = ['facebookProfilePic']
	
	static hasMany = [
		vehicles : Vehicle,
		activeJourneys : Journey,
		outgoingReviews : Review,
		incomingReviews : Review
	]
	
	static mappedBy = [outgoingReviews: 'reviewer',
		incomingReviews: 'reviewee']
	
	static constraints = {
		facebookId blank: true, nullable: true,  unique: true
		pendingReview blank: true, nullable: true
		journeyIdForReview blank: true, nullable: true
		userCode blank: true, nullable: true
	}
	
	public double getUserRating() {
		Double rating = 0.0d
		def reviewList = getIncomingReviews()
		reviewList.each {review->
			rating = rating + review.getAverageRating()
		}
		int count = reviewList.size()
		if(count>0){
			return trimRating(rating/count)
		}
		else {
			return 0.0d
		}
	}
	
	private trimRating(Double inputRating){
		double ratingAsWholeNumber = Math.floor(inputRating)
		double remainder = inputRating % ratingAsWholeNumber
		if(remainder<0.5d) {
			return ratingAsWholeNumber
		}
		else {
			return ratingAsWholeNumber + 0.5d
		}
	}
	
	public markUserAsReviewPending(){
		this.pendingReview = PENDING_REVIEW
	}
	
	public markUserAsReviewComplete(){
		this.pendingReview = REVIEW_COMPLETE
	}
	
	public nullifyReview(){
		this.pendingReview = BLANK
	}
	
	public String getPhotoUrl() {
		return (this.facebookId ? getFacebookProfilePic(): profile?.getGravatarUri())
	}
	
}
