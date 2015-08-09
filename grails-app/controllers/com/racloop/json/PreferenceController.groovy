package com.racloop.json

import com.racloop.mobile.data.response.MobileResponse
import grails.converters.JSON
import com.racloop.GenericUtil
import com.racloop.JourneyRequestCommand
class PreferenceController {

	def journeyDataService
    def makeRecurring() {def json = request.JSON
		log.info("makeRecurring json ${json}")
		MobileResponse mobileResponse = new MobileResponse();
		mobileResponse.success = true;
		render mobileResponse as JSON;
	}
	
	def recurringJourney() {
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		log.info("recurringJourney json ${json}")		
		String currentTime = params.currentTime;//json?.currentTime
		
			mobileResponse.data = dummyJourney()
			mobileResponse.data.recurringDays = [1,2,5,6]
			mobileResponse.success = true
			mobileResponse.total = 1
		
		log.info "My History size : ${mobileResponse.total}"
		render mobileResponse as JSON
	
	}
	
	def deleteRecurringJourney() {
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		log.info("delete recurring Journey json ${json}")
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
