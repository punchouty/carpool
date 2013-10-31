package com.racloop

class TravelHistory {
	
	String place;
	String geoHash;
	Integer searchCount = 1;
	Double latitude;
	Double longitude;
	Date lastUpdatedAt = new Date();
	
	static belongsTo = [user : User]
	
    static constraints = {
    }
}
