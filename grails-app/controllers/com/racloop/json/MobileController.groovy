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
import org.elasticsearch.common.joda.time.format.DateTimeFormatter;


import org.codehaus.groovy.grails.web.mapping.LinkGenerator;

import com.racloop.ElasticSearchService
import com.racloop.JourneyRequestCommand
import com.racloop.User
import com.racloop.journey.workkflow.WorkflowState
import com.racloop.mobile.data.response.MobileResponse;

import grails.plugin.nimble.core.ProfileBase
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.format.DateTimeFormat
import org.elasticsearch.common.joda.time.format.DateTimeFormatter
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat

class MobileController {

	def shiroSecurityManager
	def userService
	def journeyManagerService
	def journeyService
	def journeyWorkflowService
	LinkGenerator grailsLinkGenerator
	static Map allowedMethods = [ login: 'POST', logout : 'POST', signup : 'POST', changePassword : 'POST' ]
	public static final String JAVA_DATE_FORMAT = "dd MMMM yyyy    HH:mm a";
	public static final DateTimeFormatter UI_DATE_FORMAT = DateTimeFormat.forPattern(JAVA_DATE_FORMAT);
	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "email": "sample.user@racloop.com", "password": "P@ssw0rd", "rememberMe": "true" }' http://localhost:8080/app/mlogin
	 * @param user
	 * @param password
	 * @param rememberMe
	 * @return
	 */
	def login() {
		def json = request.JSON
		def jsonResponse = null
		def mobileResponse = new MobileResponse()
		if(json) {
			def email = json.email
			def password = json.password
			def rememberMe = json.rememberMe
			def authToken = new UsernamePasswordToken(email, password)			
			if (rememberMe) {
				authToken.rememberMe = true
			}
			try {				
				//TODO - do we need this event mechanism below. See AuthController
				SecurityUtils.subject.login(authToken)
				userService.createLoginRecord(request)
				authenticatedUser.pass = password //TODO need to remove storing of password. Potential security threat
				mobileResponse.data=authenticatedUser
				mobileResponse.success=true
			}
			catch (IncorrectCredentialsException e) {
				log.info "Credentials failure for user '${email}'."
				mobileResponse.message="Wrong Username/Password combination"
				mobileResponse.success=false
			}
			catch (DisabledAccountException e) {
				log.info "Attempt to login to disabled account for user '${email}'."
				mobileResponse.message="Acount Disabled"
				mobileResponse.success=false
			}
			catch (AuthenticationException e) {
				log.info "General authentication failure for user '${email}'."
				mobileResponse.message="Authentication Failure"
				mobileResponse.total=0
				mobileResponse.success=false
			}
		}
		else {
			mobileResponse.message="Invalid input JSON request"
			mobileResponse.success=false
			
		}
		render mobileResponse as JSON
		
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
		MobileResponse mobileResponse = new MobileResponse();//getMobileResoponse(jsonResponse)
		mobileResponse.message = "Successful Logout"
		mobileResponse.success = true
		render mobileResponse as JSON
		
	}

	

	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "email": "rajan@racloop.com", "password" : "P@ssw0rd", "passwordConfirm" : "P@ssw0rd", "fullName" : "Rajan Punchouty", "mobile" : "9717744392", "gender" : "male" }' http://localhost:8080/app/msignup
	 * @return
	 */
	def signup() {
		MobileResponse mobileResponse = new MobileResponse();
		def json = request.JSON
		def errors = null
		if(json) {
			def gender = json?.gender
			boolean isMale = true
			if(gender != 'male') {
				isMale = false
			}
			def user = InstanceGenerator.user(grailsApplication)
			user.profile = InstanceGenerator.profile(grailsApplication)
			user.profile.owner = user
			user.username = json?.email
			user.pass = json?.password
			user.passConfirm = json?.passwordConfirm
			user.profile.fullName = json?.fullName
			user.profile.email = json?.email
			user.profile.mobile = json?.mobile
			user.profile.isMale = isMale
			user.enabled = true
			user.external = false
			
			
			def profile = ProfileBase.findByEmail(json?.email)
			if(profile) {
				mobileResponse.success = false
				mobileResponse.message = "This email is already in use"
			}
			else {
				def savedUser
				if(user.validate()) {
					userService.generateValidationHash(user)
					savedUser = userService.createUser(user)
					if (savedUser.hasErrors()) {
						errors = savedUser.errors
						mobileResponse.success = false
						mobileResponse.message = "Input Errors"
					}
					else {
						sendMail {
							to user.profile.email
							from grailsApplication.config.grails.messaging.mail.from
							subject nimbleConfig.messaging.passwordreset.external.subject
							html g.render(template: "/templates/nimble/mail/forgottenpassword_external_email", model: [user: user, baseUrl: grailsLinkGenerator.serverBaseURL]).toString()
						}
//						def authToken = new UsernamePasswordToken(user.username, user.pass)
//						SecurityUtils.subject.login(authToken)
//						userService.createLoginRecord(request)
//						authenticatedUser.pass = json?.password //TODO need to remove storing of password. Potential security threat
//						mobileResponse.data=authenticatedUser
						mobileResponse.success=true
						mobileResponse.message = "User sign up sucessfully. Please check your email and activate your account."
					}
				}
				else {
					errors = user.errors
					mobileResponse.message = "Input Errors"
				}	
				
			}	
		}
		else {
			mobileResponse.message = "Invalid Json"
		}
		render mobileResponse as JSON
		
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
		MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
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
		MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
	}
	
	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "action": "fetch" }' http://localhost:8080/app/mprofile
	 * @return
	 */
	def getProfile() {
		MobileResponse mobileResponse = new MobileResponse();
		def user = authenticatedUser
		if(user) {
			mobileResponse.message = "Successfully get profile"
			mobileResponse.success = true
			mobileResponse.data = user
		}
		else {
			mobileResponse.message = "Session Expired. Please login again"
			mobileResponse.success = false
		}
		render mobileResponse as JSON
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
		MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
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
		String user=json?.user
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
		currentJourney.user = user
		if(chainModel && chainModel.currentJourney) {
			currentJourney = chainModel.currentJourney
		}
		def currentUser = getAuthenticatedUser();
		if(!currentUser) {
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
		MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
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
		MobileResponse mobileResponse = getMobileResoponse(journeyRequestCommand)
		render mobileResponse as JSON
		
	}
	
//	curl -X POST -H "Content-Type: application/json" -d '{"user":"admin","name":"Administrator"}' http://localhost:8080/app/mobile/myJourneys
	
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
		
		MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
	}
	
//	curl -X POST -H "Content-Type: application/json" -d '{"user":"admin","name":"Administrator","myJourneyId":"89","matchedJourneyId":"12de4899-74af-451a-a5ad-f3a8bfd1f678","isDummy":true}' http://localhost:8080/app/mobile/requestService
	
	def requestService() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		String myJourneyId=json?.myJourneyId
		String user=json?.user
		boolean isDummy =json?.isDummy
		
		def matchedJourneyId = json?.matchedJourneyId		
		def currentUser = getAuthenticatedUser()
		if(!currentUser) {
			currentUser = User.findByUsername(user);
		}
	
		def currentJourney= journeyService.findJourneyById(myJourneyId, false)
		def matchedJourney = journeyService.findJourneyById(matchedJourneyId, isDummy)
		
		def workflow = journeyManagerService.saveJourneyAndInitiateWorkflow(currentJourney,matchedJourney)
		
		jsonMessage = "Successfully executed requestService"
			jsonResponse = "ok"
		
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
			"workflow" : workflow
		]
		
		MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
	}
	
//	curl -X POST -H "Content-Type: application/json" -d '{"myJourneyId":"89","matchedJourneyId":"91","isDummy":false}' http://localhost:8080/app/mobile/sendResponse
	
	
	def sendResponse(){
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null		
		String myJourneyId=json?.myJourneyId
		boolean isDummy =json?.isDummy
		def currentJourney = journeyService.findJourneyById(myJourneyId, false)
		def matchedJourneyId = json?.matchedJourneyId	
		def matchedJourney = journeyService.findMatchedJourneyById(matchedJourneyId, currentJourney, isDummy)
		def matchedUser = User.findByUsername(matchedJourney.user)
		
		jsonMessage = "Successfully executed requestService"
		jsonResponse = "ok"
	
	def jsonResponseBody = [
		"response": jsonResponse,
		"message": jsonMessage,
		"errors" : errors,
		"matchedJourney" : matchedJourney,
		"matchedUser":matchedUser
	]	
	MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
	}
			
//	curl -X POST -H "Content-Type: application/json" -d '{"myJourneyId":"93","matchedJourneyId":"92","user":"sample.user","isDummy":false}' http://localhost:8080/app/mobile/myOutgoingResponses
	
	def myOutgoingResponses() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		String user=json?.user
		def currentUser = null
		if(!currentUser) {
			currentUser = User.findByUsername(user);
		}
		String myJourneyId=json?.myJourneyId
		boolean isDummy =json?.isDummy
		def journeyInstance = journeyService.findJourneyById(myJourneyId, false)
		/*def matchedJourneyId = json?.matchedJourneyId
		def matchedJourney = journeyService.findMatchedJourneyById(matchedJourneyId, journeyInstance, isDummy)*/
		def requestWorkflowDetails = journeyWorkflowService.getWorkflowRequestedByUserForAJourney(journeyInstance.id, currentUser)
		
		jsonMessage = "Successfully executed myOutgoingResponses"
		jsonResponse = "ok"
		
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
			"requestWorkflowDetails" : requestWorkflowDetails
		]
		MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
	}
	
	
//	curl -X POST -H "Content-Type: application/json" -d '{"myJourneyId":"89","user":"sample.rider","isDummy":false}' http://localhost:8080/app/mobile/myIncomingResponses
			
	def myIncomingResponses() {
		
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		String user=json?.user
		def currentUser = null
		if(!currentUser) {
			currentUser = User.findByUsername(user);
		}
		String myJourneyId=json?.myJourneyId
		boolean isDummy =json?.isDummy
		def journeyInstance = journeyService.findJourneyById(myJourneyId, false)
		/*def matchedJourneyId = json?.matchedJourneyId
		def matchedJourney = journeyService.findMatchedJourneyById(matchedJourneyId, journeyInstance, isDummy)*/
//		def requestWorkflowDetails = journeyWorkflowService.getWorkflowRequestedByUserForAJourney(journeyInstance.id, currentUser)
		def matchedWorkflowDetails = journeyWorkflowService.getWorkflowMatchedForUserForAJourney(journeyInstance.id, currentUser)
		jsonMessage = "Successfully executed myIncomingResponses"
		jsonResponse = "ok"
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
			"matchedWorkflowDetails" : matchedWorkflowDetails
		]
		MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
	}
	
	
//	curl -X POST -H "Content-Type: application/json" -d '{"myJourneyId":"89","workflowId":"43d27623-45cb-4201-aba3-cac07ea03f41","user":"sample.rider"}' http://localhost:8080/app/mobile/acceptResponse
	
	def acceptResponse() { 
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		String mobile = null
		String user=json?.user
		def currentUser = null
		if(!currentUser) {
			currentUser = User.findByUsername(user);
		}
		String myJourneyId=json?.myJourneyId
		def journeyInstance = journeyService.findJourneyById(myJourneyId, false)
		def workflowId = json?.workflowId
		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.ACCEPTED.state)
		def matchedWorkflowDetails = journeyWorkflowService.getWorkflowMatchedForUserForAJourney(journeyInstance.id, currentUser)
		if(matchedWorkflowDetails.showContactInfo){
			mobile=matchedWorkflowDetails.otherUser?.profile?.mobile
		}
		
		jsonMessage = "Successfully executed acceptResponse"
		jsonResponse = "ok"
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
//			"journeyWorkflowService" : journeyWorkflowService,
			"mobile":mobile
		]
		MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
		
	}

	// curl -X POST -H "Content-Type: application/json" -d '{"myJourneyId":"89","workflowId":"43d27623-45cb-4201-aba3-cac07ea03f41","user":"sample.rider"}' http://localhost:8080/app/mobile/rejectResponse
	
	
	def rejectResponse() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		def workflowId = json?.workflowId
		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.REJECTED.state)
		jsonMessage = "Successfully executed rejectResponse"
		jsonResponse = "ok"		
			def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors
		]			
	  MobileResponse mobileResponse = getMobileResoponse(jsonResponseBody)
		render mobileResponse as JSON
	}
	
	private MobileResponse getMobileResoponse(Object data){
		MobileResponse mobileResponse = new MobileResponse(data:data, message:'Success',total:1,success:true)
		return mobileResponse
	}
	
	/*
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
	
	private setUserInformation(User currentUser, JourneyRequestCommand currentJourney) {
		if(currentUser) {
			currentJourney.user = currentUser.username
			currentJourney.name = currentUser.profile.fullName
			currentJourney.isMale = currentUser.profile.isMale
		}
	}
	
	private getSearchResultMap(User currentUser, JourneyRequestCommand currentJourney) {
		currentJourney = getExisitngJourneyForUser(currentUser, currentJourney)
		def selectedJourneyIds = getAlreadySelectedJourneyIdsForCurrentJourney(currentJourney)
		updateHttpSession(currentJourney,selectedJourneyIds)
		def matchedJourney = searchJourneys(currentUser, currentJourney)
		def matchResult =[currentUser: currentUser, currentJourney: currentJourney, journeys : matchedJourney.matchedJourneys, numberOfRecords : matchedJourney.numberOfRecords, isDummyData: matchedJourney.isDummyData]
		return matchResult
	}
	
	private setDates(JourneyRequestCommand currentJourney) {
		if(currentJourney.dateOfJourneyString) {
			currentJourney.dateOfJourney = UI_DATE_FORMAT.parseDateTime(currentJourney.dateOfJourneyString).toDate()
		}
		if(currentJourney.validStartTimeString) {
			currentJourney.validStartTime = UI_DATE_FORMAT.parseDateTime(currentJourney.validStartTimeString).toDate()
		}
		if(!currentJourney.validStartTime) {
			DateTime currentDate = new DateTime()
			//TODO - get this Info from config
			currentDate.plusMinutes(Integer.valueOf(grailsApplication.config.grails.approx.time.to.match))
			currentJourney.validStartTime = currentDate.toDate()
		}
	}
}
