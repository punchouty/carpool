package com.racloop

class Sos {
	
	String userName
	String mobile
	Double latitude
	Double longitude
	Date sosTimestamp
	String journeyIds
	Boolean processed = true
	
    static constraints = {
		journeyIds(nullable: true, blank: true)
    }
}
