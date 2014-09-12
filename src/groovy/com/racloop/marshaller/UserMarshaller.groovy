package com.racloop.marshaller

import grails.converters.JSON
import com.racloop.User

class UserMarshaller {
	
	void register() {
		JSON.registerObjectMarshaller(User) { User user ->
			return [
				fullName : user.profile.fullName,
				email : user.profile.email,
				password : user.pass,
				photoUrl : 'http://www.gravatar.com/avatar/' + user.profile.emailHash,
				mobile : user.profile.mobile,
				isMale : user.profile.isMale
			]
		}
		
	}

}
