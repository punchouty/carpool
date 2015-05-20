package com.racloop.json

import static com.racloop.util.date.DateUtil.convertUIDateToElasticSearchDate
import grails.converters.JSON

import java.text.SimpleDateFormat

import org.elasticsearch.common.joda.time.DateTime

import com.racloop.Constant
import com.racloop.GenericUtil
import com.racloop.JourneyRequestCommand
import com.racloop.User
import com.racloop.domain.Journey
import com.racloop.mobile.data.response.MobileResponse

class JourneyMobileController {
	
	def journeyDataService
	def workflowDataService
	def journeySearchService
	
	def search() {
		log.info("myJourneys Request from ${request.remoteAddr}")	
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		MobileResponse mobileResponse = new MobileResponse()
		def errors = null
		def searchResultMap = null
		JourneyRequestCommand currentJourney = null;//convertJsonToJourneyObject(json);
		//in case this search request is forwarded from other method
		JourneyRequestCommand currentJourneyFromRequest = request.getAttribute('currentJourney')
		if(currentJourneyFromRequest) {
			currentJourney = currentJourneyFromRequest
		}
		else {
			currentJourney = convertJsonToJourneyObject(json);
		}
		def currentUser = getAuthenticatedUser();
		if(!currentUser) {
			currentUser = User.findByUsername(currentJourney.user);
		}
		if(currentUser) {
			currentJourney.user = currentUser.username
			currentJourney.name = currentUser.profile.fullName
			currentJourney.isMale = currentUser.profile.isMale
			currentJourney.mobile = currentUser.profile.mobile
			currentJourney.ip = request.remoteAddr
			session.currentJourney = currentJourney
			if(currentJourney.dateOfJourney.after(currentJourney.validStartTime)) {
				mobileResponse = journeySearchService.executeSearch(currentJourney);
			}
			else {
				mobileResponse.message = "Invalid travel date and time"
			}
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
		}
		render mobileResponse as JSON
	}
	
	def searchAgain() {
		def json = request.JSON
		def journeyId = json?.journeyId
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			Journey currentJourney = journeyDataService.findJourney(journeyId);
			if(currentJourney != null) {
				JourneyRequestCommand currentJourneyCommand = currentJourney.convert()
				mobileResponse = journeySearchService.straightThruSearch(currentJourneyCommand, false);
			}
			else {
				mobileResponse.message = "Error : No journey to search"
				log.warn ("currentJourney not in session scope for user ${currentUser}")
			}
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform search"
		}
		render mobileResponse as JSON
	}
	
	def keepOriginalAndSearch() {
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			JourneyRequestCommand currentJourney = session.currentJourney
			if(currentJourney != null) {
				mobileResponse = journeySearchService.straightThruSearch(currentJourney);
			}
			else {
				mobileResponse.message = "Error : No journey to search"
				log.warn ("currentJourney not in session scope for user ${currentUser}")
			}
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform search"
		}
		render mobileResponse as JSON
	}
	
	def replaceAndSearch() {
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			JourneyRequestCommand currentJourney = session.currentJourney
			if(currentJourney != null) {
				workflowDataService.replace(Journey.convert(currentJourney));
				mobileResponse = journeySearchService.straightThruSearch(currentJourney);
			}
			else {
				mobileResponse.message = "Error : No journey to search"
				log.warn ("currentJourney not in session scope for user ${currentUser}")
			}
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform search"
		}
		render mobileResponse as JSON
	}

    def myJourneys() {	
		log.info("myJourneys Request from ${request.remoteAddr}")		
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		def currentUser = getAuthenticatedUser();
		String currentTime = params.currentTime;//json?.currentTime
		log.info "myJourneys currentUser : ${currentUser?.profile?.email} currentTime : ${currentTime}"
		if(currentUser) {
			def journeys = journeyDataService.findMyJourneys(currentUser.profile.mobile, GenericUtil.uiDateStringToJavaDate(currentTime));
			mobileResponse.data = journeys
			mobileResponse.success = true
			mobileResponse.total = journeys?.size()
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
		log.info "My Journeys size : ${mobileResponse.total}"	
		render mobileResponse as JSON
	}

    def myHistory() {	
		log.info("myJourneys Request from ${request.remoteAddr}")			
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		def currentUser = getAuthenticatedUser();
		String currentTime = params.currentTime;//json?.currentTime
		log.info "myHistory currentUser : ${currentUser?.profile?.email} currentTime : ${currentTime}"
		if(currentUser) {
			def journeys = journeyDataService.findMyHistory(currentUser.profile.mobile, GenericUtil.uiDateStringToJavaDate(currentTime));
			mobileResponse.data = journeys
			mobileResponse.success = true
			mobileResponse.total = journeys?.size()
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
		log.info "My History size : ${mobileResponse.total}"	
		render mobileResponse as JSON
	}
	
	private def convertJsonToJourneyObject(def json) {
		JourneyRequestCommand currentJourney = new JourneyRequestCommand()
		currentJourney.dateOfJourneyString = json?.dateOfJourneyString
		currentJourney.dateOfJourney = GenericUtil.uiDateStringToJavaDateForSearch(json?.dateOfJourneyString);
		currentJourney.validStartTimeString = json?.validStartTimeString
		currentJourney.validStartTime = GenericUtil.uiDateStringToJavaDateForSearch(json?.validStartTimeString);
		currentJourney.from = json?.from
		currentJourney.fromLatitude = convertToDouble(json?.fromLatitude)
		currentJourney.fromLongitude = convertToDouble(json?.fromLongitude)
		currentJourney.to = json?.to
		currentJourney.toLatitude = convertToDouble(json?.toLatitude)
		currentJourney.toLongitude = convertToDouble(json?.toLongitude)
		currentJourney.isDriver = json?.isDriver?.toBoolean()
		currentJourney.tripDistance = convertToDouble(json?.tripDistance)
		currentJourney.tripUnit = json?.tripUnit;
		currentJourney.ip = request.remoteAddr;
		currentJourney.user = json?.user
		currentJourney.id = json?.id		
		return currentJourney
	}
	
	private Double convertToDouble(Object input) {
		if(input){
			 return Double.valueOf(input)
		}
		return null
	}
	
	private setDates(JourneyRequestCommand currentJourney) {
		boolean success = true
		SimpleDateFormat uiFormatter = new SimpleDateFormat(Constant.DATE_FORMAT_UI);
		try {
			if(currentJourney.dateOfJourneyString) {
				currentJourney.dateOfJourney = uiFormatter.parse(currentJourney.dateOfJourneyString);//convertUIDateToElasticSearchDate(currentJourney.dateOfJourneyString).toDate()
			}
			if(currentJourney.validStartTimeString) {
				currentJourney.validStartTime = uiFormatter.parse(currentJourney.validStartTimeString);//new DateTime().toDate()
			}
			if(!currentJourney.validStartTime) {
				DateTime currentDate = new DateTime()
				currentDate.plusMinutes(Integer.valueOf(grailsApplication.config.grails.approx.time.to.match))
				currentJourney.validStartTime = currentDate.toDate()
			}
			
		}
		catch(IllegalArgumentException e) {
			success = false
			log.error "Something went wrong while setting Dates", e
		}
		return success;
	}
}
