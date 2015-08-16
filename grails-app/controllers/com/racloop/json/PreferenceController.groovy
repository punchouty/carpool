package com.racloop.json

import grails.converters.JSON

import com.racloop.JourneyRequestCommand
import com.racloop.mobile.data.response.MobileResponse
class PreferenceController {

	
	def recurrenceJourneyService
	
    def makeRecurring() {
		def json = request.JSON
		log.info("makeRecurring json ${json}")
		MobileResponse mobileResponse = new MobileResponse();
		String journeyId = json?.journeyId
		String[] recurring = json?.recurring
		recurrenceJourneyService.createNewRecurringJourney(journeyId , recurring)
		mobileResponse.success = true;
		mobileResponse.message = "Saved sucessfully."
		render mobileResponse as JSON;
	}
	
	def recurringJourney() {
		def currentUser = getAuthenticatedUser();
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		log.info("recurringJourney json ${json}")		
		if(currentUser){
			Set result = recurrenceJourneyService.getAllRecurringJourneyForAUser(currentUser.profile.mobile)
			mobileResponse.data = result
			//mobileResponse.data.recurringDays = [1,2,5,6]
			mobileResponse.success = true
			mobileResponse.total = result.size()
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch results."
			mobileResponse.success = false
			mobileResponse.total =0
		}
		log.info "recurringJourney size : ${mobileResponse.total}"
		render mobileResponse as JSON
	
	}
	
	def deleteRecurringJourney() {
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		log.info("delete recurring Journey json ${json}")
		String journeyId = json?.journeyId
		recurrenceJourneyService.deleteRecurringJourney(journeyId)
		mobileResponse.success = true
		mobileResponse.message = "Successfully Deleted"
		render mobileResponse as JSON
	}
	private def dummyJourney() {
		JourneyRequestCommand currentJourney = new JourneyRequestCommand()
		currentJourney.dateOfJourneyString =  new Date() 
		currentJourney.dateOfJourney = new Date()
//		currentJourney.validStartTimeString = json?.validStartTimeString
//		currentJourney.validStartTime = GenericUtil.uiDateStringToJavaDateForSearch(json?.validStartTimeString);
		currentJourney.from = "Chandigarh"
//		currentJourney.fromLatitude = convertToDouble(json?.fromLatitude)
//		currentJourney.fromLongitude = convertToDouble(json?.fromLongitude)
		currentJourney.to = "Delhi"
//		currentJourney.toLatitude = convertToDouble(json?.toLatitude)
//		currentJourney.toLongitude = convertToDouble(json?.toLongitude)
//		currentJourney.isDriver = json?.isDriver?.toBoolean()
//		currentJourney.isTaxi = json?.isTaxi?.toBoolean()
//		currentJourney.tripDistance = convertToDouble(json?.tripDistance)
//		currentJourney.tripTimeInSeconds = converToInteger(json?.tripTimeInSeconds)
//		currentJourney.tripUnit = json?.tripUnit;
//		currentJourney.ip = request.remoteAddr;
//		currentJourney.user = json?.user
		currentJourney.id = 123
		return currentJourney
	}
}
