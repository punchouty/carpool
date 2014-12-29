package com.racloop

import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.AuthController
import grails.plugin.nimble.core.ProfileBase

import org.apache.shiro.crypto.hash.Sha256Hash
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class UserSessionController {

	def userService
	def recaptchaService
	LinkGenerator grailsLinkGenerator
	def emailService

	def search() {
		def user = getAuthenticatedUser();//dynamic method added by nimble
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

		model: [local: local, registration: registration, username: username, rememberMe: (rememberMe != null), targetUri: targetUri]
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
		
		def sex = params['sex']
		boolean isMale = true
		if(sex != 'male') {
			isMale = false
		}
		

		def user = InstanceGenerator.user(grailsApplication)
		user.profile = InstanceGenerator.profile(grailsApplication)
		user.profile.owner = user
		user.properties['username', 'pass', 'passConfirm'] = params
		user.profile.properties['fullName', 'email', 'mobile', 'emergencyContact'] = params
		user.username = user.profile.email
		user.profile.isMale = isMale
		user.enabled = nimbleConfig.localusers.provision.active
		user.external = false

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
		
		if (user.profile.mobile == user.profile.emergencyContact) {
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

		log.info("Sending account registration confirmation email to $user.profile.email with subject $nimbleConfig.messaging.registration.subject")

		if(nimbleConfig.messaging.enabled) {
			emailService.sendMail(user.profile.email, nimbleConfig.messaging.registration.subject, g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: savedUser]).toString(),grailsApplication.config.grails.messaging.mail.from )
		}
		else {
			log.debug "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: user]).toString()}"
		}

		log.info("Created new account identified as $user.username with internal id $savedUser.id")

		flash.type = "message"
		flash.message = "Account registered successfully. Please check your email and activate the account."
		redirect (action: "search")
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

			log.info("Sending account password reset email to $user.profile.email with subject $nimbleConfig.messaging.passwordreset.external.subject")
			if(nimbleConfig.messaging.enabled) {
				sendMail {
					to user.profile.email
					from grailsApplication.config.grails.messaging.mail.from
					subject nimbleConfig.messaging.passwordreset.external.subject
					html g.render(template: "/templates/nimble/mail/forgottenpassword_external_email", model: [user: user, baseUrl: grailsLinkGenerator.serverBaseURL]).toString()
				}
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

			log.info("Sending account password reset email to $user.profile.email with subject $nimbleConfig.messaging.passwordreset.subject")
			if(nimbleConfig.messaging.enabled) {
				sendMail {
					to user.profile.email
					from grailsApplication.config.grails.messaging.mail.from
					subject nimbleConfig.messaging.passwordreset.subject
					html g.render(template: "/templates/nimble/mail/forgottenpassword_email", model: [user: user, baseUrl: grailsLinkGenerator.serverBaseURL]).toString()
				}
			}
			else {
				log.debug "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/forgottenpassword_email", model: [user: user]).toString()}"
			}

			log.info("Successful password reset for user identified as [$user.id]$user.username")

			//redirect(action: "forgotPasswordComplete", validFlow: true)
			flash.type = "message"
			flash.message = "Email is sent to you with new password. Please check your inbox."
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
		[user:authenticatedUser]
	}

	def updatePassword(String currentPassword, String pass, String passConfirm) {
		def user = authenticatedUser

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
				user.errors.reject('nimble.user.password.nomatch')
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
		def user = authenticatedUser
		[user: user]
	}
	
	def editProfile(String fullName, String sex, String mobile, String emergencyContact) {
		def user = authenticatedUser
		boolean isMale = true
		if(sex != 'male') {
			isMale = false
		}
		user.profile.fullName = fullName
		user.profile.mobile = mobile
		user.profile.isMale = isMale
		user.profile.emergencyContact = emergencyContact
		
		if(mobile == emergencyContact) {
			flash.type = "error"
			flash.message = "Emergency contact cannot be same as your mobile number"
			render view: 'profile', model: [user: user]
			return
		}
		
		if (!user.validate()) {
			log.debug("Updated details for user [$user.id] $user.username are invalid")
			render view: 'profile', model: [user: user]
			return
		}

		def updatedUser = userService.updateUser(user)
		
		log.info("Successfully updated details for user [$user.id]$user.username")
		flash.type = "success"
		flash.message = message(code: 'nimble.user.update.success', args: [user.username])
		redirect action: 'profile', user: updatedUser
		return
	}

	private getNimbleConfig() {
		grailsApplication.config.nimble
	}
}
