
package com.racloop

class Profile extends grails.plugin.nimble.core.ProfileBase {
	
	String mobile;
	String sex;
	
	static constraints = {
		mobile nullable: true
		sex inList : ['Male', 'Female'], nullable: true
	}
	
}
