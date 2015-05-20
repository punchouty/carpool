package com.racloop.json

import static com.racloop.util.date.DateUtil.convertUIDateToElasticSearchDate
import grails.converters.JSON

import com.racloop.JourneyRequestCommand
import com.racloop.domain.Journey
import com.racloop.mobile.data.response.MobileResponse

class WorkflowMobileController {
	
	def journeyDataService
	def workflowDataService
	def journeySearchService
	
	def save() {
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			JourneyRequestCommand currentJourney = session.currentJourney
			if(currentJourney != null) {
				Journey journey = Journey.convert(currentJourney);
				journeyDataService.createJourney(journey);
				mobileResponse = journeySearchService.straightThruSearch(currentJourney);
				mobileResponse.message = "Journey saved successfully"
			}
			else {
				mobileResponse.message = "Error : No journey to save"
				log.warn ("currentJourney not in session scope for user ${currentUser}")
			}
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform save"
		}
		render mobileResponse as JSON
	}
	
	def request() {
		def json = request.JSON
		log.info("json : ${json}")	
		String jsonMessage = null
		String jsonResponse = "error"
		def mobileResponse = new MobileResponse()
		render mobileResponse as JSON
	}
}
