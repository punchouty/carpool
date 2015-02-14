package com.racloop

import grails.plugin.facebooksdk.FacebookContext
import grails.transaction.Transactional



class UserAuthenticationService {
	
	FacebookContext facebookContextProxy
	def grailsApplication
	def userService
	
	@Transactional(readOnly = true)
	public User getRacloopUser(){
		
		def user = getFBUser()
		if(!user){
			user = getAuthenticatedUser()
		}
		return user
		
	}
	
	private User getFBUser(){
		def User user
		if (facebookContextProxy.app.id && facebookContextProxy.authenticated) {
			user  = User.findByFacebookId(facebookContextProxy.user.id.toString())
		}
		return user
	}
	
}
