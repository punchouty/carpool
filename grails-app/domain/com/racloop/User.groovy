
package com.racloop

import com.racloop.vechicle.Vehicle;

class User extends grails.plugin.nimble.core.UserBase {
	
	Integer asDriverTotalVotes = 0
	Integer asRiderTotalVotes = 0
	int safety // 1-5 scale - average of all reviews
	int punctualty // 1-5 scale - average of all reviews
	int overall // 1-5 scale - average of all reviews
	
	static hasMany = [
		vehicles : Vehicle,
		activeJourneys : Journey,
		outgoingReviews : Review,
		incomingReviews : Review
	]
	
}
