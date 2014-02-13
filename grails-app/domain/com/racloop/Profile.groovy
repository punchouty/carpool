
package com.racloop

class Profile extends grails.plugin.nimble.core.ProfileBase {
	
	String mobile;
	Boolean isMale = true;
	Integer ratingAsDriverTotalVotes = 0
	Integer ratingAsDriverOverAllExperienceGood = 0
	Integer ratingAsDriverOverAllExperienceOk = 0
	Integer ratingAsDriverOverAllExperienceBad = 0
	Integer ratingAsDriverSafetyGood = 0
	Integer ratingAsDriverSafetyOk = 0
	Integer ratingAsDriverSafetyBad = 0
	Integer ratingAsDriverPunctualtyGood = 0
	Integer ratingAsDriverPunctualtyOk = 0
	Integer ratingAsDriverPunctualtyBad = 0
	Integer ratingAsRiderTotalVotes = 0
	Integer ratingAsRiderOverAllExperienceTotalVotes = 0
	Integer ratingAsRiderOverAllExperienceGood = 0
	Integer ratingAsRiderOverAllExperienceOk = 0
	Integer ratingAsRiderOverAllExperienceBad = 0
	Integer ratingAsRiderPunctualtyGood = 0
	Integer ratingAsRiderPunctualtyOk = 0
	Integer ratingAsRiderPunctualtyBad = 0
	
	static constraints = {
		mobile blank: false, nullable: false, matches:'\\d{10}'
	}
	
}
