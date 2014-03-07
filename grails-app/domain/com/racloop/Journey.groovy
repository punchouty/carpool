package com.racloop


class Journey implements Serializable {
	
	//UUID id
	Boolean isDriver;
	Date dateOfJourney;
	Date createdDate = new Date();
	
	static belongsTo = [user : User]
		
	static hasMany = {
		travelRequests : TravelRequest
	}
	
	/*static mapping ={
		id(generator: "uuid2", type: "uuid-binary", length: 16)
	}*/
	
	String toString(){
		return "id : ${id} | isDriver : ${isDriver} | date : ${dateOfJourney}";
	}
}
