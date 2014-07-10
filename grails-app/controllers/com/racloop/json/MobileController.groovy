package com.racloop.json

import grails.converters.JSON
import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.ProfileBase

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.DisabledAccountException
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha256Hash

import com.racloop.ElasticSearchService
import com.racloop.JourneyRequestCommand
import com.racloop.User

class MobileController {

	def shiroSecurityManager
	def userService
	def journeyManagerService
	def journeyService
	def journeyWorkflowService
	static Map allowedMethods = [ login: 'POST', logout : 'POST', signup : 'POST', changePassword : 'POST' ]

	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "user": "sample.user", "password": "P@ssw0rd", "rememberMe": "true" }' http://localhost:8080/app/mlogin
	 * @param user
	 * @param password
	 * @param rememberMe
	 * @return
	 */
	def login() {
		def json = request.JSON
		def jsonResponse = null
		if(json) {
			def username = json.user
			def password = json.password
			def rememberMe = json.rememberMe
			def authToken = new UsernamePasswordToken(username, password)			
			if (rememberMe) {
				authToken.rememberMe = true
			}
			try {				
				//TODO - do we need this event mechanism below. See AuthController
				SecurityUtils.subject.login(authToken)
				userService.createLoginRecord(request)
				jsonResponse = [
					"response":"ok",
					"message":"Login Successfull",
					"username" : authenticatedUser.username,
					"name" : authenticatedUser.profile.fullName,
					"email" : authenticatedUser.profile.email,
					"jsessionid" : session.id
				]
			}
			catch (IncorrectCredentialsException e) {
				log.info "Credentials failure for user '${username}'."
				jsonResponse = [
					"response":"error",
					"message":"Login Failed"
				]
			}
			catch (DisabledAccountException e) {
				log.info "Attempt to login to disabled account for user '${username}'."
				jsonResponse = [
					"response":"error",
					"message":"Acount Disabled"
				]
			}
			catch (AuthenticationException e) {
				log.info "General authentication failure for user '${username}'."
				jsonResponse = [
					"response":"error",
					"message":"Authentication Failure"
				]
			}
		}
		else {
			jsonResponse = [
				"response":"error",
				"message":"Invalid JSON request"
			]
		}
		render jsonResponse as JSON
	}

	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "action": "logout" }' http://localhost:8080/app/mlogout
	 * @return
	 */
	def logout() {
		SecurityUtils.subject?.logout()
		def jsonResponse = [
			"response":"ok",
			"message":"User logout successfully"
		]
		render jsonResponse as JSON
	}

	

	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "username": "rajan", "password" : "P@ssw0rd", "passwordConfirm" : "P@ssw0rd", "fullName" : "Rajan Punchouty", "email" : "rajan@racloop.com", "mobile" : "9717744392" }' http://localhost:8080/app/msignup
	 * @return
	 */
	def signup() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		if(json) {
			def sex = json?.sex
			boolean isMale = true
			if(sex != 'male') {
				isMale = false
			}
			def user = InstanceGenerator.user(grailsApplication)
			user.profile = InstanceGenerator.profile(grailsApplication)
			user.profile.owner = user
			user.username = json?.username
			user.pass = json?.password
			user.passConfirm = json?.passwordConfirm
			user.profile.fullName = json?.fullName
			user.profile.email = json?.email
			user.profile.mobile = json?.mobile
			user.profile.isMale = isMale
			user.enabled = true
			user.external = false
	
			def savedUser
			if(user.validate()) {
				userService.generateValidationHash(user)
				savedUser = userService.createUser(user)
				if (savedUser.hasErrors()) {
					errors = savedUser.errors
					jsonMessage = "Input Errors"
				}
				else {
					def authToken = new UsernamePasswordToken(user.username, user.pass)
					SecurityUtils.subject.login(authToken)
					userService.createLoginRecord(request)
					jsonResponse = "ok"
					jsonMessage = "User sign up sucessfully"
				}
			}
			else {
				errors = user.errors
				jsonMessage = "Input Errors"
			}		
		}
		else {
			jsonMessage = "Invalid Json"
		}
		
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
			"jsessionid" : session.id
		]
		render jsonResponseBody as JSON
	}

	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "currentPassword": "P@ssw0rd", "newPassword" : "P@ssw0rd1", "confirmPassword" : "P@ssw0rd1" }' http://localhost:8080/app/mpassword
	 * @return
	 */
	def changePassword() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		String currentPassword = json.currentPassword
		String pass = json.newPassword
		String passConfirm = json.confirmPassword
		def user = authenticatedUser
		if(user) {
			if(!currentPassword) {
				jsonMessage = "User attempting to change password but has not supplied current password"
			}
			else {
				def pwEnc = new Sha256Hash(currentPassword)
				def crypt = pwEnc.toHex()
				if(!crypt.equals(user.passwordHash)) {
					jsonMessage = "User attempting to change password but has supplied wrong password"
				}
				else {
					user.pass = pass
					user.passConfirm = passConfirm		
					if(user.validate() && userService.validatePass(user, true)) {
						userService.changePassword(user)
						jsonResponse = "ok"
						jsonMessage = "Password changed sucessfully"
					}
					else {
						errors = user.errors
					}
				}
			}
		}
		else {
			jsonMessage = "User is not logged in. Cannot change password"
		}
		
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
			"jsessionid" : session.id
		]
		render jsonResponseBody as JSON
	}

	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "email": "rajan@racloop.com" }' http://localhost:8080/app/mforgot
	 * @return
	 */
	def forgotPassword() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		String email = json.email
		if(email) {
			def profile = ProfileBase.findByEmail(email)
			if (!profile) {
				def user = profile.owner
				if (user.external || user.federated) {
					log.info("User identified by [$user.id]$user.username is external or federated")					
					log.info("Sending account password reset email to $user.profile.email with subject $nimbleConfig.messaging.passwordreset.external.subject")
					if(nimbleConfig.messaging.enabled) {
						sendMail {
							to user.profile.email
							from grailsApplication.config.grails.messaging.mail.from
							subject nimbleConfig.messaging.passwordreset.external.subject
							html g.render(template: "/templates/nimble/mail/forgottenpassword_external_email", model: [user: user, baseUrl: grailsLinkGenerator.serverBaseURL]).toString()
						}
						jsonResponse = "ok"
						jsonMessage = "Password retrieve successfully. Please check your email"
					}
					else {
						log.error "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/forgottenpassword_external_email", model: [user: user]).toString()}"
					}
				}
			}
			else {
				jsonMessage = "User account for supplied email address $email was not found when attempting to process forgotten password"
			}
		}
		else {
			jsonMessage = "Invalid Json"
		}
		
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
			"jsessionid" : session.id
		]
		render jsonResponseBody as JSON
	}
	
	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "action": "fetch" }' http://localhost:8080/app/mprofile
	 * @return
	 */
	def getProfile() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		def jsonResponseBody = null
		def user = authenticatedUser
		if(user) {
			jsonResponseBody = [
				"response": "ok",
				"message": "Fetch successful",
				"username" : user.username,
				"fullName" : user.profile.fullName,
				"email" : user.profile.email,
				"mobile" : user.profile.mobile,
				"isMale" : user.profile.isMale,
				"jsessionid" : session.id
			]
		}
		else {
			jsonResponseBody = [
				"response": "error",
				"message": "User is not logged in. Cannot fetch user profile",
				"jsessionid" : session.id
			]
		}
		render jsonResponseBody as JSON
	}
	
	def editProfile() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		String fullName = json?.fullName
		String sex = json?.sex
		String email = json?.email
		String mobile = json?.mobile
		
		boolean isMale = true
		if(sex != 'male') {
			isMale = false
		}
		def user = getAuthenticatedUser()
		if(user) {
			user.profile.fullName = fullName
			user.profile.email = email
			user.profile.mobile = mobile
			user.profile.isMale = isMale
			if (user.validate()) {
				def updatedUser = userService.updateUser(user)
				jsonResponse = "ok"
				jsonMessage = "Profile updated successfully"
			}
			else {
				errors = user.errors
				jsonMessage = "Invalid Data"
			}
		}
		else {
			jsonMessage = "User is not logged in. Cannot change user profile"
		}
		
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
			"jsessionid" : session.id
		]
		render jsonResponseBody as JSON
	}
	
	def addJourney() {
		def user = getAuthenticatedUser()
		def json = request.JSON
		JourneyRequestCommand journeyRequestCommand = new JourneyRequestCommand()
		journeyRequestCommand.user = json.user
		journeyRequestCommand.fromLatitude = json.fromLatitude
		journeyRequestCommand.fromLongitude =json.fromLongitude
		journeyRequestCommand.fromPlace = json.fromPlace
		journeyRequestCommand.toPlace = json.toPlace
		journeyRequestCommand.toLatitude =json.toLatitude
		journeyRequestCommand.toLongitude = json.toLongitude
		journeyRequestCommand.isDriver = json.isDriver
		journeyRequestCommand.dateOfJourney = ElasticSearchService.BASIC_DATE_FORMAT.parseDateTime(json.dateOfJourney).toDate()
		if(!user) {
			user = User.findByUsername(journeyRequestCommand.user);
		}
		journeyManagerService.createJourney(user, journeyRequestCommand)
		render journeyRequestCommand as JSON
		
	}
	
	def myJourneys() {
		def currentUser = getAuthenticatedUser()	
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		JourneyRequestCommand journeyRequestCommand = new JourneyRequestCommand()
		journeyRequestCommand.user = json.user

		def workflows =[]
		def journeys =[]
		int numberOfRecords = 0				
		if(!currentUser) {
			currentUser = User.findByUsername(journeyRequestCommand.user);
		}
				
			journeys = journeyService.findAllActiveJourneyDetailsForUser(currentUser)
			numberOfRecords = journeys?.size()
			jsonMessage = "Successfully executed myJourneys"
			jsonResponse = "ok"
		
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
			"journeys" : journeys
		]
		
		render jsonResponseBody as JSON
	}
	
	def search() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		def searchResultMap = null
		String dateOfJourneyString = json?.dateOfJourneyString
		String validStartTimeString = json?.validStartTimeString
		String fromPlace = json?.fromPlace
		Double fromLatitude = json?.fromLatitude;
		Double fromLongitude = json?.fromLongitude;
		String toPlace = json?.toPlace
		Double toLatitude = json?.toLatitude;
		Double toLongitude = json?.toLongitude;
		Boolean isDriver = json?.isDriver;
		Double tripDistance = json?.tripDistance;
		String tripUnit = json?.tripUnit;
		String ip = request.remoteAddr;
		String name=json?.name
		JourneyRequestCommand currentJourney = new JourneyRequestCommand()
		currentJourney.dateOfJourneyString = dateOfJourneyString
		currentJourney.validStartTimeString = validStartTimeString
		currentJourney.fromPlace = fromPlace
		currentJourney.fromLatitude = fromLatitude
		currentJourney.fromLongitude = fromLongitude
		currentJourney.toPlace = toPlace
		currentJourney.toLatitude = toLatitude
		currentJourney.toLongitude = toLongitude
		currentJourney.isDriver = isDriver
		currentJourney.tripDistance = tripDistance
		currentJourney.tripUnit = tripUnit
		currentJourney.ip = ip
		currentJourney.user=name
		if(chainModel && chainModel.currentJourney) {
			currentJourney = chainModel.currentJourney
		}
		def currentUser = getAuthenticatedUser();
		if (!currentuser) {
			currentUser = User.findByUsername(currentJourney.user);
		}
		
		if(currentUser) {
			setUserInformation(currentUser,currentJourney)
			currentJourney.ip = request.remoteAddr
			setDates(currentJourney)
			if(currentJourney.dateOfJourney && currentJourney.validStartTime && currentJourney.dateOfJourney.after(currentJourney.validStartTime)) {
				if(currentJourney.validate()) {
					searchResultMap = getSearchResultMap(currentUser, currentJourney)
					jsonMessage = "Successfully executed search"
					jsonResponse = "ok"
				}
			}
			else {
				jsonMessage = "Invalid travel date and time"
			}
		}
		else {
			jsonMessage = "User is not logged in. Cannot fetch search results"
		}
		
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
			"searchResults" : searchResultMap
		]
		render jsonResponseBody as JSON
		
	}
	
	
/*
//	def rejectResponse() {
//		String jsonMessage = null
//		String jsonResponse = "error"
//		def workflowId = params.workflowId
//		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.REJECTED.state)
//		jsonMessage = "Successfully executed rejectResponse"
//		jsonResponse = "ok"		
//			def jsonResponseBody = [
//			"response": jsonResponse,
//			"message": jsonMessage,
//			"errors" : errors
//		]			
//	  render jsonResponseBody as JSON
//	}
	
	
	//User 1
	def search() { 
		
	}

	//User 1 - commercial driver
	def searchStartPoint() { }

	//User 1 -Category persist request for other to respond
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
	def acceptResponse() { } acceptIncomingRequest

	//User 1 - reject request
	def rejectResponse() { } rejectIncomingRequest

	//User 2 - cancel earlier sent response
	def cancelResponse() { } cancelOutgoingRequest
	*/

	private getNimbleConfig() {
		grailsApplication.config.nimble
	}
}
