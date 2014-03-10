package com.racloop

import grails.converters.JSON

import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.format.DateTimeFormat
import org.elasticsearch.common.joda.time.format.DateTimeFormatter

class JourneyController {
	
	// Date format for date.js library - dd MMMM yyyy    hh:mm tt - map.js
	// This is different from that of datetime plugin which is - dd MM yyyy    HH:ii P - search.gsp
	// This in turn is different from Joda date format - dd MMMM yyyy    hh:mm a - JourneyController.groovy
	public static final String JAVA_DATE_FORMAT = "dd MMMM yyyy    HH:mm a";
	public static final DateTimeFormatter UI_DATE_FORMAT = DateTimeFormat.forPattern(JAVA_DATE_FORMAT);
	def grailsApplication
	def journeyService
	def journeyWorkflowService
	def journeyManagerService

    def findMatching(JourneyRequestCommand currentJourney) {
		if(chainModel && chainModel.currentJourney) {
			currentJourney = chainModel.currentJourney
		}
		def currentUser = getAuthenticatedUser();
		setUserInformation(currentUser,currentJourney )
		currentJourney.ip = request.remoteAddr
		setDates(currentJourney)
		if(currentJourney.dateOfJourney && currentJourney.validStartTime && currentJourney.dateOfJourney.after(currentJourney.validStartTime)) {
			if(currentJourney.validate()) {
				def searchResultMap = getSearchResultMap(currentUser, currentJourney)
				render(view: "results", model: searchResultMap)
			}
			else {
				log.warn 'Error in command : ' + params
				redirect(controller: 'userSession', action: "search")
			}
		}
		else {
			currentJourney.errors.rejectValue("travelDateString", "invalid.travel.date", [message(code: 'travel.date', default: 'Travel Date')] as Object[],
                          "Invalid travel date")
			log.warn 'Error in command travel dates : ' + params
			redirect(controller: 'userSession', action: "search")
		}
	}
	
	private setUserInformation(User currentUser, JourneyRequestCommand currentJourney) {
		if(currentUser) {
			currentJourney.user = currentUser.username
			currentJourney.name = currentUser.profile.fullName
			currentJourney.isMale = currentUser.profile.isMale
		}
	}
	
	private setDates(JourneyRequestCommand currentJourney) {
		if(currentJourney.dateOfJourneyString) {
			currentJourney.dateOfJourney = UI_DATE_FORMAT.parseDateTime(currentJourney.dateOfJourneyString).toDate()
		}
		if(currentJourney.validStartTimeString) {
			currentJourney.validStartTime = UI_DATE_FORMAT.parseDateTime(currentJourney.validStartTimeString).toDate()
		}
		if(!currentJourney.validStartTime) {
			DateTime currentDate = new DateTime()
			//TODO - get this Info from config
			currentDate.plusMinutes(Integer.valueOf(grailsApplication.config.grails.approx.time.to.match))
			currentJourney.validStartTime = currentDate.toDate()
		}
	}
	
	private getSearchResultMap(User currentUser, JourneyRequestCommand currentJourney) {
		currentJourney = getExisitngJourneyForUser(currentUser, currentJourney)
		def selectedJourneyIds = getAlreadySelectedJourneyIdsForCurrentJourney(currentJourney)
		updateHttpSession(currentJourney,selectedJourneyIds)
		def matchedJourney = searchJourneys(currentUser, currentJourney)
		def matchResult =[currentUser: currentUser, currentJourney: currentJourney, journeys : matchedJourney.matchedJourneys, numberOfRecords : matchedJourney.numberOfRecords, isDummyData: matchedJourney.isDummyData]
		return matchResult
	}
	
	private Map searchJourneys(User currentUser, JourneyRequestCommand currentJourney) {
		def matchedJourney =[:]
		boolean isDummyData =false
		def journeys = journeyService.search(currentUser, currentJourney)
		int numberOfRecords = 0;
		if(journeys.size > 0) {
			numberOfRecords = journeys.size
		}
		else {
			journeys = journeyService.getDummyData(currentUser, currentJourney)
			numberOfRecords = journeys.size
			isDummyData = true;
		}
		matchedJourney.matchedJourneys = journeys
		matchedJourney.numberOfRecords = numberOfRecords
		matchedJourney.isDummyData = isDummyData
		return matchedJourney
	}
	
	private getExisitngJourneyForUser(User currentUser, JourneyRequestCommand currentJourneyFromWeb) {
		if(currentUser && !currentJourneyFromWeb.id && !currentJourneyFromWeb.isSaved) {
			def possibleExisitingJourney = journeyService.searchPossibleExistingJourneyForUser(currentUser, currentJourneyFromWeb)
			if(possibleExisitingJourney) {
				currentJourneyFromWeb = possibleExisitingJourney
			}
		}
		return currentJourneyFromWeb
	}
	
	private updateHttpSession(JourneyRequestCommand currentJourney, List selectedJourneyIds) {
		session.currentJourney = currentJourney
		selectedJourneyIds.each {journeyId ->
			setSelectedJounreyInSession(currentJourney, journeyId)
		}
	}
	private getAlreadySelectedJourneyIdsForCurrentJourney(JourneyRequestCommand currentJourney) {
		def selectedJourneyIds = journeyWorkflowService.getAlreadySelectedJourneyIdsForCurrentJourney(currentJourney)
		return selectedJourneyIds
	}
	
//	def activeJourneys() {
//		def currentUser = getAuthenticatedUser();
//		def activeJourneys = currentUser.journeys;
//	}
	
//	def results() {
//		def currentUser = getAuthenticatedUser();
//		def currentJourney = session.currentJourney
//		if(currentJourney.isSaved) {
//			log.warnEnabled "Should not come here. This journey is already processed"
//			redirect(controller: 'staticPage', action: "search")			
//		}
//		else {
//			def journeys = journeyService.search(currentUser, currentJourney)
//			int numberOfRecords = 0;
//			if(journeys.size > 0) {
//				numberOfRecords = journeys.size
//			}
//			[currentJourney: currentJourney, journeys : journeys, numberOfRecords : numberOfRecords]
//		}
//	}
	
	def newJourney() {
		def currentUser = getAuthenticatedUser();
		def currentJourney = session.currentJourney
		journeyManagerService.createJourney(currentUser, currentJourney)
		session.currentJourney.isSaved = true
		flash.message ="Successfully saved your request"
		//redirect(controller: 'staticPage', action: "search")
		render(view: "results", model: [currentUser: currentUser, currentJourney: currentJourney])
	}
	
	def requestService(JourneyRequestCommand myJourney) {
		def currentJourney = session.currentJourney
		def currentUser = getAuthenticatedUser()
		def matchedJourneyId = params.matchedJourneyId
		boolean isDummy =params.boolean('dummy')
		if(!currentJourney.user) {
			currentJourney.user = currentUser.username
			currentJourney.name = currentUser.profile.fullName
			currentJourney.isMale = currentUser.profile.isMale
			session.currentJourney = currentJourney
		}
		def matchedJourney = journeyService.findMatchedJourneyById(matchedJourneyId, currentJourney, isDummy)
		def workflow = journeyWorkflowService.saveJourneyAndInitiateWorkflow(currentJourney,matchedJourney)
		setSelectedJounreyInSession(currentJourney,matchedJourneyId)
		//render(view: "results", model: [currentUser: currentUser, currentJourney: currentJourney, journeys : journeys, numberOfRecords : numberOfRecords, isDummyData: isDummyData])
		chain(action: 'findMatching', model: [currentJourney: currentJourney])
	}

	private setSelectedJounreyInSession(def currentJourney, String matchedJourneyId) {
		def currentJourneyId = currentJourney.id
		def selectedJourneysMap = session.selectedJourneysMap
		def  selectedJourneyIdList=[] as Set
		if(!selectedJourneysMap) {
			selectedJourneysMap =[:]
			session.selectedJourneysMap = selectedJourneysMap
		}
		else {
			if(session.selectedJourneysMap.get(currentJourneyId)) {
				selectedJourneyIdList =session.selectedJourneysMap.get(currentJourneyId)
			}
		}
		selectedJourneyIdList<<matchedJourneyId
		session.selectedJourneysMap.put(currentJourneyId,selectedJourneyIdList)
	}
	
	def getWorkflow(){
		def currentUser = getAuthenticatedUser()
		def workflows = journeyWorkflowService.searchWorkflowRequestedByUser(currentUser)
		render workflows as JSON
	}
	
	def selectedJourney(){
		def currentJourney = session.currentJourney
		def matchedJourneyId = params.matchedJourneyId
		boolean isDummy =params.boolean('dummy') 
		def matchedJourney = journeyService.findMatchedJourneyById(matchedJourneyId, currentJourney, isDummy)
		def matchedUser = User.findByUsername(matchedJourney.user)
		[matchedJourney: matchedJourney, matchedUser:matchedUser, isDummy: isDummy]
	}
	
	
}

class JourneyRequestCommand {
	
	String user
	String id;//will be assigned later
	String name; //Should get from user. 
	Boolean isMale; //Should get from user.
	String dateOfJourneyString; //date as string
	Date dateOfJourney; //it should get populated before validation
	String validStartTimeString; //date as string
	Date validStartTime; //it should get populated before validation
	String fromPlace;
	Double fromLatitude;
	Double fromLongitude;
	String toPlace;
	Double toLatitude;
	Double toLongitude;
	Boolean isDriver;
	Double tripDistance;
	String tripUnit;
	String ip; //should get from request
	Date createdDate = new Date();
	Boolean isSaved = false
	
	static constraints = {
		id nullable : true
		/*name blank: true
		ip blank : true*/
	}
	
	String toString(){
		return "JourneyRequestCommand -> name : ${name} | isDriver : ${isDriver} | dateOfJourneyString : ${dateOfJourneyString} | fromPlace : ${fromPlace} | toPlace : ${toPlace}";
	}
	
}
