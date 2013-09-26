
package com.racloop

class Profile extends grails.plugin.nimble.core.ProfileBase {
	
	String mobile;
	String sex;
	
	static constraints = {
		sex inList : ['Male', 'Female']
	}
	
}
