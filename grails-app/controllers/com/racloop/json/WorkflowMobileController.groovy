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
	
	def saveJourney() {
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			JourneyRequestCommand currentJourney = session.currentJourneyCommand
			if(currentJourney != null) {
				Journey journey = Journey.convert(currentJourney);
				journeyDataService.createJourney(journey);
				session.currentJourneyCommand = null;
				mobileResponse = journeySearchService.straightThruSearch(currentJourney, true);
				mobileResponse.data['hideSaveButton'] = true;
				mobileResponse.message = "Journey saved successfully"
				mobileResponse.currentJourney = journey
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
	
	def sendRequest() {
		def json = request.JSON
		def otherJourneyId = json.matchedJourneyId;
		log.info("request() json : ${json}");
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			JourneyRequestCommand currentJourney = session.currentJourneyCommand
			if(currentJourney != null) {
				Journey myJourney = Journey.convert(currentJourney)
				if(workflowDataService.validateInvitationRequest(myJourney, otherJourneyId)){
					log.info("currentJourney.id : " + currentJourney.id + ", otherJourneyId : " + otherJourneyId)
					if(currentJourney.isNewJourney()) {
						workflowDataService.requestJourneyAndSave(myJourney, otherJourneyId);
					}
					else {
						workflowDataService.requestJourney(currentJourney.id, otherJourneyId);
					}
					session.currentJourneyCommand = null;
					mobileResponse.success = true;
					mobileResponse.message = "Request Sent to user";
				}
				else {
					mobileResponse.success = false;
					mobileResponse.message = "Sorry, you cannot invite yourself.";
				}
			}
			else {
				mobileResponse.message = "Error : No journey in session"
				log.warn ("currentJourney not in session scope for user ${currentUser} so cannot perform request")
			}
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform save"
		}
		render mobileResponse as JSON
	}
	
	private isNewJourney(JourneyRequestCommand currentJourney) {
		return currentJourney?.id == null;
	}
	
	def deleteJourney() {
		def json = request.JSON
		log.info("deleteJourney() json : ${json}");
		def journeyId = json.journeyId;
		def mobileResponse = new MobileResponse();
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			workflowDataService.cancelMyJourney(journeyId);
			mobileResponse.success = true
			mobileResponse.message = "Successfully Cancelled Journey"
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform cancel my journey"
		}
		
		render mobileResponse as JSON
	}
	
	def acceptRequest() {
		def json = request.JSON
		log.info("acceptRequest() json : ${json}");
		def journeyPairId = json.journeyPairId;
		def mobileResponse = new MobileResponse();
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			mobileResponse.data = workflowDataService.acceptRequest(journeyPairId)
			mobileResponse.success = true
			mobileResponse.message = "Successfully Accepted journey request"
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform accept request"
		}
		render mobileResponse as JSON
	}
	
	def rejectRequest() {
		def json = request.JSON
		log.info("rejectJourneyRequest() json : ${json}");
		def journeyPairId = json.journeyPairId;
		def mobileResponse = new MobileResponse();
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			mobileResponse.data = workflowDataService.rejectRequest(journeyPairId)
			mobileResponse.success = true
			mobileResponse.message = "Successfully rccepted journey request"
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform reject request"
		}
		render mobileResponse as JSON
	}
	
	def cancelRequest() {
		def json = request.JSON
		log.info("cancelJourneyRequest() json : ${json}");
		def journeyPairId = json.journeyPairId;
		def myJourneyId = json.journeyId;
		def mobileResponse = new MobileResponse();
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			mobileResponse.data = workflowDataService.cancelMyRequest(journeyPairId, myJourneyId, currentUser.getUsername())
			mobileResponse.success = true
			mobileResponse.message = "Successfully cancelled journey request"
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform cancel request"
		}
		render mobileResponse as JSON
	}
}
