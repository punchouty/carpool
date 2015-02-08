package com.racloop.taglib

import grails.plugin.facebooksdk.FacebookContext
import grails.plugin.nimble.core.UserBase

import org.apache.shiro.SecurityUtils

import com.racloop.User

class RacloopTagLib {
   
	FacebookContext facebookContextProxy
    //static encodeAsForTags = [tagName: 'raw']
	
	static namespace ="racloop"
	
	static returnObjectForTags = ['getUser', 'getAppId']
	
	def userLoggedIn = {attrs, body ->
		if ((SecurityUtils.subject.principal != null) || getFBUser()) {
			out << body()
		}
	}
	
	def userNotLoggedIn = {attrs, body ->
		if (SecurityUtils.subject.principal == null && !getFBUser()) {
			out << body()
		}
	}
	
	def getUser = {attrs, body ->
		def User user
		if (facebookContextProxy.app.id && facebookContextProxy.authenticated) {
			user  = User.findByFacebookId(facebookContextProxy.user.id.toString())
		}
		if(!user) {
			user = UserBase.get(SecurityUtils.subject?.principal)
		}
		
		return user
	}
	
	private boolean getFBUser(){
		def User user
		if (facebookContextProxy.app.id && facebookContextProxy.authenticated) {
			user  = User.findByFacebookId(facebookContextProxy.user.id.toString())
		}
		return user && user.profile.isVerified?true:false
	}
	
	def getAppId = {attrs, body ->
		return facebookContextProxy.app.id
	}
}
