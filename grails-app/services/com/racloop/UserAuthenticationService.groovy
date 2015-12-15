package com.racloop

import grails.plugin.facebooksdk.FacebookContext
import grails.transaction.Transactional

import org.apache.shiro.SecurityUtils



class UserAuthenticationService {
	
	FacebookContext facebookContextProxy
	def grailsApplication
	def userService
	
	@Transactional(readOnly = true)
	public User getRacloopUser(){
		
		def user = getFBUser()
		if(!user){
			try {
				user = getAuthenticatedUser()
			}
			catch (Exception e) {
				if(SecurityUtils.subject?.principal) {
					SecurityUtils.subject?.logout()
				}
			}
		}
		return user
		
	}
	
	private User getFBUser(){
		/*def User user
		if (facebookContextProxy.app.id && facebookContextProxy.authenticated) {
			user  = User.findByFacebookId(facebookContextProxy.user.id.toString())
		}
		return user*/
		return null //Disabaling FB login. Using FB user to sign in using nimble
	}
	
}
