package com.racloop

import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.AuthController;

class StaticPageController {
	
	def userService
	def recaptchaService
	def staticdataService

//	def home(){
//		def user = getAuthenticatedUser()
//		if(user) {
//			redirect(action: "search")
//		}
//		else {
//			redirect(action: "index")
//		}
//	}
	
	def safety() {
//		String value = staticdataService.getSaticValueBasedOnKey("safety")
//		[safety:value]
	}
	
	def faq() {
//		String value = staticdataService.getSaticValueBasedOnKey("faq")
//		[faq:value]
	}

	def about() {
		String value = staticdataService.getSaticValueBasedOnKey("about")
		[about:value]
	}
	
	def terms() {
//		String value = staticdataService.getSaticValueBasedOnKey("terms")
//		[terms:value]
	}

	def privacy() {
//		String value = staticdataService.getSaticValueBasedOnKey("privacy")
//		[privacy:value]
	}

	def etiquettes() {
		//String value = staticdataService.getSaticValueBasedOnKey("etiquettes")
		//[etiquettes:value]
	}
	
	def main() {
		def user = authenticatedUser
		if(user) {
			redirect (controller : "userSession" , action: "search")
		}
		else {
			redirect (action: "index")
		}
	}
	
	def index() {
		def user = authenticatedUser
		[user: user]
	}

}
