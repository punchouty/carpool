package com.racloop


class Journey implements Serializable {
	
	//UUID id
	Boolean isDriver;
	Date dateOfJourney;
	Date createdDate = new Date();
	String from
	String to
	
	static belongsTo = [user : User]
		
	/*static mapping ={
		id(generator: "uuid2", type: "uuid-binary", length: 16)
	}*/
	
	String toString(){
		return "id : ${id} | isDriver : ${isDriver} | date : ${dateOfJourney}";
	}
}
