package com.racloop

import grails.converters.JSON

import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.format.DateTimeFormat
import org.elasticsearch.common.joda.time.format.DateTimeFormatter

import com.racloop.journey.workkflow.WorkflowState
import com.racloop.workflow.JourneyWorkflow

class JourneyController {
	
	// Date format for date.js library - dd MMMM yyyy    hh:mm tt - map.js
	// This is different from that of datetime plugin which is - dd MM yyyy    HH:ii P - search.gsp
	// This in turn is different from Joda date format - dd MMMM yyyy    hh:mm a - JourneyController.groovy
	public static final String JAVA_DATE_FORMAT = "dd MMMM yyyy    hh:mm a";
	public static final DateTimeFormatter UI_DATE_FORMAT = DateTimeFormat.forPattern(JAVA_DATE_FORMAT);
	def grailsApplication
	def journeyService
	def journeyWorkflowService
	def journeyManagerService

    def findMatching(JourneyRequestCommand currentJourney) {
		println params.inspect()
		
		if(chainModel && chainModel.currentJourney) {
			currentJourney = chainModel.currentJourney
		}
		def currentUser = getAuthenticatedUser();
		setUserInformation(currentUser,currentJourney)
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
			currentJourney.errors.rejectValue("dateOfJourneyString", "invalid.travel.date", [message(code: 'travel.date', default: 'Travel Date')] as Object[],
                          "Invalid travel date")
			log.warn 'Error in command travel dates : ' + params
			flash.error = "Invalid Input Data"
			redirect(controller: 'userSession', action: "search")
		}
	}
	
	def mobileFindMatching() {
		def json = request.JSON
		String jsonMessage = null
		String jsonResponse = "error"
		def errors = null
		def searchResultMap = null
		String dateOfJourneyString = json?.dateOfJourneyString
		String validStartTimeString = json?.validStartTimeString
		String fromPlace = json?.fromPlace
		Double fromLatitude = json?.fromLatitude;
		Double fromLongitude = json?.fromLongitude;
		String toPlace = json?.toPlace
		Double toLatitude = json?.toLatitude;
		Double toLongitude = json?.toLongitude;
		Boolean isDriver = json?.isDriver;
		Double tripDistance = json?.tripDistance;
		String tripUnit = json?.tripUnit;
		String ip = request.remoteAddr;
		String user=json?.user
		JourneyRequestCommand currentJourney = new JourneyRequestCommand()
		currentJourney.dateOfJourneyString = dateOfJourneyString
		currentJourney.validStartTimeString = validStartTimeString
		currentJourney.fromPlace = fromPlace
		currentJourney.fromLatitude = fromLatitude
		currentJourney.fromLongitude = fromLongitude
		currentJourney.toPlace = toPlace
		currentJourney.toLatitude = toLatitude
		currentJourney.toLongitude = toLongitude
		currentJourney.isDriver = isDriver
		currentJourney.tripDistance = tripDistance
		currentJourney.tripUnit = tripUnit
		currentJourney.ip = ip
		currentJourney.user = user
		if(chainModel && chainModel.currentJourney) {
			currentJourney = chainModel.currentJourney
		}
		def currentUser = getAuthenticatedUser();
		if(!currentUser) {
			currentUser = User.findByUsername(currentJourney.user);
		}
		if(currentUser) {
			setUserInformation(currentUser,currentJourney)
			currentJourney.ip = request.remoteAddr
			setDates(currentJourney)
			if(currentJourney.dateOfJourney && currentJourney.validStartTime && currentJourney.dateOfJourney.after(currentJourney.validStartTime)) {
				if(currentJourney.validate()) {
					searchResultMap = getSearchResultMap(currentUser, currentJourney)
					jsonMessage = "Successfully executed search"
					jsonResponse = "ok"
				}
			}
			else {
				jsonMessage = "Invalid travel date and time"
			}
		}
		else {
			jsonMessage = "User is not logged in. Cannot fetch search results"
		}
		
		def jsonResponseBody = [
			"response": jsonResponse,
			"message": jsonMessage,
			"errors" : errors,
			"searchResults" : searchResultMap
		]
		render jsonResponseBody as JSON
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
	
	private updateHttpSession(JourneyRequestCommand currentJourney, Map selectedJourneyMapForCurrentJourney) {
		session.currentJourney = currentJourney
		selectedJourneyMapForCurrentJourney.each {selectedJourneyId, selectedWorkflow ->
			setSelectedJounreyInSession(currentJourney, selectedJourneyId, selectedWorkflow)
		}
	}
	private getAlreadySelectedJourneyIdsForCurrentJourney(JourneyRequestCommand currentJourney) {
		def selectedJourneyIds = journeyWorkflowService.getAlreadySelectedJourneyIdsForCurrentJourney(currentJourney)
		return selectedJourneyIds
	}
	
	def history() {
		
	}
	
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
		if(!currentJourney.id) {
			journeyManagerService.createJourney(currentUser, currentJourney)
		}
		session.currentJourney.isSaved = true
		flash.message ="Successfully saved your request"
		//redirect(controller: 'staticPage', action: "search")
		//render(view: "results", model: [currentUser: currentUser, currentJourney: currentJourney])
		chain(action: 'findMatching', model: [currentJourney: currentJourney])
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
		def workflow = journeyManagerService.saveJourneyAndInitiateWorkflow(currentJourney,matchedJourney)
		setSelectedJounreyInSession(currentJourney,matchedJourneyId, workflow)
		//render(view: "results", model: [currentUser: currentUser, currentJourney: currentJourney, journeys : journeys, numberOfRecords : numberOfRecords, isDummyData: isDummyData])
		chain(action: 'findMatching', model: [currentJourney: currentJourney])
	}

	private setSelectedJounreyInSession(def currentJourney, String matchedJourneyId, JourneyWorkflow matchedWorkflow) {
		def currentJourneyId = currentJourney.id
		def selectedJourneysMap = session.selectedJourneysMap
		def  selectedJourneyForCurrentJourneyMap=[:]
		if(!selectedJourneysMap) {
			selectedJourneysMap =[:]
			session.selectedJourneysMap = selectedJourneysMap
		}
		else {
			if(session.selectedJourneysMap.get(currentJourneyId)) {
				selectedJourneyForCurrentJourneyMap =session.selectedJourneysMap.get(currentJourneyId)
			}
		}
		selectedJourneyForCurrentJourneyMap .put(matchedJourneyId,matchedWorkflow)
		session.selectedJourneysMap.put(currentJourneyId,selectedJourneyForCurrentJourneyMap)
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
	
	def activeJourneys() {
		def workflows =[]
		def journeys =[]
		int numberOfRecords = 0
		def currentUser = getAuthenticatedUser()
		if(currentUser) {
			journeys = journeyService.findAllActiveJourneyDetailsForUser(currentUser)
			numberOfRecords = journeys?.size()
			
		}
		render(view: "activeJourneys", model: [currentUser: currentUser, journeys:journeys, numberOfRecords : numberOfRecords, workflows:workflows])
	}
	
	def myMatchedJourneys() {
		def workflows =[]
		int numberOfRecords = 0
		def currentUser = getAuthenticatedUser()
		if(currentUser) {
			workflows = journeyWorkflowService.searchWorkflowMatchedForUser(currentUser)
			numberOfRecords = workflows?.size()
		}
		render(view: "myMatchedJourneys", model: [currentUser: currentUser, numberOfRecords : numberOfRecords, workflows:workflows])
	}
	
	def backToSearchResult() {
		def currentJourney = session.currentJourney
		chain(action: 'findMatching', model: [currentJourney: currentJourney])
	
	}
	
	def acceptIncomingRequest() {
		def workflowId = params.workflowId
		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.ACCEPTED.state)
		if(params.redirectToSearch) {
			chain(action: 'findMatching', model: [currentJourney: session.currentJourney])
		}
		else {
			redirect(action: "activeJourneys")
		}
	}
	
	def rejectIncomingRequest() {
		def workflowId = params.workflowId
		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.REJECTED.state)
		if(params.redirectToSearch) {
			chain(action: 'findMatching', model: [currentJourney: session.currentJourney])
		}
		else {
			redirect(action: "activeJourneys")
		}
	}
	
	def cancelJourneyRequest() {
		def workflowId
		def currentJourney = session.currentJourney
		def requestedJourneyId = params.requestedJourneyId
		def matchedJourneyId = params.matchedJourneyId
		List requestWorkflows = journeyWorkflowService.getWorkFlowByJounreyTuple(requestedJourneyId, matchedJourneyId)
		if (requestWorkflows) {
			workflowId = requestWorkflows.get(0).id
		}
		if(!workflowId) {
			List matchedWorkflows = journeyWorkflowService.getWorkFlowByJounreyTuple(matchedJourneyId, requestedJourneyId)
			if (matchedWorkflows) {
				workflowId = matchedWorkflows.get(0).id
			}
		}
		if(!workflowId){
			log.error 'Something is wrong. Trying to cancel a request which does not exists' + params
		}
		journeyWorkflowService.updateWorkflowState(workflowId.toString(), WorkflowState.CANCELLED.state)
		chain(action: 'findMatching', model: [currentJourney: currentJourney])
	}
	
	def searchAgain() {
		def journeyId = params.journeyId 
		def indexName = ElasticSearchService.JOURNEY //params.indexName
		boolean isDriver =params.boolean('isDriver')
		//Date dateOfJourney = ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(journeyDate)
		def journey = journeyService.findJourneyById(journeyId, indexName)
		chain(action: 'findMatching', model: [currentJourney: journey])
	}
	
	def cancelOutgoingRequest() {
		def workflowId = params.workflowId
		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.CANCELLED.state)
		redirect(action: "activeJourneys")
	}
	
	def redoSearch() {
		def currentJourney = session.currentJourney
		chain(action: 'findMatching', model: [currentJourney: currentJourney])
	}
	def deleteJourney() {
		def journeyId = params.journeyId
		journeyManagerService.deleteJourney(journeyId)
		redirect(action: "activeJourneys")
	}
	
}

public class JourneyRequestCommand {
	
	String user
	String id;//will be assigned later
	String name; //Should get from user. 
	Boolean isMale; //Should get from user.
	String dateOfJourneyString; //date as string
	Date dateOfJourney; //it should get populated before validation
	String validStartTimeString; //date as string
	Date validStartTime; //it should get populated before validation
	String fromPlace;
	Double fromLatitude = -1;
	Double fromLongitude = -1;
	String toPlace;
	Double toLatitude = -1;
	Double toLongitude = -1;
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
		return "JourneyRequestCommand -> id : ${id} | name : ${name} | isDriver : ${isDriver} | dateOfJourneyString : ${dateOfJourneyString} | fromPlace : ${fromPlace} | toPlace : ${toPlace}";
	}
	
}
