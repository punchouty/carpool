
package com.racloop


class Profile extends grails.plugin.nimble.core.ProfileBase {
	
	String mobile;
	Boolean isMale = true;
	Boolean showReviewPopup = false;
	String getGravatarUri() { Constant.GRAVATAR_URL + emailHash + Constant.GRAVATAR_URL_SUFFIX}
	
	static transients = ['gravatarUri']
	
	static constraints = {
		mobile blank: false, nullable: false, matches:'\\d{10}', unique: true
	}
	
}
