package com.racloop

import grails.plugin.nimble.InstanceGenerator

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
	
	def terms() {
		String value = staticdataService.getSaticValueBasedOnKey("term")
		[terms:value]
	}

	def etiquettes() {
		String value = staticdataService.getSaticValueBasedOnKey("etiquettes")
		[etiquettes:value]
	}

	def about() {
		String value = staticdataService.getSaticValueBasedOnKey("about")
		[about:value]
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
