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
				photoUrl : user.profile.getGravatarUri(),
				mobile : user.profile.mobile,
				isMale : user.profile.isMale,
				emergencyContactOne: user.profile.emergencyContactOne,
				emergencyContactTwo: user.profile.emergencyContactTwo,
				isVerified: user.profile.isVerified,
				travelModePreference:user.profile.travelModePreference,
				paymentPreference:user.profile.paymentPreference,
				cabServicePreference:user.profile.cabPreference
			]
		}
		
	}

}
