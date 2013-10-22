package com.racloop

import grails.plugin.nimble.InstanceGenerator

class StaticPageController {
	
	def userService
	def recaptchaService

	def home(){
		def user = getAuthenticatedUser()
		if(user) {
			redirect(action: "search")
		}
		else {
			redirect(action: "index")
		}
	}
	
	def terms() {
	}

	def etiquettes() {
	}

	def about() {
	}

	def search() {
		def commandInstance = new JourneyRequestCommand()
		[commandInstance : commandInstance]
	}
	
	def index() {
		
	}
	
	def login() {
	
	}
}
