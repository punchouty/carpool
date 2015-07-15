
package com.racloop


class Profile extends grails.plugin.nimble.core.ProfileBase {
	
	String mobile;
	String emergencyContactOne;
	String emergencyContactTwo;
	Boolean isVerified = false;
	String verificationCode;
	Boolean isMale = true;
	Boolean isPushEnabled = false;
	Boolean showReviewPopup = false;
	String getGravatarUri() { Constant.GRAVATAR_URL + emailHash + Constant.GRAVATAR_URL_SUFFIX}
	String travelMode
	String paymentPreference
	String cabPreference
	
	static transients = ['gravatarUri']
	
	static constraints = {
		mobile blank: false, nullable: false, matches:'\\d{10}', unique: true
		emergencyContactOne blank: true, nullable: true
		emergencyContactTwo blank: true, nullable: true
		verificationCode blank: true, nullable: true
		travelMode blank: true, nullable: true
		paymentPreference blank: true, nullable: true
		cabPreference blank: true, nullable: true
	}
	
	static mapping = {
		mobile index : "profile_mobile_index"
		emergencyContactOne index : "profile_emergencyContactOne_index"
		emergencyContactTwo index : "profile_emergencyContactTwo_index"
	}
	
}
