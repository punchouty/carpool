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
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.format.DateTimeFormat
import org.elasticsearch.common.joda.time.format.DateTimeFormatter

import com.racloop.Constant;
import com.racloop.ElasticSearchService;
import com.racloop.GenericStatus;
import com.racloop.JourneyRequestCommand
import com.racloop.Profile;
import com.racloop.Sos;
import com.racloop.User
import com.racloop.journey.workkflow.WorkflowState
import com.racloop.mobile.data.response.MobileResponse
import com.racloop.staticdata.StaticData;
import com.racloop.util.date.DateUtil

import static com.racloop.util.date.DateUtil.convertUIDateToElasticSearchDate


class MobileController {

	def shiroSecurityManager
	def userService
	def userManagerService
	def journeyManagerService
	def journeyService
	def journeyWorkflowService
	def jmsService
	LinkGenerator grailsLinkGenerator
	static Map allowedMethods = [ login: 'POST', logout : 'POST', signup : 'POST', changePassword : 'POST', forgotPassword : 'POST' ]
	
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
			def currentDateString = json.currentDateString
			def currentDate = convertUIDateToElasticSearchDate(currentDateString)
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
				def journeys = journeyService.findCurrentJourneyForUser(authenticatedUser, currentDate)
				def currentJourney = null
				if(journeys.size() > 0) currentJourney = journeys[0]
				//JourneyRequestCommand currentJourney = journeyService.searchCurrentJourney(authenticatedUser, currentDate)
				mobileResponse.currentJourney = currentJourney
				mobileResponse.success=true
			}
			catch (IncorrectCredentialsException e) {
				log.info "Credentials failure for user '${email}'."
				mobileResponse.message="Wrong Username/Password combination"
				mobileResponse.success=false
			}
			catch (DisabledAccountException e) {
				log.info "Attempt to login to disabled account for user '${email}'."
				mobileResponse.message="Mobile number is not verified or Acount is disabled"
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
			user.enabled = nimbleConfig.localusers.provision.active
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
						def errorMessage = ""
						errors.allErrors.each {
							errorMessage = errorMessage + g.message(code: it.getCodes()[0], args: []) + "</br>"
							println "g.message(code: it.getCodes()[0], args: []) : " + g.message(code: it.getCodes()[0], args: []) 
						}
						mobileResponse.message = errorMessage
					}
					else {
//						sendMail {
//							to user.profile.email
//							from grailsApplication.config.grails.messaging.mail.from
//							subject nimbleConfig.messaging.registration.subject
//							html g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: savedUser]).toString()
//						}
						log.info("Sending verification code to $user.profile.mobile")
						userManagerService.setUpMobileVerificationDuringSignUp(savedUser.profile)
						mobileResponse.success=true
						mobileResponse.message = "User sign up sucessfully. Check SMS for verificaton code."
					}
				}
				else {
					errors = user.errors
					def errorMessage = ""
					errors.allErrors.each {
						errorMessage = errorMessage + g.message(code: it.getCodes()[0], args: []) + "</br>"
					}
					mobileResponse.message = errorMessage
				}	
				
			}	
		}
		else {
			mobileResponse.message = "Invalid Input Json"
		}
		render mobileResponse as JSON
		
	}
	
	def verifyMobile() {
		MobileResponse mobileResponse = new MobileResponse();
		def json = request.JSON
		def errors = null
		if(json) {
			def mobile = json?.mobile
			def verificationCode = json?.verificationCode
			def status = userManagerService.verify(mobile, verificationCode)
			if(status == GenericStatus.SUCCESS) {
				mobileResponse.message="Mobile Verified Successfully"
				mobileResponse.total=0
				mobileResponse.success=true
			}
			else if (status == GenericStatus.FAILURE) {
				mobileResponse.message = "Invalid verification code. Please try again."
				mobileResponse.total=0
				mobileResponse.success=false
			}
			else {
				mobileResponse.message = "No associated account with given mobile number."
				mobileResponse.total=0
				mobileResponse.success=false
			}
		}
		else {
			mobileResponse.total=0
			mobileResponse.success=false
			mobileResponse.message = "Invalid Input Json"
		}
		render mobileResponse as JSON
	}
	
	def resendSms() {
		MobileResponse mobileResponse = new MobileResponse();
		def json = request.JSON
		def errors = null
		if(json) {
			def mobile = json?.mobile
			def status = userManagerService.setUpMobileVerification(mobile)
			if(status == GenericStatus.SUCCESS) {
				mobileResponse.message = "SMS Sent successfully. Please check your mobile."
				mobileResponse.total=0
				mobileResponse.success=true
			}
			else {
				mobileResponse.message = "No associated account with given mobile number : ${mobile}. Cannot verify it."
				mobileResponse.total=0
				mobileResponse.success=false
			}
		}
		else {
			mobileResponse.total=0
			mobileResponse.success=false
			mobileResponse.message = "Invalid Input Json"
		}
		render mobileResponse as JSON
	}

	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "currentPassword": "P@ssw0rd", "newPassword" : "P@ssw0rd1", "confirmPassword" : "P@ssw0rd1" }' http://localhost:8080/app/mpassword
	 * @return
	 */
	def changePassword() {
		MobileResponse mobileResponse = new MobileResponse();
		def json = request.JSON
		def errors = null
		String currentPassword = json.currentPassword
		String pass = json.newPassword
		String passConfirm = json.confirmPassword
		def user = authenticatedUser
		if(user) {
			if(!currentPassword) {
				mobileResponse.message = "User attempting to change password but has not supplied current password"
			}
			else {
				def pwEnc = new Sha256Hash(currentPassword)
				def crypt = pwEnc.toHex()
				if(!crypt.equals(user.passwordHash)) {
					mobileResponse.message = "Wrong supplied password"
				}
				else {
					user.pass = pass
					user.passConfirm = passConfirm		
					if(user.validate() && userService.validatePass(user, true)) {
						userService.changePassword(user)
						mobileResponse.message = "Password changed sucessfully"
						mobileResponse.success = true;
					}
					else {
						errors = user.errors
						def errorMessage = ""
						errors.allErrors.each {
							errorMessage = errorMessage + g.message(code: it.getCodes()[0], args: []) + "</br>"
						}
						mobileResponse.message = errorMessage
					}
				}
			}
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot change password"
		}
		render mobileResponse as JSON
	}

	/**
	 * curl -X POST -H "Content-Type: application/json" -d '{ "email": "rajan@racloop.com" }' http://localhost:8080/app/mforgot
	 * @return
	 */
	def forgotPassword() {
		MobileResponse mobileResponse = new MobileResponse();
		def json = request.JSON
		def errors = null
		String email = json.email
		if(email) {
			def profile = ProfileBase.findByEmail(email)
			if (profile) {
				def user = profile.owner
				if (user.external || user.federated) {
					log.info("User identified by [$user.id]$user.username is external or federated")					
					
				}
				else{
					userService.setRandomPassword(user)
		
					if(nimbleConfig.messaging.enabled) {
						log.info("Sending account password reset SMS to $user.profile.mobile. Publishing to messaging queue")
						def  messageMap =[(Constant.MOBILE_KEY):user.profile.mobile, (Constant.NEW_PASSWORD_KEY):user.pass]
						jmsService.send(queue: Constant.NOTIFICATION_MOBILE_FORGOT_PASSWORD_QUEUE, messageMap)
						
						/*sendMail {
							to user.profile.email
							from grailsApplication.config.grails.messaging.mail.from
							subject nimbleConfig.messaging.passwordreset.subject
							html g.render(template: "/templates/nimble/mail/forgottenpassword_email", model: [user: user, baseUrl: grailsLinkGenerator.serverBaseURL]).toString()
						}*/
						mobileResponse.success = true
						mobileResponse.message = "Password retrieve successfully. Please check your SMS"
						
					}
					else {
						log.info "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/forgottenpassword_email", model: [user: user]).toString()}"
					}
		
				}
			}
			else {
				mobileResponse.message = "Invalid email id"
			}
		}
		else {
			mobileResponse.message = "Invalid Input Json"
		}
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
		}
		render mobileResponse as JSON
	}
	
	/**
	 * TODO changes for sms verification pending
	 * TODO emergency contacts functionality pending
	 * @return
	 */
	def editProfile() {
		def json = request.JSON
		def mobileResponse = new MobileResponse()
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
			//user.profile.email = email
			user.profile.mobile = mobile
			user.profile.isMale = isMale
			if (user.validate()) {
				def updatedUser = userService.updateUser(user)
				mobileResponse.success = true
				mobileResponse.message = "Profile updated successfully"
			}
			else {
				errors = user.errors
				mobileResponse.success = false
				def errorMessage = ""
				errors.allErrors.each {
					errorMessage = errorMessage + g.message(code: it.getCodes()[0], args: []) + "</br>"
				}
				mobileResponse.message = errorMessage
			}
		}
		else {
			mobileResponse.success = false
			mobileResponse.message = "User is not logged in. Cannot change user profile"
		}
		
		render mobileResponse as JSON
	}
	
	def search() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def mobileResponse = new MobileResponse()
		def errors = null
		def searchResultMap = null
		JourneyRequestCommand currentJourney = convertJsonToJourneyObject(json)
//		if(chainModel && chainModel.currentJourney) {
//			currentJourney = chainModel.currentJourney
//		}
		JourneyRequestCommand currentJourneyFromRequest = request.getAttribute('currentJourney')
		if(currentJourneyFromRequest) {
			currentJourney = currentJourneyFromRequest
		}
		def currentUser = getAuthenticatedUser();
		if(!currentUser) {
			currentUser = User.findByUsername(currentJourney.user);
		}
		if(currentUser) {
			setUserInformation(currentUser,currentJourney)
			currentJourney.ip = request.remoteAddr
			boolean isValidDate = setDates(currentJourney)
			if(!isValidDate){
				mobileResponse.success = false
				mobileResponse.message = "Invalid Date"
			}
			else {
				if(currentJourney.dateOfJourney && currentJourney.validStartTime && currentJourney.dateOfJourney.after(currentJourney.validStartTime)) {
					if(currentJourney.validate()) {
						session.currentJourney = currentJourney
						boolean shouldSearchJourneys = true
						if(currentUser && currentJourney.isNewJourney()) {//Usually it will be the case for Mobile search
							JourneyRequestCommand existingJourney = journeyService.searchPossibleExistingJourneyForUser(currentUser, currentJourney)
							if(existingJourney) {
								mobileResponse.data = ['existingJourney':existingJourney,'currentJourney':currentJourney] 
								mobileResponse.success = true
								mobileResponse.total = 0
								mobileResponse.message = "Existing journey found!"
								mobileResponse.existingJourney=true
								
								shouldSearchJourneys = false
							}
						}
						if(shouldSearchJourneys){
							searchResultMap = journeyService.getSearchResults(currentUser, currentJourney)
							session.currentJourney = currentJourney
							mobileResponse.data = searchResultMap
							mobileResponse.success = true
							mobileResponse.total = searchResultMap.numberOfRecords
							mobileResponse.existingJourney=false
						}
					}
				}
				else {
					mobileResponse.message = "Invalid travel date and time"
					mobileResponse.success = false
					mobileResponse.total =0
				}	
			}
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
		
		
		render mobileResponse as JSON
    }

	def searchAgain() {
		def json = request.JSON
		def journeyId = json?.journeyId
		def mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		if(!currentUser) {
			currentUser = User.findByUsername(currentJourney.user);
		}
		if(currentUser) {
			def indexName = ElasticSearchService.JOURNEY //params.indexName
			def currentJourney = journeyService.findJourneyById(journeyId, indexName)
			def searchResultMap = journeyService.getSearchResults(currentUser, currentJourney)
			mobileResponse.data = searchResultMap
			mobileResponse.success = true
			mobileResponse.total = searchResultMap.numberOfRecords
			mobileResponse.existingJourney=false
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
		render mobileResponse as JSON
	}
	
	def addJourney() {
		def user = getAuthenticatedUser()
		def json = request.JSON
		JourneyRequestCommand journeyRequestCommand = session.currentJourney
		if(!user) {
			user = User.findByUsername(journeyRequestCommand.user);
		}
		journeyManagerService.createJourney(user, journeyRequestCommand)
		MobileResponse mobileResponse = getMobileResoponse(journeyRequestCommand)
		render mobileResponse as JSON
	}
	
	def deleteJourney() {
		def json = request.JSON
		def journeyId = json?.journeyId
		journeyManagerService.deleteJourney(journeyId)
		MobileResponse mobileResponse = new MobileResponse()
		mobileResponse.message = "Journey Deleted Successfully"
		mobileResponse.success = true
		mobileResponse.total = 0
		render mobileResponse as JSON
	}
	
//	curl -X POST -H "Content-Type: application/json" -d '{"user":"admin","name":"Administrator"}' http://localhost:8080/app/mobile/myJourneys
	
	def myJourneys() {			
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		JourneyRequestCommand journeyRequestCommand = new JourneyRequestCommand()
		journeyRequestCommand.user = json?.user

		def workflows =[]
		def journeys =[]
		int numberOfRecords = 0		
		def currentUser = getAuthenticatedUser();
		if(!currentUser) {
			currentUser = User.findByUsername(journeyRequestCommand.user);
		}		
		if(currentUser) {
			journeys = journeyService.findAllActiveJourneyDetailsForUser(currentUser)
			mobileResponse.data = journeys
			mobileResponse.success = true
			mobileResponse.total = journeys?.size()
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
		log.info "My Journeys size : ${mobileResponse.total}"	
		render mobileResponse as JSON
	}
	
//	curl -X POST -H "Content-Type: application/json" -d '{"user":"admin","name":"Administrator","myJourneyId":"89","matchedJourneyId":"12de4899-74af-451a-a5ad-f3a8bfd1f678","isDummy":true}' http://localhost:8080/app/mobile/requestService
	
	def myCurrentJourney() {
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		JourneyRequestCommand journeyRequestCommand = new JourneyRequestCommand()
		journeyRequestCommand.user = json?.user

		def workflows =[]
		def journeys =[]
		int numberOfRecords = 0
		def currentUser = getAuthenticatedUser();
		if(!currentUser) {
			currentUser = User.findByUsername(journeyRequestCommand.user);
		}
		if(currentUser) {
			journeys = journeyService.findCurrentJourneyForUser(currentUser)
			mobileResponse.data = journeys
			mobileResponse.success = true
			mobileResponse.total = journeys?.size()
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
		render mobileResponse as JSON
	}
	
	def requestService() {
		def json = request.JSON
		def mobileResponse = new MobileResponse()
		String jsonMessage = null
		def currentJourney = null
		String jsonResponse = "error"
		def errors = null
		String myJourneyId=json?.myJourneyId
		String user=json?.user
		boolean isDummy =json?.isDummy?.toBoolean()
		
		def matchedJourneyId = json?.matchedJourneyId		
		def currentUser = getAuthenticatedUser()
		if(!currentUser) {
			currentUser = User.findByUsername(user);
		}
		if(currentUser) {
			if(!myJourneyId) {
				currentJourney = session.currentJourney
			} 
			else {
				currentJourney= journeyService.findJourneyById(myJourneyId, false)
			}
			def matchedJourney = journeyService.findJourneyById(matchedJourneyId, isDummy)
			
			def workflow = journeyManagerService.saveJourneyAndInitiateWorkflow(currentJourney,matchedJourney)
			mobileResponse.data = currentJourney
			mobileResponse.message="Request is successful"
			mobileResponse.success = true
			mobileResponse.total = 0
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
		render mobileResponse as JSON
//		chain(action: 'search', model: [currentJourney: currentJourney])
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
		def matchedJourney = journeyService.findMatchedJourneyById(matchedJourneyId, isDummy)
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
		def mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser()
		def workflowId = json?.workflowId
		
		if(currentUser) {
			journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.ACCEPTED.state)
			mobileResponse.message = "Request is Successfully Accepted"
			mobileResponse.success = true
			mobileResponse.total =0	
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
		
	   render mobileResponse as JSON
		
	}

	// curl -X POST -H "Content-Type: application/json" -d '{"myJourneyId":"89","workflowId":"43d27623-45cb-4201-aba3-cac07ea03f41","user":"sample.rider"}' http://localhost:8080/app/mobile/rejectResponse
	
	
	def rejectResponse() {
		def json = request.JSON
		def mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser()
		def workflowId = json?.workflowId
		if(currentUser) {
			journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.REJECTED.state)
			mobileResponse.message = "Request is Successfully Rejected"
			mobileResponse.success = true
			mobileResponse.total =0	
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
	   render mobileResponse as JSON

	}
	
	def cancelResponse() { 
		def json = request.JSON
		def mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser()
		def workflowId = json?.workflowId
		if(currentUser) {
		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.CANCELLED_BY_REQUESTER.state)
		mobileResponse.message = "Request is Successfully Cancelled"
		mobileResponse.success = true
		mobileResponse.total =0
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
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
	
	
	private setDates(JourneyRequestCommand currentJourney) {
		boolean success = true
		try {
			if(currentJourney.dateOfJourneyString) {
				currentJourney.dateOfJourney = convertUIDateToElasticSearchDate(currentJourney.dateOfJourneyString).toDate()
			}
			if(currentJourney.validStartTimeString) {
				currentJourney.validStartTime = new DateTime().toDate()//convertUIDateToElasticSearchDate(currentJourney.validStartTimeString).toDate()
			}
			if(!currentJourney.validStartTime) {
				DateTime currentDate = new DateTime()
				currentDate.plusMinutes(Integer.valueOf(grailsApplication.config.grails.approx.time.to.match))
				currentJourney.validStartTime = currentDate.toDate()
			}
			
		}
		catch(IllegalArgumentException e) {
			success = false
			log.error "Something went wrong while setting Dates", e
		}
		finally {
			return success
		}	
	}
	
	private Double convertToDouble(Object input) {
		if(input){
			 return Double.valueOf(input)
		}
		return null
	}
	
	def searchWithExistingJourney() {
		def json = request.JSON
		println json.inspect()
		def mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser()
		def existingJourneyId = json.existingJourneyId
		def newJourney = json?.searchWithNewJourney
		def currentJourney
		if("newJourney".equals(newJourney)) {
			println("searchWithExistingJourney : " + newJourney)
			journeyManagerService.deleteJourney(existingJourneyId)
			currentJourney = convertJsonToJourneyObject(json)
			setDates(currentJourney);
			journeyManagerService.createJourney(currentUser, currentJourney)
		}
		else {
			println("searchWithExistingJourney : " + newJourney)
			currentJourney = journeyService.findJourneyById(existingJourneyId, false)
		}
//		chain(action: 'search', model: [currentJourney: currentJourney])
		forward action: 'search', model: [currentJourney: currentJourney]
	}
	
	
	private def convertJsonToJourneyObject(def json) {
		JourneyRequestCommand currentJourney = new JourneyRequestCommand()
		currentJourney.dateOfJourneyString = json?.dateOfJourneyString
		currentJourney.validStartTimeString = json?.validStartTimeString
		currentJourney.fromPlace = json?.fromPlace
		currentJourney.fromLatitude = convertToDouble(json?.fromLatitude)
		currentJourney.fromLongitude = convertToDouble(json?.fromLongitude)
		currentJourney.toPlace = json?.toPlace
		currentJourney.toLatitude = convertToDouble(json?.toLatitude)
		currentJourney.toLongitude = convertToDouble(json?.toLongitude)
		currentJourney.isDriver = json?.isDriver?.toBoolean()
		currentJourney.tripDistance = convertToDouble(json?.tripDistance)
		currentJourney.tripUnit = json?.tripUnit;
		currentJourney.ip = request.remoteAddr;
		currentJourney.user = json?.user
		currentJourney.id = json?.id		
		return currentJourney
	}
	
	def privacy() {
		def mobileResponse = new MobileResponse()
		def html = StaticData.findByStaticDataKey("privacy").pageData;
		mobileResponse.message = html 
		mobileResponse.success = true
		mobileResponse.total =0
		render mobileResponse as JSON
	}
	
	def terms() {
		def mobileResponse = new MobileResponse()
		def html = StaticData.findByStaticDataKey("terms").pageData;
		mobileResponse.message = html
		mobileResponse.success = true
		mobileResponse.total =0
		render mobileResponse as JSON
	}
	
	def saveEmergencyContacts() {
		def json = request.JSON
		def mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser()
		if(currentUser != null) {
			Profile profile = currentUser.profile
			profile.emergencyContactOne = json.contactOne
			profile.emergencyContactTwo = json.contactTwo
			profile.save();
			mobileResponse.success = true
			mobileResponse.message = "Emergency contacts saved successfully"
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot save the emergency contacts"
		}
	   render mobileResponse as JSON
	}
	
	/**
	 * 
	 * @return
	 */
	def sos() {
		def json = request.JSON
		def mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser()
		if(currentUser == null) {
			log.info("Current user not in session : " + json.email)
			def email = json.email
			Profile profile = Profile.findByEmail(email);
			if(profile)
				currentUser = profile.owner
		}
		if(currentUser) {
			def currentDateString = json.currentDateString
			DateTime currentDate = convertUIDateToElasticSearchDate(currentDateString)
			def journeys = journeyService.findCurrentJourneyForUser(currentUser, currentDate)
			def journeyIds = null
			journeys.each { journey ->
				journeyIds = journeyIds + "~" + journey.id
			}
			def emergencyContactOne = null
			def emergencyContactTwo = null
			if(currentUser?.profile?.emergencyContactOne) {
				emergencyContactOne = currentUser?.profile?.emergencyContactOne
			}
			else {
				log.info("Emergency contact one not present")
			}
			if(currentUser?.profile?.emergencyContactTwo) {
				emergencyContactTwo = currentUser?.profile?.emergencyContactTwo
			}
			else {
				log.info("Emergency contact two not present")
			}
			
			def  messageMap =[
				(Constant.EMAIL_KEY) : currentUser?.profile?.email,
				(Constant.MOBILE_KEY) : currentUser?.profile?.mobile,
				(Constant.SOS_NAME_KEY) : currentUser?.profile?.fullName,
				(Constant.EMERGENCY_CONTACT_ONE_KEY) : emergencyContactOne,
				(Constant.EMERGENCY_CONTACT_TWO_KEY) : emergencyContactTwo,
				(Constant.JOURNEY_IDS_KEY) : journeyIds,
				(Constant.SOS_USER_LATITUDE) : json.lat,
				(Constant.SOS_USER_LONGITUDE): json.lng
			]
			
			Sos sos = new Sos();
			sos.userName = currentUser?.profile?.email
			sos.mobile = currentUser?.profile?.mobile
			sos.latitude = json.lat
			sos.longitude = json.lng
			sos.sosTimestamp = currentDate.toDate()
			sos.journeyIds = journeyIds
			sos.save();
			jmsService.send(queue: Constant.MOBILE_SOS_QUEUE, messageMap)
			log.info("SMS parameters : " + messageMap)
			mobileResponse.message = "Sos registered Successfully" 
			mobileResponse.success = true
		}
		else {
			mobileResponse.message = "Sos not registered Successfully. Email not found " +  json.email// TODO implementation incomplete and hard coded
			mobileResponse.success = false
		}
		render mobileResponse as JSON
	}
	
	def sosCancel() {
		
	}
	
	def logCurrentLocation(String journeyId, String currentTime, double lat, double lng) {
		//check if current location is close to destination
		//return status
	}
	
	def stopLogging() {
		
	}
	
	def history() {
		
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		JourneyRequestCommand journeyRequestCommand = new JourneyRequestCommand()
		journeyRequestCommand.user = json?.user

		def workflows =[]
		def journeys =[]
		int numberOfRecords = 0
		def currentUser = getAuthenticatedUser();
		if(!currentUser) {
			currentUser = User.findByUsername(journeyRequestCommand.user);
		}
		if(currentUser) {
			journeys = journeyService.findHistoricJourneyDetailsForUser(currentUser)
			mobileResponse.data = journeys
			mobileResponse.success = true
			mobileResponse.total = journeys?.size()
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
		render mobileResponse as JSON
		
	}
}
