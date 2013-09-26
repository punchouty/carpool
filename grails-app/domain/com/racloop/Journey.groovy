package com.racloop

class Journey {
	
	String fromPlace
	Double fromLatitude;
	Double fromLongitude;
	String toPlace;
	Double toLatitude;
	Double toLongitude;
	Boolean isDriver;
	Date dateOfJourney;
	
	static hasMany = {
		notifications: Notification
	}

    static constraints = {
    }
}
