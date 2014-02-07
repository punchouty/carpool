package com.racloop

import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.AuthController;

class StaticPageController {
	
	def userService
	def recaptchaService
	def staticdataService

	def home(){
		def user = getAuthenticatedUser()
		if(user) {
			redirect(action: "search")
		}
		else {
			redirect(action: "index")
		}
	}
	
	def safety() {
		String value = staticdataService.getSaticValueBasedOnKey("safety")
		[safety:value]
	}
	
	def faq() {
		String value = staticdataService.getSaticValueBasedOnKey("faq")
		[faq:value]
	}

	def about() {
		String value = staticdataService.getSaticValueBasedOnKey("about")
		[about:value]
	}
	
	def terms() {
		String value = staticdataService.getSaticValueBasedOnKey("terms")
		[terms:value]
	}

	def privacy() {
		String value = staticdataService.getSaticValueBasedOnKey("privacy")
		[privacy:value]
	}

	def etiquettes() {
		String value = staticdataService.getSaticValueBasedOnKey("etiquettes")
		[etiquettes:value]
	}

	def search() {
		def user = getAuthenticatedUser();
		def commandInstance = new JourneyRequestCommand();
		def criteria = TravelHistory.createCriteria();
		def historyResults = criteria {
			eq("user", user)
			maxResults(7)
			and {
				order('searchCount', 'desc')
				order('lastUpdatedAt', 'desc')
			}
		}
		[commandInstance: commandInstance, history : historyResults]
	}
	
	def index() {
		
	}
	
	def signin(String targetUri, String username, String rememberMe) {
		def local = nimbleConfig.localusers.authentication.enabled
		def registration = nimbleConfig.localusers.registration.enabled

		if(targetUri) {
			session.setAttribute(AuthController.TARGET, targetUri)
		}

		model: [local: local, registration: registration, username: username, rememberMe: (rememberMe != null), targetUri: targetUri]
	}
	
	def signup(String targetUri, String username, String rememberMe) {
		def local = nimbleConfig.localusers.authentication.enabled
		def registration = nimbleConfig.localusers.registration.enabled

		if(targetUri) {
			session.setAttribute(AuthController.TARGET, targetUri)
		}

		model: [local: local, registration: registration, username: username, rememberMe: (rememberMe != null), targetUri: targetUri]
	}

	private getNimbleConfig() {
		grailsApplication.config.nimble
	}

}
