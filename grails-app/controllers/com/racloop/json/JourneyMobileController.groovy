package com.racloop.json

import static com.racloop.util.date.DateUtil.convertUIDateToElasticSearchDate
import grails.converters.JSON
import grails.gsp.PageRenderer

import java.text.SimpleDateFormat

import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.common.joda.time.DateTime

import com.racloop.Constant
import com.racloop.GenericUtil
import com.racloop.HistoryComparator
import com.racloop.JourneyComparator;
import com.racloop.JourneyRequestCommand
import com.racloop.User
import com.racloop.domain.Journey
import com.racloop.domain.WayPoint
import com.racloop.mobile.data.response.MobileResponse

class JourneyMobileController {
	
	def journeyDataService
	def workflowDataService
	def journeySearchService
	def grailsApplication
	PageRenderer groovyPageRenderer
	def cabDetailsService
	
	def search() {
		def json = request.JSON
		log.info("search json ${json}")
		String jsonMessage = null
		String jsonResponse = "error"
		MobileResponse mobileResponse = new MobileResponse()
		def errors = null
		def searchResultMap = null
		JourneyRequestCommand currentJourneyCommand = null;//convertJsonToJourneyObject(json);
		//in case this search request is forwarded from other method
		JourneyRequestCommand currentJourneyFromRequest = request.getAttribute('currentJourney')
		if(currentJourneyFromRequest) {
			currentJourneyCommand = currentJourneyFromRequest
		}
		else {
			currentJourneyCommand = convertJsonToJourneyObject(json);
		}
		def currentUser = getAuthenticatedUser();
		if(!currentUser) {
			currentUser = User.findByUsername(currentJourneyCommand.user);
		}
		if(currentUser) {
			currentJourneyCommand.user = currentUser.username
			currentJourneyCommand.name = currentUser.profile.fullName
			currentJourneyCommand.isMale = currentUser.profile.isMale
			currentJourneyCommand.mobile = currentUser.profile.mobile
			currentJourneyCommand.ip = request.remoteAddr
			currentJourneyCommand.photoUrl = currentUser.getPhotoUrl()
			session.currentJourneyCommand = currentJourneyCommand
			if(isAfterUpperLimit(currentJourneyCommand.validStartTime, currentJourneyCommand.dateOfJourney)) {
				mobileResponse.message = "You cannot search for more than 7 days in future"
			}
			else if(currentJourneyCommand.dateOfJourney.after(currentJourneyCommand.validStartTime)) {
				mobileResponse = journeySearchService.executeSearch(currentJourneyCommand);
			}
			else {
				mobileResponse.message = "Journey date cannot be in past."
			}
		}
		else {
			currentJourneyCommand.isMale = true; //defaulting to male so that femaleOnlySearch should work for anonymous user
			currentJourneyCommand.ip = request.remoteAddr
			if(isAfterUpperLimit(currentJourneyCommand.validStartTime, currentJourneyCommand.dateOfJourney)) {
				mobileResponse.message = "You cannot search for more than 7 days in future"
			}
			else if(currentJourneyCommand.dateOfJourney.after(currentJourneyCommand.validStartTime)) {
				mobileResponse = journeySearchService.executeSearch(currentJourneyCommand);
			}
			else {
				mobileResponse.message = "Journey date cannot be in past."
			}
			//mobileResponse.message = "User is not logged in. Cannot fetch search results"
		}
		render mobileResponse as JSON
	}
	
	private boolean isAfterUpperLimit(Date validStartDate, Date journeyDateTime){
		Integer timeLimitInDays = Integer.valueOf(grailsApplication.config.grails.max.days.to.search)
		Date validEndDate = new Date(validStartDate.getTime() + timeLimitInDays * 24 * 60 * 60000)
		return journeyDateTime.after(validEndDate)
	}
	
	/**
	 * Search request coming from MyJourney
	 * @return
	 */
	def searchAgain() {
		def json = request.JSON
		log.info("searchAgain json ${json}")
		def journeyId = json?.journeyId
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			Journey currentJourney = journeyDataService.findJourney(journeyId);
			if(currentJourney != null) {
				JourneyRequestCommand currentJourneyCommand = currentJourney.convert();
				//no need to search dummy records
				mobileResponse = journeySearchService.straightThruSearch(currentJourneyCommand, true);
				mobileResponse.data['hideSaveButton'] = true;
				session.currentJourneyCommand = currentJourneyCommand
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
		def json = request.JSON
		log.info("keepOriginalAndSearch json ${json}")
		def existingJourneyId = json?.existingJourneyId
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			Journey existingJourney = journeyDataService.findJourney(existingJourneyId)
			if(existingJourney != null) {
				def currentJourneyCommand = existingJourney.convert()
				session.currentJourneyCommand = currentJourneyCommand
				mobileResponse = journeySearchService.straightThruSearch(currentJourneyCommand);
				mobileResponse.data['hideSaveButton'] = true;
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
		def json = request.JSON
		log.info("replaceAndSearch json ${json}")
		def existingJourneyId = json?.existingJourneyId
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		JourneyRequestCommand currentJourneyCommand = convertJsonToJourneyObject(json);
		if(currentUser) {
			currentJourneyCommand.user = currentUser.username
			currentJourneyCommand.name = currentUser.profile.fullName
			currentJourneyCommand.isMale = currentUser.profile.isMale
			currentJourneyCommand.mobile = currentUser.profile.mobile
			currentJourneyCommand.ip = request.remoteAddr
			currentJourneyCommand.photoUrl = currentUser.getPhotoUrl()
			if(currentJourneyCommand.dateOfJourney.after(currentJourneyCommand.validStartTime)) {
				workflowDataService.cancelMyJourney(existingJourneyId);
				Journey journey = Journey.convert(currentJourneyCommand);
				journey.id = null;
				journeyDataService.createJourney(journey);
				currentJourneyCommand.id = journey.id;
				mobileResponse = journeySearchService.straightThruSearch(currentJourneyCommand, true);
				mobileResponse.message = "Journey replaced successfully"
				mobileResponse.data['hideSaveButton'] = true;
				session.currentJourneyCommand = currentJourneyCommand
			}
			else {
				mobileResponse.message = "Journey date cannot be in past."
			}
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform search"
		}
		render mobileResponse as JSON
	}
	
	def journeyDetails(String journeyId, boolean isMyJourney, boolean isDummy) {
		log.info("journeyId : ${journeyId}, isMyJourney : ${isMyJourney}, isDummy : ${isDummy}")
		MobileResponse mobileResponse = new MobileResponse()
		Journey journey = journeyDataService.findJourney(journeyId)
		if(!journey) {
			log.info "Finding data from Elastic search"
			journey = journeyDataService.findJourneyFromElasticSearch(journeyId, true);
		}
		if(journey) {
			List<WayPoint> wayPoints = null;
			Map priceMap = [:];
			if(isMyJourney) {
				wayPoints = journeyDataService.getRouteWayPointForMyJourney(journeyId)
				if(journey.getTripDistance() <= 50) priceMap = cabDetailsService.getDelhiCabPrices(journey.getTripDistance(), journey.getTripTimeInSeconds())
			}
			else {
				JourneyRequestCommand currentJourneyCommand = session.currentJourneyCommand
				wayPoints = journeyDataService.getRouteWayPoint(Journey.convert(currentJourneyCommand), journeyId)
				if(journey.getTripDistance() <= 50) priceMap = cabDetailsService.getDelhiCabPrices(currentJourneyCommand.getTripDistance(),currentJourneyCommand.getTripTimeInSeconds())
			}
			
			if(wayPoints){
				String html = groovyPageRenderer.render template: '/templates/misc/journeyDetails', model: [id: journeyId, wayPoints:wayPoints, priceMap:priceMap]
				mobileResponse.success = true;
				mobileResponse.message = html;
			}
			else {
				mobileResponse.success = false;
				mobileResponse.message = "Sorry, unable to find route details"
			}
		}
		else {
			log.error "Could not find the journey for this id ${journeyId}"
			mobileResponse.success = false;
			mobileResponse.message = "Sorry, unable to find route details"
		}
		render mobileResponse as JSON
	}

    def myJourneys() {		
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		log.info("myJourneys json ${json}")
		def currentUser = getAuthenticatedUser();
		String currentTime = params.currentTime;//json?.currentTime
		log.info "myJourneys currentUser : ${currentUser?.profile?.email} currentTime : ${currentTime}"
		Date current = GenericUtil.uiDateStringToJavaDate(currentTime);
		DateTime minimumValidSearchTime = new DateTime(current).minusHours(1);
		if(currentUser) {
			def journeys = journeyDataService.findMyJourneys(currentUser.profile.mobile, minimumValidSearchTime.toDate());
			Collections.sort(journeys, new JourneyComparator());
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

    def childJourneys() {		
		def json = request.JSON
		log.info("childJourneys json ${json} : ${params}")
		def journeyId = params.journeyId
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		//JourneyRequestCommand currentJourneyCommand = convertJsonToJourneyObject(json);
		if(currentUser) {
			def journey = journeyDataService.findChildJourneys(journeyId);
			mobileResponse.data = journey?.relatedJourneys
			mobileResponse.success = true
			mobileResponse.total = journey?.relatedJourneys.size()
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform search"
		}
		render mobileResponse as JSON
	}
	
	def passengers() {
		def json = request.JSON
		log.info("childJourneys json ${json} : ${params}")
		def journeyId = params.journeyId
		MobileResponse mobileResponse = new MobileResponse()
		def currentUser = getAuthenticatedUser();
		if(currentUser) {
			def journeys = journeyDataService.findSiblingJourneys(journeyId);
			mobileResponse.data = journeys;
			mobileResponse.success = true
			mobileResponse.total = journeys.size()
		}
		else {
			mobileResponse.message = "User is not logged in. Unable to perform search"
		}
		render mobileResponse as JSON
	}

    def myHistory() {				
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		log.info("myHistory json ${json}")
		def currentUser = getAuthenticatedUser();
		String currentTime = params.currentTime;//json?.currentTime
		log.info "myHistory currentUser : ${currentUser?.profile?.email} currentTime : ${currentTime}"
		Date current = GenericUtil.uiDateStringToJavaDate(currentTime);
		DateTime minimumValidSearchTime = new DateTime(current).minusHours(1);
		if(currentUser) {
			def journeys = journeyDataService.findMyHistory(currentUser.profile.mobile, minimumValidSearchTime.toDate());
			Collections.sort(journeys, new HistoryComparator());
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
		currentJourney.isTaxi = json?.isTaxi?.toBoolean()
		currentJourney.tripDistance = convertToDouble(json?.tripDistance)
		currentJourney.tripTimeInSeconds = converToInteger(json?.tripTimeInSeconds)
		currentJourney.tripUnit = json?.tripUnit;
		currentJourney.ip = request.remoteAddr;
		currentJourney.user = json?.user
		currentJourney.id = json?.id	
		currentJourney.femaleOnlySearch = json?.femaleOnlySearch		
		return currentJourney
	}
	
	private Double convertToDouble(Object input) {
		if(input){
			 return Double.valueOf(input)
		}
		return null
	}
	
	private Integer converToInteger(Object input){
		if(input){
			 return Integer.valueOf(input)
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
	
	def nearByPoints() {
		def json = request.JSON
		log.info("nearByPoints json ${json} : ${params}")
		def currentUser = getAuthenticatedUser();
		MobileResponse mobileResponse = new MobileResponse();
		if(currentUser) {
			String mobile = currentUser.profile.mobile;
			double latitude = convertToDouble(json?.latitude)
			double longitude = convertToDouble(json?.longitude)
			def dateOfJourney = GenericUtil.uiDateStringToJavaDateForSearch(json?.currentTime);
			def journeys = journeySearchService.nearByPoints(mobile, dateOfJourney, latitude, longitude);
			def points = [];
			journeys.each { journey ->
				log.info journey
				points << new GeoPoint(journey.fromLatitude, journey.fromLongitude);
			}
			log.info "nearByPoints size : ${points.size()}"
			mobileResponse.success = true
			mobileResponse.data = points;
			mobileResponse.total = points.size()
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total = 0
		}
		render mobileResponse as JSON
	}
}
