package com.racloop

import java.util.Date;

class TravelRequest {
	
	Date dateOfJourney; //to handle remove history data
	Boolean isUserDriver;
	
	static hasMany = {
		responses : TravelResponse
	}
	
	static belongsTo = [requester : User, journey : Journey]

    static constraints = {
    }
}
