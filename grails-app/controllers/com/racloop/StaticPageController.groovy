package com.racloop

import grails.plugin.nimble.InstanceGenerator

class StaticPageController {
	
	def userService
	def recaptchaService

	def home(){
		def user = getAuthenticatedUser()
		if(user) {
			redirect(action: "search", params: [journeyInstance: new Journey(params)])
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
	}
	
	def index() {
		
	}
	
	def login() {
	
	}
}
