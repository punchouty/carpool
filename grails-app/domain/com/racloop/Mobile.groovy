package com.racloop

class Mobile {
	String referrer
	String uuid
	String imei
	String imsi
	String iccid
	String mac
	String carrierName
	String countryCode
	String mcc
	String mnc
	String phoneNumber
	String callState
	String dataActivity
	String networkType
	String phoneType
	String simState
	String cordova
	String model
	String platform
	String oSVersion
	String userAgent
	String uuidPhoneNumber
	Date createdDate = new Date();
	
	static mapping = {
		uuid column: "Uuid", index : "Uuid_Index"
		uuidPhoneNumber column: "UuidPhoneNumber", index : "uuidPhoneNumber_index"
	}

    static constraints = {
		referrer(nullable: true, blank: true)
		uuid(nullable: true, blank: true)
		imei(nullable: true, blank: true)
		imsi(nullable: true, blank: true)
		iccid(nullable: true, blank: true)
		mac(nullable: true, blank: true)
		carrierName(nullable: true, blank: true)
		countryCode(nullable: true, blank: true)
		mcc(nullable: true, blank: true)
		mnc(nullable: true, blank: true)
		phoneNumber(nullable: true, blank: true)
		callState(nullable: true, blank: true)
		dataActivity(nullable: true, blank: true)
		networkType(nullable: true, blank: true)
		phoneType(nullable: true, blank: true)
		simState(nullable: true, blank: true)
		cordova(nullable: true, blank: true)
		model(nullable: true, blank: true)
		platform(nullable: true, blank: true)
		oSVersion(nullable: true, blank: true)
		userAgent(nullable: true, blank: true)
		uuidPhoneNumber(nullable: true, blank: true)
    }
}
