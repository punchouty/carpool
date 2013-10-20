package com.racloop

class Journey {
	
	String name; //transient field to contain name
	Date dateOfJourney;
	Date validStartTime;
	String fromPlace;
	Double fromLatitude;
	Double fromLongitude;
	String toPlace;
	Double toLatitude;
	Double toLongitude;
	Boolean isDriver;
	Double tripDistance;
	Double tripUnit;
	String ip;
	Date createdDate = new Date();
	
	static belongsTo = [user : User]
	
	static transients = ['name', 'validStartTime', 'fromPlace', 'fromLatitude', 'fromLongitude', 'toPlace', 'toLatitude', 'toLongitude', 'isDriver', 'tripDistance', 'tripUnit', 'ip']
	
	static hasMany = {
		notificationRequests: Notification
	}

    static constraints = {
		ip nullable : true
		dateOfJourney nullable : false
		fromPlace blank : false
		toPlace blank : false
    }
	
	String toString(){
		return "id : ${id} | name : ${name} | isDriver : ${isDriver} | date : ${dateOfJourney} | fromPlace : ${fromPlace} | toPlace : ${toPlace}";
	}
}
