package com.racloop

import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.AuthController
import grails.plugin.nimble.core.ProfileBase

class UserSessionController {

	def userService
	def recaptchaService

	def search() {
		def user = getAuthenticatedUser();//dynamic method added by nimble
		JourneyRequestCommand commandInstance = new JourneyRequestCommand();
		if(user) {
			def criteria = TravelHistory.createCriteria();
			def historyResults = criteria {
				eq("user", user)
				maxResults(7)
				and {
					order('searchCount', 'desc')
					order('lastUpdatedAt', 'desc')
				}
			}
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
		user.profile.properties['fullName', 'email', 'mobile'] = params
		user.profile.isMale = isMale
		user.enabled = nimbleConfig.localusers.provision.active
		user.external = false

		user.validate()

		log.debug("Attempting to create new user account identified as $user.username")

		// Enforce username restrictions on local accounts, letters + numbers only
		if (user.username == null || user.username.length() < nimbleConfig.localusers.usernames.minlength || !user.username.matches(nimbleConfig.localusers.usernames.validregex)) {
			log.debug("Supplied username of $user.username does not meet requirements for local account usernames")
			user.errors.rejectValue('username', 'nimble.user.username.invalid')
		}

		// Enforce email address for account registrations
		if (user.profile.email == null || user.profile.email.length() == 0) {
			user.profile.email = 'invalid'
		}

		if (user.hasErrors()) {
			log.debug("Submitted values for new user are invalid")
			user.errors.each { log.debug it }

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
			log.info(user.profile.email)
			log.info(nimbleConfig.messaging.mail.from)
			log.info(nimbleConfig.messaging.registration.subject)
			sendMail {
				to user.profile.email
				from nimbleConfig.messaging.mail.from
				subject nimbleConfig.messaging.registration.subject
				html g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: savedUser]).toString()
			}
		}
		else {
			log.debug "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: user]).toString()}"
		}

		log.info("Created new account identified as $user.username with internal id $savedUser.id")

		//redirect action: 'search'
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
	
	def forgotPasswordComplete() {
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
					from nimbleConfig.messaging.mail.from
					subject nimbleConfig.messaging.passwordreset.external.subject
					html g.render(template: "/templates/nimble/mail/forgottenpassword_external_email", model: [user: user]).toString()
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
					from nimbleConfig.messaging.mail.from
					subject nimbleConfig.messaging.passwordreset.subject
					html g.render(template: "/templates/nimble/mail/forgottenpassword_email", model: [user: user]).toString()
				}
			}
			else {
				log.debug "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/forgottenpassword_email", model: [user: user]).toString()}"
			}

			log.info("Successful password reset for user identified as [$user.id]$user.username")

			redirect(action: "forgotPasswordComplete")
			return
		}

		log.debug("Captcha entry was invalid when attempting to process forgotten password for user identified by [$user.id]$user.username")
		flash.type = "error"
		flash.message = message(code: 'nimble.invalid.captcha')
		redirect(action: "forgotPassword")
	}
	
	def changePassword(){
		
	}

	private getNimbleConfig() {
		grailsApplication.config.nimble
	}
}
