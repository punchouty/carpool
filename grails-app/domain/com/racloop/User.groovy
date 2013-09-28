
package com.racloop

class User extends grails.plugin.nimble.core.UserBase {
	
	static hasMany = [
		journeys : Journey, 
		notificationRequests : Notification
	]
	
}
