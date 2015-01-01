
package com.racloop


class Profile extends grails.plugin.nimble.core.ProfileBase {
	
	String mobile;
	String emergencyContact;
	Boolean isVerified = false;
	String verificationCode;
	Boolean isMale = true;
	Boolean isPushEnabled = false;
	Boolean showReviewPopup = false;
	String getGravatarUri() { Constant.GRAVATAR_URL + emailHash + Constant.GRAVATAR_URL_SUFFIX}
	
	static transients = ['gravatarUri']
	
	static constraints = {
		mobile blank: false, nullable: false, matches:'\\d{10}', unique: true
		emergencyContact blank: true, nullable: true
		verificationCode blank: true, nullable: true
	}
	
	static mapping = {
		mobile index : "profile_mobile_index"
		emergencyContact index : "profile_emergencyContact_index"
	}
	
}
