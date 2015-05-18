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
import com.racloop.domain.Journey;
import com.racloop.domain.RacloopUser;
import com.racloop.journey.workkflow.WorkflowState
import com.racloop.mobile.data.response.MobileResponse
import com.racloop.staticdata.StaticData;
import com.racloop.util.date.DateUtil

import static com.racloop.util.date.DateUtil.convertUIDateToElasticSearchDate

class UserMobileController {
	
	def shiroSecurityManager
	def userService
	def userDataService
	def journeyDataService
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
			def authToken = new UsernamePasswordToken(email, password, true)			
//			if (rememberMe) {
//				authToken.rememberMe = true
//			}
			try {				
				//TODO - do we need this event mechanism below. See AuthController
				SecurityUtils.subject.login(authToken)
				userService.createLoginRecord(request)
				authenticatedUser.pass = password //TODO need to remove storing of password. Potential security threat
				mobileResponse.data=authenticatedUser
				Journey currentJourney = journeyDataService.findCurrentJourney(authenticatedUser.profile.mobile, currentDate)
				if(currentJourney != null) mobileResponse.currentJourney = currentJourney.convert();
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
						//Saving user in DynamoDB
						RacloopUser racloopUser = new RacloopUser();
						racloopUser.setMobile(profile.mobile);
						racloopUser.setEmail(profile.email);
						racloopUser.setFullName(profile.fullName);
						racloopUser.setEmailHash(profile.emailHash);
						//TODO take care for failure scenario
						userDataService.saveUser(racloopUser);
						log.info("Sending verification code to $user.profile.mobile");
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
			user.profile.email = email
			user.profile.mobile = mobile
			user.profile.isMale = isMale
			if (user.validate()) {
				def updatedUser = userService.updateUser(user)
				RacloopUser racloopUser = new RacloopUser();
				racloopUser.setMobile(user.profile.mobile);
				racloopUser.setEmail(user.profile.email);
				racloopUser.setFullName(user.profile.fullName);
				racloopUser.setEmailHash(user.profile.emailHash);
				//TODO take care for failure scenario
				userDataService.saveUser(racloopUser);
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
}
