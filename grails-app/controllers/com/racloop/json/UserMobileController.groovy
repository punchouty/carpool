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

import com.racloop.Constant
import com.racloop.GenericStatus
import com.racloop.GenericUtil
import com.racloop.Profile
import com.racloop.Review
import com.racloop.Sos
import com.racloop.User
import com.racloop.domain.Journey
import com.racloop.mobile.data.response.MobileResponse
import com.racloop.staticdata.StaticData

class UserMobileController {
	
	def shiroSecurityManager
	def userService
	def userDataService
	def journeyDataService
	def searchService
	def userManagerService
	def journeyManagerService
	def journeyService
	def journeyWorkflowService
	def jmsService
	def testDataService
	def userReviewService
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
		log.info("login() json : ${json}");
		def jsonResponse = null
		def mobileResponse = new MobileResponse()
		if(json) {
			def email = json.email
			def password = json.password
			if(email == null || password == null) {
				mobileResponse.message="Empty Username/Password combination"
				mobileResponse.success=false
			}
			else {
				def rememberMe = json.rememberMe
				def currentDateString = json.currentDateString
				def currentDate = GenericUtil.uiDateStringToJavaDateForSearch(currentDateString);
				def authToken = new UsernamePasswordToken(email, password);
				rememberMe = true;
				if (rememberMe) {
					authToken.rememberMe = true
				}
				try {
					//TODO - do we need this event mechanism below. See AuthController
					SecurityUtils.subject.login(authToken)
					userDataService.createLoginRecord(request)
					authenticatedUser.pass = password //TODO need to remove storing of password. Potential security threat
					mobileResponse.data = authenticatedUser
					if(authenticatedUser.journeyIdForReview != null) {
						Journey journeyForReview = userReviewService.findJourneysToBeReviewedForAUser(authenticatedUser)
						mobileResponse.currentJourney = journeyForReview//currentJourney.convert();
						mobileResponse.feedbackPending = true
						mobileResponse.success = true
					}
					else {
						Journey currentJourney = journeyDataService.findCurrentJourney(authenticatedUser.profile.mobile, new DateTime(currentDate))
						if(currentJourney != null) mobileResponse.currentJourney = currentJourney.convert();
						mobileResponse.success = true
					}
					
				}
				catch (IncorrectCredentialsException e) {
					log.info "Credentials failure for user '${email}'."
					mobileResponse.message=message(code: "nimble.login.failed.credentials");
					mobileResponse.success=false
				}
				catch (DisabledAccountException e) {
					log.info "Attempt to login to disabled account for user '${email}'."
					mobileResponse.message=message(code: "nimble.login.failed.disabled");
					mobileResponse.success=false
				}
				catch (AuthenticationException e) {
					log.info "General authentication failure for user '${email}'."
					mobileResponse.message=message(code: "nimble.login.failed.general");
					mobileResponse.success=false
				}
			}
		}
		else {
			mobileResponse.message="Invalid input JSON request"
			mobileResponse.success=false
			
		}
		render mobileResponse as JSON
		
	}
	
	private Journey getJourneyForFeedback() {
		boolean test = false;
		if(test) {
			Journey j1 = new Journey();
			j1.id = "100";
			j1.name = "Parminder"
			j1.from = "Delhi"
			j1.to = "CHandigarh"
			
			Journey j2 = new Journey();
			j2.id = "101"
			j2.name = "Sahil"
			j1.getRelatedJourneys().add(j2)
			
			Journey j3 = new Journey();
			j3.id = "102"
			j3.name = "Sahil"
			j1.getRelatedJourneys().add(j3)
			//return null
			return j1;
		}
		else {
			return null
		}
	}
	
	def sendUserRating() {
		def json = request.JSON
		log.info("sendUserRating() json : ${json}");
		MobileResponse mobileResponse = new MobileResponse();
		def currentUser = getAuthenticatedUser();
		if(currentUser){
			String journeyId = json.journeyId
			String pairId = json.pairId1
			String comments = json.comment1
			String punctualty = json.punchuality1
			String overall=json.overall1
			Review review = new Review(journeyId:journeyId,comment:comments,punctualty:getActualRating(punctualty), overall:getActualRating(overall))
			userReviewService.saveUserRating(currentUser, review, pairId)
			if(json.pairId2){
				pairId = json.pairId2
				comments = json.comment2
				punctualty = json.punchuality2
				overall=json.overall2
				Review review2 = new Review(journeyId:journeyId,comment:comments,punctualty:getActualRating(punctualty), overall:getActualRating(overall))
				userReviewService.saveUserRating(currentUser, review2, pairId)
			}
			def currentDateString = json.currentDateString
			def currentDate = GenericUtil.uiDateStringToJavaDateForSearch(currentDateString);
			Journey currentJourney = journeyDataService.findCurrentJourney(currentUser.profile.mobile, new DateTime(currentDate))
			if(currentJourney != null) mobileResponse.currentJourney = currentJourney.convert();
			mobileResponse.success = true
			mobileResponse.message = "User review saved"
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to save user feedback"
			mobileResponse.success = false
		}
		
		render mobileResponse as JSON
	}
	
	private Double getActualRating (String rating){
		Double actualRating = null
		if(rating){
			actualRating = new Double(rating) + 1.0d
		}
		return actualRating
	}
	
	def cancelUserRating() {
		def json = request.JSON
		log.info("cancelUserRating() json : ${json}");
		MobileResponse mobileResponse = new MobileResponse();
		def currentUser = getAuthenticatedUser();
		if(currentUser){
			userReviewService.clearUserForReview(currentUser)
			def currentDateString = json.currentDateString
			def currentDate = GenericUtil.uiDateStringToJavaDateForSearch(currentDateString);
			Journey currentJourney = journeyDataService.findCurrentJourney(currentUser.profile.mobile, new DateTime(currentDate))
			if(currentJourney != null) mobileResponse.currentJourney = currentJourney.convert();
			mobileResponse.message = "User review cancelled"
			mobileResponse.success = true
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to cancel user feedback"
			mobileResponse.success = false
		}
		
		render mobileResponse as JSON
	}
	
	def getCurrentJourney() {
		def json = request.JSON
		log.info("getCurrentJourney() json : ${json}");
		def mobileResponse = new MobileResponse();
		def currentUser = getAuthenticatedUser();
		def currentDateString = json.currentDateString
		def currentDate = GenericUtil.uiDateStringToJavaDateForSearch(currentDateString);
		if(currentUser) {
			Journey currentJourney = journeyDataService.findCurrentJourney(currentUser.profile.mobile, new DateTime(currentDate))
			if(currentJourney != null) mobileResponse.currentJourney = currentJourney.convert();
			mobileResponse.success = true
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform save"
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
		log.info("signup() json : ${json}");
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
			if(json?.facebookId) {
				user.pass = Constant.DEFAULT_PASSWORD
				user.passConfirm = Constant.DEFAULT_PASSWORD
			}
			else {
				user.pass = json?.password
				user.passConfirm = json?.passwordConfirm
			}
			user.profile.fullName = json?.fullName
			user.profile.email = json?.email
			user.profile.mobile = json?.mobile
			user.profile.isMale = isMale
			user.enabled = nimbleConfig.localusers.provision.active
			user.external = false
			user.facebookId = json?.facebookId
			
			
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
						//Saving user in DynamoDB
						/*RacloopUser racloopUser = new RacloopUser();
						racloopUser.setMobile(savedUser.profile.mobile);
						racloopUser.setEmail(savedUser.profile.email);
						racloopUser.setFullName(savedUser.profile.fullName);
						racloopUser.setEmailHash(savedUser.profile.emailHash);
						//TODO take care for failure scenario
						userDataService.saveUser(racloopUser);*/
						log.info("Sending verification code to $user.profile.mobile");
						userManagerService.setUpMobileVerificationDuringSignUp(savedUser.profile)
						mobileResponse.success=true
						mobileResponse.data = savedUser
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
					mobileResponse.message = "Incorrect old password"
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
						mobileResponse.message = "Password sent successfully to your mobile number ending with ${user.profile.mobile[-4..-1]}. Please check your SMS"
						
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
			String oldMobile = user.profile.mobile
			user.profile.fullName = fullName
			user.profile.email = email
			user.profile.mobile = mobile
			user.profile.isMale = isMale
			if (user.validate()) {
				def updatedUser = userService.updateUser(user)
				/*RacloopUser racloopUser = userDataService.findUserByMobile(oldMobile)
				racloopUser.setMobile(user.profile.mobile);
				racloopUser.setEmail(user.profile.email);
				racloopUser.setFullName(user.profile.fullName);
				racloopUser.setEmailHash(user.profile.emailHash);
				//TODO take care for failure scenario
				userDataService.saveUser(racloopUser);*/
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
	
	def savePreferences() {
		def json = request.JSON
		def mobileResponse = new MobileResponse()
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		String contactOne = json?.contactOne
		String contactTwo = json?.contactTwo
		String travelModePreference = json?.travelModePreference
		String paymentPreference = json?.paymentPreference
		String cabServicePreference = json?.cabServicePreference
		
		def user = getAuthenticatedUser()
		if(user) {
			user.profile.emergencyContactOne = contactOne
			user.profile.emergencyContactTwo = contactTwo
			user.profile.travelModePreference=travelModePreference
			user.profile.paymentPreference= paymentPreference
			user.profile.cabPreference= cabServicePreference
			if (user.validate()) {
				def updatedUser = userService.updateUser(user)
				/*RacloopUser racloopUser = userDataService.findUserByMobile(oldMobile)
				racloopUser.setMobile(user.profile.mobile);
				racloopUser.setEmail(user.profile.email);
				racloopUser.setFullName(user.profile.fullName);
				racloopUser.setEmailHash(user.profile.emailHash);
				//TODO take care for failure scenario
				userDataService.saveUser(racloopUser);*/
				mobileResponse.success = true
				mobileResponse.message = "Preferences updated successfully"
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
			mobileResponse.message = "User is not logged in. Cannot change user preferences"
		}
		
		render mobileResponse as JSON
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
			Date date = GenericUtil.uiDateStringToJavaDate(currentDateString);
			DateTime currentDate = new DateTime(date)
			Journey journey = journeyDataService.findCurrentJourney(currentUser.profile.mobile, currentDate);
			
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
				(Constant.JOURNEY_IDS_KEY) : journey?.id,
				(Constant.SOS_USER_LATITUDE) : json.lat,
				(Constant.SOS_USER_LONGITUDE): json.lng
			]
			
			Sos sos = new Sos();
			sos.userName = currentUser?.profile?.email
			sos.mobile = currentUser?.profile?.mobile
			sos.latitude = json.lat
			sos.longitude = json.lng
			sos.sosTimestamp = currentDate.toDate()
			sos.journeyIds = journey?.id
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

	private getNimbleConfig() {
		grailsApplication.config.nimble
	}
	
	def loginFromFacebook(){
		def json = request.JSON
		log.info("loginFromFacebook() json : ${json}");
		def jsonResponse = null
		MobileResponse mobileResponse = new MobileResponse()
		
		if(json) {
			def email = json.email
			def facebookId = json.facebookId
			def gender = json.gender
			def name = json.name
			//Check if user exists. If yes then login
			User user = User.findByUsername(email)
			if(user){
				userManagerService.updateUserDetailsIfRequired(user, facebookId)
				user.pass = Constant.DEFAULT_PASSWORD
				mobileResponse.data = user
				mobileResponse.success = true
			}
			else {
				mobileResponse.data = null
				mobileResponse.success = true
			}
		}
		
		render mobileResponse as JSON
	}
		
}
