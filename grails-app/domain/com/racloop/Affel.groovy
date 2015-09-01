package com.racloop

class Affel {
	String referrer
	String uuid
	String imei
	String oSVersion
	String affelResponse
	String appEvent = "Register"
	Boolean success = true
	
    static constraints = {
		referrer(nullable: true, blank: true)
		uuid(nullable: true, blank: true)
		imei(nullable: true, blank: true)
		oSVersion(nullable: true, blank: true)
		appEvent(nullable: true, blank: true)
		affelResponse(nullable: true, blank: true)
    }
}
