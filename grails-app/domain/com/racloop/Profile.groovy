
package com.racloop

class Profile extends grails.plugin.nimble.core.ProfileBase {
	
	String mobile;
	Boolean isMale = true;
	Date dateOfBirth;
	
	static constraints = {
		mobile nullable: true
		//sex inList : ['Male', 'Female'], nullable: true
		dateOfBirth nullable: true
	}
	
}
