package com.racloop

import java.util.Date;

class Affel {
	String referrer
	String uuid
	String imei
	String oSVersion
	String affelResponse
	String appEvent = Constant.APP_EVENT_FIRST_OPEN
	Boolean success = true
	Date createdDate = new Date();
	
    static constraints = {
		referrer(nullable: true, blank: true)
		uuid(nullable: true, blank: true)
		imei(nullable: true, blank: true)
		oSVersion(nullable: true, blank: true)
		appEvent(nullable: true, blank: true)
		affelResponse(nullable: true, blank: true)
    }
	
	static mapping = {
		uuid column: "Uuid", index : "Affel_Uuid_Index"
	}
}
