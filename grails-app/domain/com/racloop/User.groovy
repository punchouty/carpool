
package com.racloop

class User extends grails.plugin.nimble.core.UserBase {
	
	static hasMany = [
		activeJourneys : Journey,
		travelRequests : TravelRequest,
		travelResponses : TravelResponse,
		travelHistory : TravelHistory
	]
	
}
