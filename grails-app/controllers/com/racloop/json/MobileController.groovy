package com.racloop.json

import grails.converters.JSON
import groovy.json.JsonSlurper

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.DisabledAccountException
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.UsernamePasswordToken

class MobileController {

	def shiroSecurityManager
	def userService

	/**
	 * curl -XPOST http://localhost:8080/racloop/mobile/login.json -d 'json={"user": "rajan", "password": "password", "rememberMe": "true" }'
	 * @param user
	 * @param password
	 * @param rememberMe
	 * @return
	 */
	def login() {
		def jsonString = params['json']
		def jsonResponse = null
		if(jsonString) {
			def slurper = new JsonSlurper()
			def result = slurper.parseText(jsonString)
			def username = result.user
			def password = result.password
			def rememberMe = result.rememberMe
			def authToken = new UsernamePasswordToken(username, password)			
			if (rememberMe) {
				authToken.rememberMe = true
			}
			try {				
				//TODO - do we need this event mechanism below. See AuthController
				SecurityUtils.subject.login(authToken)
				userService.createLoginRecord(request)
				jsonResponse = [
					"error":"false",
					"code":"200",
					"message":"Login Successfull",
					"username" : authenticatedUser.username,
					"name" : authenticatedUser.profile.fullName,
					"email" : authenticatedUser.profile.email
				]
			}
			catch (IncorrectCredentialsException e) {
				log.info "Credentials failure for user '${username}'."
				jsonResponse = [
					"error":"true",
					"code":"200",
					"message":"Login Successfull"
				]
			}
			catch (DisabledAccountException e) {
				log.info "Attempt to login to disabled account for user '${username}'."
				jsonResponse = [
					"error":"true",
					"code":"200",
					"message":"Acount Disabled"
				]
			}
			catch (AuthenticationException e) {
				log.info "General authentication failure for user '${username}'."
				jsonResponse = [
					"error":"true",
					"code":"200",
					"message":"Authentication Failure"
				]
			}
		}
		else {
			jsonResponse = [
				"error":"true",
				"code":"400",
				"message":"Invalid JSON"
			]
		}
		render jsonResponse as JSON
	}

	/**
	 * curl -XPOST http://localhost:8080/racloop/mobile/logout.json -d 'json={"action": "logout"}'
	 * @return
	 */
	def logout() {
		//TODO - do we need this event mechanism below
//		if(userService.events["logout"]) {
//			log.info("Executing logout callback")
//			userService.events["logout"](authenticatedUser)
//		}
		SecurityUtils.subject?.logout()
		def jsonResponse = [
			"error":"false",
			"code":"200",
			"message":"User logout successfully"
		]
		render jsonResponse as JSON
	}

	def signup() {
		def jsonString = params['json']
		def jsonResponse = null
		if(jsonString) {
			def slurper = new JsonSlurper()
			def result = slurper.parseText(jsonString)
			def sex = result.sex
			boolean isMale = true
			if(sex != 'male') {
				isMale = false
			}
			def user = InstanceGenerator.user(grailsApplication)
			user.profile = InstanceGenerator.profile(grailsApplication)
			user.profile.owner = user
			user.username = result.username
			user.pass = result.password
			user.passConfirm = result.passwordConfirm
			user.profile.fullName = result.fullName
			user.profile.email = result.email
			user.profile.mobile = result.mobile
			user.profile.isMale = isMale
			user.enabled = nimbleConfig.localusers.provision.active
			user.external = false
	
			user.validate()
	
		}
		else {
			jsonResponse = [
				"error":"true",
				"code":"400",
				"message":"Invalid JSON"
			]
		}
	}

	def changePassword() {
	}

	def forgotPassword() {
	}

	//User 1
	def search() { }

	//User 1 - commercial driver
	def searchStartPoint() { }

	//User 1 - persist request for other to respond
	def addJourney() { }

	//User 1 - List of journeys
	def myJourneys() { }

	//User 1 - cancel then persisted request
	//Two use cases - 1. When some one responded 2. When no one responded
	def cancelJourney() { }

	//User 2 - response to matched journey
	def sendResponse() { }

	//User 2 - responses
	//Display phone depending upon status (ACCEPTED)
	//Cancel or status use cases also handled here
	def myOutgoingResponses() { }

	//User 1 - accept request
	// Display phone depending upon status (ACCEPTED)
	//Cancel or status use cases also handled here
	def myIncomingResponses() { }

	//User 1 - accept request
	def acceptResponse() { }

	//User 1 - reject request
	def rejectResponse() { }

	//User 2 - cancel earlier sent response
	def cancelResponse() { }
}
