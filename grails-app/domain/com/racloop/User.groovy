
package com.racloop

import com.racloop.vechicle.Vehicle

class User extends grails.plugin.nimble.core.UserBase {
	
	//Integer asDriverTotalVotes = 0
	//Integer asRiderTotalVotes = 0
	int safety // 1-5 scale - average of all reviews
	int punctualty // 1-5 scale - average of all reviews
	int overall // 1-5 scale - average of all reviews
	String facebookId
	//http://graph.facebook.com/[UID]/picture
	String getFacebookProfilePic() { Constant.FACEBOOK_URL + facebookId + Constant.PICTURE}
	
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
	}
	
	public double getUserRating() {
		Double rating = 0.0d
		def reviewList = getIncomingReviews()
		reviewList.each {review->
			rating = rating + review.getAverageRating()
		}
		int count = reviewList.size()
		if(count>0){
			return rating/count
		}
		else {
			return 0.0d
		}
	}
	
}
