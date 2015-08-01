package com.racloop

import grails.converters.JSON
import grails.plugin.facebooksdk.FacebookContext
import grails.plugin.facebooksdk.FacebookGraphClient
import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.AuthController
import grails.plugin.nimble.core.ProfileBase
import grails.plugin.nimble.core.UserBase

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.DisabledAccountException
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha256Hash
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import com.racloop.mobile.data.response.MobileResponse
import com.restfb.exception.FacebookOAuthException

class UserSessionController {

	def userService
	def recaptchaService
	LinkGenerator grailsLinkGenerator
	//def emailService
	def userManagerService
	FacebookContext facebookContext
	def jmsService
	def userDataService

	def search() {
		
		def user = getRacloopAuthenticatedUser();//dynamic method added in bootstrap
		JourneyRequestCommand commandInstance = new JourneyRequestCommand();
		JourneyRequestCommand journeyFromRequest = request.getAttribute('journeyInstance')
		if(journeyFromRequest) {
			commandInstance = journeyFromRequest
		}
		if(user) {
//			def criteria = TravelHistory.createCriteria();
//			def historyResults = criteria {
//				eq("user", user)
//				maxResults(7)
//				and {
//					order('searchCount', 'desc')
//					order('lastUpdatedAt', 'desc')
//				}
//			}
			def historyResults = []
			return [commandInstance: commandInstance, history : historyResults]
		}
		else {			
			[commandInstance: commandInstance]
		}
	}

	def signin(String targetUri, String username, String rememberMe) {
		def local = nimbleConfig.localusers.authentication.enabled
		def registration = nimbleConfig.localusers.registration.enabled

		if(targetUri) {
			session.setAttribute(AuthController.TARGET, targetUri)
		}

		model: [local: local, registration: registration, username: username, rememberMe: (rememberMe != null), targetUri: targetUri, facebookContext:facebookContext]
	}
	
	def login(String username, String password, String rememberme) {
		if(!username || !password) {
			log.info "User name or password not there'."
			
			flash.type = 'error'
			flash.message = "User name and password are required field"
			redirect(action: 'signin')
			return
		}
		def authToken = new UsernamePasswordToken(username, password)

		if (rememberme) {
			authToken.rememberMe = true
		}

		log.info("Attempting to authenticate user, $username. RememberMe is $authToken.rememberMe")

		try {
			SecurityUtils.subject.login(authToken)
			userDataService.createLoginRecord(request)

			def targetUri = session.getAttribute(AuthController.TARGET) ?: nimbleConfig.localusers.authentication.postLoginUrl
			session.removeAttribute(AuthController.TARGET)
			log.info("Directing to content $targetUri")
			redirect(uri: targetUri)
			return
		}
		catch (IncorrectCredentialsException e) {
			log.info "Credentials failure for user '${username}'."
			log.debug(e)

			flash.type = 'error'
			flash.message = message(code: "nimble.login.failed.credentials")
		}
		catch (DisabledAccountException e) {
			log.info "Attempt to login to disabled account for user '${username}'."
			log.debug(e)

			flash.type = 'error'
			flash.message = message(code: "nimble.login.failed.disabled")
		}
		catch (AuthenticationException e) {
			log.info "General authentication failure for user '${username}'."
			log.debug(e)

			flash.type = 'error'
			flash.message = message(code: "nimble.login.failed.general")
		}
		redirect(action: 'signin')
		
	}
	
	def signinUsingFacebook() {
		def targetURL = params[AuthController.TARGET]
		def user
		if(isAuthenticatedFromFacebook()){
			user = getUserFromFacebook()
			if(user){
				userManagerService.updateUserDetailsIfRequired(user, facebookContext?.user?.id?.toString())
				if(targetURL && !"null".equalsIgnoreCase(targetURL)) {
					session.setAttribute(AuthController.TARGET, targetURL)
				}
				this.login(user.username, Constant.DEFAULT_PASSWORD, "true");
				return;
				
			}
			else {
				//captureMobile.gsp
				def fbUser = getFacebookUser()
				render(view:'captureMobile', model: [fullName : fbUser.name, email:fbUser.email,gender:fbUser.gender, facebookId:fbUser.id])
			}
		}
		else {
			redirect(action: "signin")
		}
	}

	def signup() {
		if (!nimbleConfig.localusers.registration.enabled) {
			log.warn("Account registration is not enabled for local users, skipping request")
			response.sendError(404)
			return
		}

		def user = InstanceGenerator.user(grailsApplication)
		user.profile = InstanceGenerator.profile(grailsApplication)

		log.debug("Starting new user creation")
		[user: user]
	}
	
	

	def saveuser() {
		if (!nimbleConfig.localusers.registration.enabled) {
			log.warn("Account registration is not enabled for local users, skipping request")
			response.sendError(404)
			return
		}
		def user = InstanceGenerator.user(grailsApplication)
		user.profile = InstanceGenerator.profile(grailsApplication)
		user.profile.owner = user
		Map userMap = request.getAttribute('userMap')
		if(userMap) {
			user.username = userMap.email
			user.pass = Constant.DEFAULT_PASSWORD
			user.passConfirm = Constant.DEFAULT_PASSWORD
			user.profile.fullName = userMap.fullName
			user.profile.email = userMap.email
			user.profile.mobile = userMap.mobile
			user.facebookId = userMap.facebookId
			def gender = userMap.gender 
			if(gender != 'male') {
				user.profile.isMale  = false
			}
			else {
				user.profile.isMale = true
			}
			
		}
		else {
			def sex = params['sex']
			boolean isMale = true
			if(sex != 'male') {
				isMale = false
			}
			user.properties['username', 'pass', 'passConfirm'] = params
			user.profile.properties['fullName', 'email', 'mobile', 'emergencyContact'] = params
			user.username = user.profile.email
			user.profile.isMale = isMale
			
		}
		user.enabled = nimbleConfig.localusers.provision.active
		user.external = false
		user.userCode = userManagerService.generateUserCode(user.profile.fullName)
		user.validate()

		log.debug("Attempting to create new user account identified as $user.username")

		// Enforce username restrictions on local accounts, letters + numbers only
		if (user.username == null || user.username.length() < nimbleConfig.localusers.usernames.minlength) {
			log.debug("Supplied username of $user.username does not meet requirements for local account usernames")
			user.errors.rejectValue('username', 'nimble.user.username.invalid')
		}

		// Enforce email address for account registrations
		if (user.profile.email == null || user.profile.email.length() == 0) {
			user.profile.email = 'invalid'
		}
		
		if (user.profile.mobile == user.profile.emergencyContactOne || user.profile.mobile == user.profile.emergencyContactTwo) {
			flash.type = "message"
			flash.message = "Mobile Number and Emergency Contact cannot be same"
			render(view: 'signup', model: [user: user])
			return
		}

		if (user.hasErrors()) {
			log.debug("Submitted values for new user are invalid")
			user.errors.each { log.debug it }
			//Yuck code but seems to be a only way to get rid of username related error. Better way would involve changing the Nimble plugin
			def allErrors = user.errors.allErrors.findAll{ true }
			def userNameError = allErrors.findAll{ it.field == "username"}
			allErrors.removeAll(userNameError)
			user.clearErrors()
			allErrors.each {
					user.errors.addError(it)
			}

			resetNewUser(user)
			render(view: 'signup', model: [user: user])
			return
		}

		def savedUser
		def human = recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)

		if (human) {
			savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				log.debug("UserService returned invalid account details when attempting account creation")
				resetNewUser(user)
				render(view: 'signup', model: [user: user])
				return
			}
		}
		else {
			log.debug("Captcha entry was invalid for user account creation")
			resetNewUser(user)
			user.errors.reject('nimble.invalid.captcha')
			render(view: 'signup', model: [user: user])
			return
		}
		
		log.info("Sending verification code to $user.profile.mobile")
		userManagerService.setUpMobileVerificationDuringSignUp(savedUser.profile)

//		log.info("Sending account registration confirmation email to $user.profile.email with subject $nimbleConfig.messaging.registration.subject")

//		if(nimbleConfig.messaging.enabled) {
//			emailService.sendMail(user.profile.email, nimbleConfig.messaging.registration.subject, g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: savedUser]).toString(),grailsApplication.config.grails.messaging.mail.from )
//		}
//		else {
//			log.debug "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: user]).toString()}"
//		}

		log.info("Created new account identified as $user.username with internal id $savedUser.id")

		flash.type = "message"
		flash.message = "Account registered successfully. Please check your mobile for activation code on SMS."
		redirect (action: "verifyMobile", params: [mobile : savedUser.profile.mobile])
	}

	private void resetNewUser(user) {
		log.debug("New user creation failed, resetting user input to accepted state")

		if (user.profile?.email.equals('invalid')) {
			user.profile.email = ''
		}

		user.pass = ""
		user.passConfirm = ""
	}


	def forgotPassword() {
		
	}
	
	def verifyMobile() {
		[mobile : params['mobile']]
	}
	
	def processMobileVerification(String mobile, String verificationCode, String formAction) {
		def user = getRacloopAuthenticatedUser()
		if(formAction == 'verifyMobile') {
			def status = userManagerService.verify(mobile, verificationCode, user?.profile?.email)
			if(status == GenericStatus.SUCCESS) {
				flash.message = "Mobile verified successfully"
				if(isAuthenticatedFromFacebook()){
					redirect(action: "search")
					return
				}
				redirect(action: "signin")
				return
			}
			else if (status == GenericStatus.FAILURE) {
				flash.type = 'error'
				flash.message = "Invalid verification code. Please try again."
				redirect(action: "verifyMobile", params: [mobile: mobile])
				return
			}
			else {
				flash.type = 'error'
				flash.message = "No associated account with given mobile number."
				redirect(action: "verifyMobile", params: [mobile: mobile])
				return
			}
		}
		else {
			def status = userManagerService.setUpMobileVerification(mobile, user?.profile?.email)
			if(status == GenericStatus.SUCCESS) {
				flash.message = "SMS Sent successfully. Please check your mobile."
				redirect(action: "verifyMobile", params: [mobile: mobile])
				return
			}
			else {
				flash.type = 'error'
				flash.message = "No associated account with given mobile number : ${mobile}. Cannot verify it."
				redirect(action: "verifyMobile", params: [mobile: mobile])
				return
			}
		}
	}

	def forgotPasswordProcess(String email) {
		def profile = ProfileBase.findByEmail(email)
		if (!profile) {
			log.debug("User account for supplied email address $email was not found when attempting to process forgotten password")
			flash.type = "error"
			flash.message = message(code: 'nimble.user.forgottenpassword.noaccount')
			redirect(action: "forgotPassword")
			return
		}

		def user = profile.owner

		if (user.external || user.federated) {
			log.info("User identified by [$user.id]$user.username is external or federated")

			//log.info("Sending account password reset email to $user.profile.email with subject $nimbleConfig.messaging.passwordreset.external.subject")
			log.info("Sending account password reset sma to $user.profile.mobile")
			if(nimbleConfig.messaging.enabled) {
				def  messageMap =[(Constant.MOBILE_KEY):user.profile.mobile, (Constant.NEW_PASSWORD_KEY):user.pass]
				jmsService.send(queue: Constant.NOTIFICATION_MOBILE_FORGOT_PASSWORD_QUEUE, messageMap)
				/*sendMail {
					to user.profile.email
					from grailsApplication.config.grails.messaging.mail.from
					subject nimbleConfig.messaging.passwordreset.external.subject
					html g.render(template: "/templates/nimble/mail/forgottenpassword_external_email", model: [user: user, baseUrl: grailsLinkGenerator.serverBaseURL]).toString()
				}*/
			}
			else {
				log.debug "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/forgottenpassword_external_email", model: [user: user]).toString()}"
			}

			redirect(action: "forgotPasswordComplete", id: user.id)
			return
		}

		def human = recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)
		if (human) {

			userService.setRandomPassword(user)

			//log.info("Sending account password reset email to $user.profile.email with subject $nimbleConfig.messaging.passwordreset.subject")
			log.info("Sending account password reset SMS to $user.profile.mobile")
			if(nimbleConfig.messaging.enabled) {
				def  messageMap =[(Constant.MOBILE_KEY):user.profile.mobile, (Constant.NEW_PASSWORD_KEY):user.pass]
				jmsService.send(queue: Constant.NOTIFICATION_MOBILE_FORGOT_PASSWORD_QUEUE, messageMap)
				/*sendMail {
					to user.profile.email
					from grailsApplication.config.grails.messaging.mail.from
					subject nimbleConfig.messaging.passwordreset.subject
					html g.render(template: "/templates/nimble/mail/forgottenpassword_email", model: [user: user, baseUrl: grailsLinkGenerator.serverBaseURL]).toString()
				}*/
			}
			else {
				log.debug "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/forgottenpassword_email", model: [user: user]).toString()}"
			}

			log.info("Successful password reset for user identified as [$user.id]$user.username")

			//redirect(action: "forgotPasswordComplete", validFlow: true)
			flash.type = "message"
			flash.message = "Your new password has been sent to your registered mobile ending with ${user.profile.mobile[-4..-1]}."
			
			redirect(action: "search")
			return
		}

		log.debug("Captcha entry was invalid when attempting to process forgotten password for user identified by [$user.id]$user.username")
		flash.type = "error"
		flash.message = message(code: 'nimble.invalid.captcha')
		redirect(action: "forgotPassword")
	}
	
	def forgotPasswordComplete(boolean validFlow) {
		if(!validFlow) {
			redirect(action: "search")
		}
	}
	
	def changePassword(){
		[user:getRacloopAuthenticatedUser()]
	}

	def updatePassword(String currentPassword, String pass, String passConfirm) {
		def user = getRacloopAuthenticatedUser()

		if(!currentPassword) {
			log.warn("User [$user?.id]$user?.username attempting to change password but has not supplied current password")
			user.errors.reject('nimble.user.password.required')
			render (view:"changePassword", model:[user:user])
			return
		}

		def pwEnc = new Sha256Hash(currentPassword)
		def crypt = pwEnc.toHex()

		def human = recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)
		if (human) {

			if(!crypt.equals(user.passwordHash)) {
				log.warn("User [$user.id]$user.username attempting to change password but has supplied invalid current password")
				user.errors.rejectValue('pass', 'nimble.user.password.incorrect.current.pass')
				render (view:"changePassword", model:[user:user])
				return
			}

			user.pass = pass
			user.passConfirm = passConfirm

			if(user.validate() && userService.validatePass(user, true)) {
				userService.changePassword(user)
				if(!user.hasErrors()) {
					log.info("Changed password for user [$user.id]$user.username successfully")
					flash.type = "message"
					flash.message = "Password Changed Successfully"
					redirect (action: "search")
					return
				}
			}

			log.error("User [$user.id]$user.username password change was considered invalid")
			user.errors.allErrors.each { log.debug it }
			render (view:"changePassword", model:[user:user])
			return
		}

		log.debug("Captcha entry was invalid for user account creation")
		resetNewUser(user)
		user.errors.reject('nimble.invalid.captcha')
		render(view: 'changePassword', model: [user: user])
		return
	}
	
	def profile() {
		def user = getRacloopAuthenticatedUser()
		[user: user]
	}
	
	def editProfile() {
		def user = getAuthenticatedUser()
		//user  = User.findByUsername(user.username)
		def newMobile = params.mobile
		def oldMobile = user.profile.mobile
		def emergencyContactOne = params.emergencyContactOne
		def emergencyContactTwo = params.emergencyContactTwo
		boolean isMale = true
		if(params.sex != 'male') {
			isMale = false
		}
		user.profile.fullName = params.fullName
		user.profile.mobile = params.mobile
		user.profile.isMale = isMale
		user.profile.emergencyContactOne = emergencyContactOne
		user.profile.emergencyContactTwo = emergencyContactTwo
		user.profile.travelModePreference=params.travelModePreference
		user.profile.paymentPreference= params.paymentPreference
		user.profile.cabPreference= params.cabPreference
		if(!user.userCode) {
			user.userCode = userManagerService.generateUserCode(user.profile.fullName)
		}
		
		if(newMobile == emergencyContactOne || newMobile == emergencyContactTwo) {
			flash.type = "error"
			flash.message = "Emergency contact cannot be same as your mobile number"
			render view: 'profile', model: [user: user]
			return
		}
		
		if(newMobile != oldMobile) { //if mobile number is changed
			Profile profile = Profile.findByMobileAndEmail(newMobile, user.profile.email);
			if(profile) {
				flash.type = "error"
				flash.message = "New mobile number given is already in use"
				render view: 'profile', model: [user: user]
				return
			}
		}
		
		if (!user.validate()) {
			log.debug("Updated details for user [$user.id] $user.username are invalid")
			render view: 'profile', model: [user: user]
			return
		}

		def updatedUser = userService.updateUser(user)
		log.info("Successfully updated details for user [$user.id]$user.username")
		if(newMobile == oldMobile) {
			flash.type = "success"
			flash.message = message(code: 'nimble.user.update.success', args: [user.username])
			render view: 'profile', model: [user: updatedUser]
			return
		}
		else {
			userManagerService.setUpVerificationForMobileChange(newMobile, user.profile.email)
			SecurityUtils.subject?.logout()
			flash.type = "success"
			flash.message = "You have update your mobile number. Check SMS and verify your new mobile number"
			redirect action: 'verifyMobile', params: [mobile: newMobile]
			return;
		}
	}

	private getNimbleConfig() {
		grailsApplication.config.nimble
	}
	
	
	private boolean isAuthenticatedFromFacebook(){
		return facebookContext.app.id && facebookContext.authenticated
	}
	
	
	
	private User getUserFromFacebook() {
		User user
		def fbUser = getFacebookUser()
		if(fbUser) {
			user = UserBase.findByUsername(fbUser.email)
		}
		return user

	}
	
	private getFacebookUser() {
		def fbUser
		String token = facebookContext.user.token
		if (token) {
			/*if (facebookContext.authenticated && !facebookContext.user.tokenExpired) {
				// Exchange token to get an extended expiration time (60 days)
				log.info "Current token expiration time: " + new Date(facebookContext.user.tokenExpirationTime)
				facebookContext.user.exchangeToken()
				log.info "Exchanged token expiration time:  " + new Date(facebookContext.user.tokenExpirationTime)
			}*/
			FacebookGraphClient facebookGraphClient = new FacebookGraphClient(token)
			try {
				fbUser = facebookGraphClient.fetchObject(facebookContext.user.id.toString(),[fields:"id, name,email,gender"])

			} catch (FacebookOAuthException exception) {
				facebookContext.user.invalidate()
			}
		}

		return fbUser
	}
	
	def signout() {
		facebookContext.user.invalidate()
		facebookContext.setSignedRequest(null)
		SecurityUtils.subject?.logout()
		redirect(uri: '/')
	}
	
	def addMobile() {
		def userMap =[:]
		userMap.mobile = params.mobile
		userMap.fullName = params.fullName
		userMap.email = params.email
		userMap.gender = params.gender
		userMap.facebookId= params.facebookId
		forward action: 'saveuser', model: [userMap: userMap]
	}
	
}
