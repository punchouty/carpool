package com.racloop

import org.elasticsearch.common.geo.GeoPoint;

class Journey implements Serializable {
	
	Boolean isDriver;
	Date dateOfJourney;
	Date createdDate = new Date();
	
	static belongsTo = [user : User]
		
	static hasMany = {
		travelRequests : TravelRequest
	}
	
	String toString(){
		return "id : ${id} | isDriver : ${isDriver} | date : ${dateOfJourney}";
	}
}
