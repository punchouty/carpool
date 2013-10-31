package com.racloop

import java.util.Date;

class TravelResponse {
	
	Date dateOfJourney; //to handle remove history data
	Boolean isUserDriver;
	String status = 'Awaiting';
	
	static belongsTo = [responder : User, travelRequest : TravelRequest]

    static constraints = {
		status inList : ['Awaiting', 'Accepted', 'Rejected']
    }
}
