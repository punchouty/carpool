package com.racloop

import org.hibernate.engine.SessionImplementor
import org.hibernate.id.IdentifierGenerator


class Journey implements Serializable {
	
	String id
	Boolean isDriver;
	Date dateOfJourney;
	Date createdDate = new Date();
	
	static belongsTo = [user : User]
		
	static mapping ={
		//id(generator: "uuid2", type: "uuid-binary", length: 16)
		id generator: "com.racloop.util.domain.JourneyIdGenerator", column:"id", unique:"true"
	}
	
	String toString(){
		return "id : ${id} | isDriver : ${isDriver} | date : ${dateOfJourney}";
	}
	
}
