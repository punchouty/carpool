package com.racloop

import static com.racloop.util.date.DateUtil.convertUIDateToElasticSearchDate
import grails.converters.JSON

import org.elasticsearch.common.joda.time.DateTime
import org.springframework.web.servlet.ModelAndView

import com.racloop.domain.Journey;
import com.racloop.journey.workkflow.WorkflowState

class JourneyController {
	
	// Date format for date.js library - dd MMMM yyyy    hh:mm tt - map.js
	// This is different from that of datetime plugin which is - dd MM yyyy    HH:ii P - search.gsp
	// This in turn is different from Joda date format - dd MMMM yyyy    hh:mm a - JourneyController.groovy
	def grailsApplication
	def journeyService
	def journeyWorkflowService
	def journeyManagerService
	def journeySearchService
	def journeyDataService
	

	/**
	 * Main Search from web front end
	 * @param currentJourney
	 * @return
	 */
    def findMatching(JourneyRequestCommand currentJourney) {
		
		JourneyRequestCommand currentJourneyFromRequest = request.getAttribute('currentJourney')
		if(currentJourneyFromRequest) {
			currentJourney = currentJourneyFromRequest
		}
		def currentUser = getRacloopAuthenticatedUser();
		setUserInformation(currentUser,currentJourney)
		currentJourney.ip = request.remoteAddr
		boolean isValidDate = setDates(currentJourney)
		if(!isValidDate){
			log.error 'Error in Date : ' + params
			flash.error = "Invalid Journey Date"
			redirect(controller: 'userSession', action: "search")
			return
		}
		if(currentJourney.isFromPageReload() && session.currentJourney) {
			currentJourney = session.currentJourney
		}
		if(currentJourney.dateOfJourney && currentJourney.validStartTime && currentJourney.dateOfJourney.after(currentJourney.validStartTime)) {
			if(currentJourney.validate()) {
				session.currentJourney = currentJourney
				def mobileResponse = journeySearchService.executeSearch(currentJourney)
				render(view: "results", model: ['searchResults': mobileResponse])
			}
			else {
				log.warn 'Error in command : ' + params
				redirect(controller: 'userSession', action: "search")
			}
		}
		else {
			if(!currentJourney.isFromPageReload()) {
				currentJourney.errors.rejectValue("dateOfJourneyString", "invalid.travel.date", [message(code: 'travel.date', default: 'Travel Date')] as Object[],
					"Invalid travel date")
				  log.warn 'Error in command travel dates : ' + params
				  flash.error = "Invalid Input Data"
			}
			
			redirect(controller: 'userSession', action: "search")
		}
	}
	
	public findOppositeSideMatching() {
		def currentUser = getRacloopAuthenticatedUser();
		def searchResultMap = journeyService.getSearchResults(currentUser, currentJourney) //getSearchResultMap(currentUser, currentJourney)
		render(view: "results", model: ['searchResults': searchResultMap])
	}
	
	
	private setUserInformation(User currentUser, JourneyRequestCommand currentJourney) {
		if(currentUser) {
			currentJourney.user = currentUser.username
			currentJourney.name = currentUser.profile.fullName
			currentJourney.isMale = currentUser.profile.isMale
		}
	}
	
	private boolean setDates(JourneyRequestCommand currentJourney) {
		boolean success = true
		try {
			if(currentJourney.dateOfJourneyString) {
				currentJourney.dateOfJourney = convertUIDateToElasticSearchDate(currentJourney.dateOfJourneyString).toDate()
			}
			if(currentJourney.validStartTimeString) {
				currentJourney.validStartTime = new DateTime().toDate()//convertUIDateToElasticSearchDate(currentJourney.validStartTimeString).toDate()
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
		finally {
			return success
		}
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
	
	
	
	/**
	 * History of user's Journeys
	 *  
	 */
	def history() {
		def journeys =[]
		int numberOfRecords = 0
		def currentUser = getRacloopAuthenticatedUser()
		if(currentUser) {
			journeys = journeyService.findHistoricJourneyDetailsForUser(currentUser)
			numberOfRecords = journeys?.size()
			
		}
		render(view: "history", model: [currentUser: currentUser, journeys:journeys, numberOfRecords : numberOfRecords])
		
	}
	
//	def results() {
//		def currentUser = getRacloopAuthenticatedUser();
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
	
	/**
	 * Save Journey - if no result found
	 * @return
	 */
	def newJourney() {
		def currentUser = getRacloopAuthenticatedUser();
		def currentJourney = session.currentJourney
		Journey journey = Journey.convert(currentJourney)
		if(!journey.id) {
			journeyDataService.createJourney(journey)
		}
		if(journey.id){
		session.currentJourney.isSaved = true
		session.currentJourney.id = journey.id
		flash.message ="Successfully saved your request"
		}
		else {
			flash.message ="Some problem in saving your request"
		}
		//redirect(controller: 'staticPage', action: "search")
		//render(view: "results", model: [currentUser: currentUser, currentJourney: currentJourney])
		//chain(action: 'findMatching', model: [currentJourney: currentJourney])
		forward action: 'findMatching', model: [currentJourney: currentJourney]
	}
	
	/**
	 * Confirm Journey - search result -> confirm screen -> confirm journey
	 * @param myJourney
	 * @return
	 */
	def requestService(JourneyRequestCommand myJourney) {
		def currentJourney = session.currentJourney
		def currentUser = getRacloopAuthenticatedUser()
		def matchedJourneyId = params.matchedJourneyId
		boolean isDummy =params.boolean('dummy')
		if(!currentJourney.user) {
			currentJourney.user = currentUser.username
			currentJourney.name = currentUser.profile.fullName
			currentJourney.isMale = currentUser.profile.isMale
			session.currentJourney = currentJourney
		}
		def matchedJourney = journeyService.findMatchedJourneyById(matchedJourneyId, isDummy)
		def workflow = journeyManagerService.saveJourneyAndInitiateWorkflow(currentJourney,matchedJourney)
		//render(view: "results", model: [currentUser: currentUser, currentJourney: currentJourney, journeys : journeys, numberOfRecords : numberOfRecords, isDummyData: isDummyData])
		//chain(action: 'findMatching', model: [currentJourney: currentJourney])
		forward action: 'findMatching', model: [currentJourney: currentJourney]
	}
	
	/**
	 * Request Journey - from search results of main search screen
	 * @return
	 */
	def selectedJourney(){
		def requestCountMap = [:]
		def currentJourney = session.currentJourney
		def matchedJourneyId = params.matchedJourneyId
		boolean isDummy =params.boolean('dummy')
		int threshHold = Integer.valueOf(grailsApplication.config.grails.max.active.requests)
		if(currentJourney?.id) {
			requestCountMap = journeyService.findCountOfAllWorkflowRequestForAJourney(currentJourney.id)
		}
		def matchedJourney = journeyService.findMatchedJourneyById(matchedJourneyId, isDummy)
		def matchedUser = User.findByUsername(matchedJourney.user)
		boolean isThresholdReached = requestCountMap.totalCount >= threshHold
		if(isThresholdReached) {
			flash.error = "Sorry, you cannot send the request as you have reached the threshold limit. You already have ${requestCountMap.outgoingCount} active outgoing requests and ${requestCountMap.incomingCount} active incoming requests for this journey."
		}
		[matchedJourney: matchedJourney, matchedUser:matchedUser, isDummy: isDummy, showRequestButton:!isThresholdReached]
	}
	
	/**
	 * Active journey for user
	 * @return
	 */
	def activeJourneys() {
		def workflows =[]
		def journeys =[]
		int numberOfRecords = 0
		def currentUser = getRacloopAuthenticatedUser()
		if(currentUser) {
			journeys = journeyService.findAllActiveJourneyDetailsForUser(currentUser)
			numberOfRecords = journeys?.size()
			
		}
		render(view: "activeJourneys", model: [currentUser: currentUser, journeys:journeys, numberOfRecords : numberOfRecords, workflows:workflows])
	}
	
	
	/**
	 * TODO - need to find where it is used
	 * @return
	 */
	def backToSearchResult() {
		def currentJourney = session.currentJourney
		//chain(action: 'findMatching', model: [currentJourney: currentJourney])
		forward action: 'findMatching', model: [currentJourney: currentJourney]
	
	}
	
	/**
	 * Accept action from user - Work flow
	 * @return
	 */
	def acceptIncomingRequest() {
		def workflowId = params.workflowId
		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.ACCEPTED.state)
		if(params.redirectToSearch) {
			//chain(action: 'findMatching', model: [currentJourney: session.currentJourney])
			forward action: 'findMatching', model: [currentJourney: session.currentJourney]
		}
		else {
			redirect(action: "activeJourneys")
		}
	}
	
	/**
	 * Reject action from user - Work flow
	 * @return
	 */
	def rejectIncomingRequest() {
		def workflowId = params.workflowId
		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.REJECTED.state)
		if(params.redirectToSearch) {
			//chain(action: 'findMatching', model: [currentJourney: session.currentJourney])
			forward action: 'findMatching', model: [currentJourney: session.currentJourney]
		}
		else {
			redirect(action: "activeJourneys")
		}
	}
	
	/**
	 * Cancel from other user - Work flow
	 * @return
	 */
	def cancelJourneyRequest() {
		def workflowId
		def currentJourney = session.currentJourney
		def requestedJourneyId = params.requestedJourneyId
		def matchedJourneyId = params.matchedJourneyId
		List requestWorkflows = journeyWorkflowService.getWorkFlowByJounreyTuple(requestedJourneyId, matchedJourneyId)
		if (requestWorkflows) {
			workflowId = requestWorkflows.get(0).id
			journeyWorkflowService.updateWorkflowState(workflowId.toString(), WorkflowState.CANCELLED_BY_REQUESTER.state)
		}
		if(!workflowId) {
			List matchedWorkflows = journeyWorkflowService.getWorkFlowByJounreyTuple(matchedJourneyId, requestedJourneyId)
			if (matchedWorkflows) {
				workflowId = matchedWorkflows.get(0).id
				journeyWorkflowService.updateWorkflowState(workflowId.toString(), WorkflowState.CANCELLED.state)
			}
		}
		if(!workflowId){
			log.error 'Something is wrong. Trying to cancel a request which does not exists' + params
		}
		
		//chain(action: 'findMatching', model: [currentJourney: currentJourney])
		forward action: 'findMatching', model: [currentJourney: currentJourney]
	}
	
	/**
	 * TODO Where it is used?
	 * @return
	 */
	def searchAgain() {
		def journeyId = params.journeyId 
		boolean isDriver =params.boolean('isDriver')
		def journey = journeyService.findJourneyById(journeyId)
		//chain(action: 'findMatching', model: [currentJourney: journey])
		forward action: 'findMatching', model: [currentJourney: journey]
	}
	
	/**
	 * Cancel by owner - Work flow
	 * @return
	 */
	def cancelOutgoingRequest() {
		def workflowId = params.workflowId
		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.CANCELLED_BY_REQUESTER.state)
		redirect(action: "activeJourneys")
	}
	
	def cancelIncomingRequest() {
		def workflowId = params.workflowId
		journeyWorkflowService.updateWorkflowState(workflowId, WorkflowState.CANCELLED.state)
		redirect(action: "activeJourneys")
	}
	
	/**
	 * TODO Where it is used?
	 * @return
	 */
	def redoSearch() {
		def currentJourney = session.currentJourney
		//chain(action: 'findMatching', model: [currentJourney: currentJourney])
		forward action: 'findMatching', model: [currentJourney: currentJourney]
	}
	
	/**
	 * All request cancel
	 * @return
	 */
	def deleteJourney() {
		def journeyId = params.journeyIdToBeDeleted
		journeyManagerService.deleteJourney(journeyId)
		redirect(action: "activeJourneys")
	}
	
	def searchWithExistingJourney() {
		def existingJourneyId = params.existingJourneyId
		def newJourney = params.newJourney
		def currentJourney
		if("newJourney".equals(newJourney)) {
			journeyManagerService.deleteJourney(existingJourneyId)
			currentJourney = new JourneyRequestCommand()
			bindData(currentJourney, params, [exclude: ['existingJourneyId', 'newJourney']])
		}
		else {
			currentJourney = journeyService.findJourneyById(existingJourneyId, false)
		}
		//chain(action: 'findMatching', model: [currentJourney: currentJourney])
		forward action: 'findMatching', model: [currentJourney: currentJourney]
		
	}
	
	def searchRouteAgain(){
		String journeyId = params.journeyId
		JourneyRequestCommand journey = journeyService.findJourneyById(journeyId, false)
		if(journey){
			resetJourney(journey)
		}
		forward controller: 'userSession', action: 'search', model: [journeyInstance: journey]
	}
	
	
	private void resetJourney(JourneyRequestCommand journey) {
		journey.id = null
		journey.dateOfJourneyString = null
		journey.dateOfJourney = null
	}
	
}

public class JourneyRequestCommand {
	
	String user
	String mobile
	String email
	String id;//will be assigned later
	String name; //Should get from user. 
	Boolean isMale; //Should get from user.
	String dateOfJourneyString; //date as string
	Date dateOfJourney; //it should get populated before validation
	String validStartTimeString; //date as string
	Date validStartTime; //it should get populated before validation
	String from;
	Double fromLatitude = -1;
	Double fromLongitude = -1;
	String to;
	Double toLatitude = -1;
	Double toLongitude = -1;
	Boolean isDriver;
	Double tripDistance;
	String tripUnit;
	String ip; //should get from request
	Date createdDate = new Date();
	Boolean isSaved = false
	String photoUrl
	String actionOnSearchUi = "SEARCH"
	
	static constraints = {
		id nullable : true
		/*name blank: true
		ip blank : true*/
	}
	
	String toString(){
		return "JourneyRequestCommand -> id : ${id} | name : ${name} | isDriver : ${isDriver} | dateOfJourneyString : ${dateOfJourneyString} | fromPlace : ${from} | toPlace : ${to}";
	}
	
	boolean isNewJourney() {
		boolean isNew = (!id && !isSaved)?true:false
		return isNew
	} 
	
	boolean isFromPageReload(){
		boolean isFromPageReload = false
		if(!dateOfJourney){
			isFromPageReload = true
		}
		return isFromPageReload
	}
	
}
