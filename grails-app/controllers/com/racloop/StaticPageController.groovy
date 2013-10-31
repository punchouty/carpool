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
	
	def login() {
	
	}
}
