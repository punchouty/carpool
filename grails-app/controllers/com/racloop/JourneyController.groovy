package com.racloop

import static com.racloop.util.date.DateUtil.convertUIDateToElasticSearchDate

import org.elasticsearch.common.joda.time.DateTime
import org.springframework.web.servlet.ModelAndView

import com.racloop.domain.Journey
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
	def workflowDataService
	

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
				def mobileResponse = journeySearchService.executeSearch(currentJourney)
				def existingJourney = mobileResponse.data.get('existingJourney')
				if(existingJourney) {
					session.currentJourney = currentJourney
					return new ModelAndView("existingJourney", ['currentJourney': currentJourney, 'existingJourney':existingJourney])
				}
				session.currentJourney = mobileResponse.currentJourney
				render(view: "results", model: ['searchResults': mobileResponse, 'currentUser': currentUser])
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
			currentJourney.mobile = currentUser.profile.mobile
			currentJourney.photoUrl = currentUser.getPhotoUrl()
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
		def workflows =[]
		def journeys =[]
		int numberOfRecords = 0
		def currentUser = getRacloopAuthenticatedUser()
		if(currentUser) {
			def result =journeyDataService.findMyHistory(currentUser.profile.mobile, GenericUtil.getCurrentDateInIST())
			result.each {it->
				journeys << journeyDataService.findChildJourneys(it.getId())
			}
			numberOfRecords = result?.size()
			
		}
		render(view: "history", model: [currentUser: currentUser, journeys:journeys, numberOfRecords : numberOfRecords, workflows:workflows])
		
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
		def userId = params.currentUser
		def currentUser = getRacloopAuthenticatedUser();
		def currentJourney = session.currentJourney
		if(!currentJourney.user) {
			setUserInformation(currentUser, currentJourney)
			session.currentJourney = currentJourney
		}
		if(!userId) {
			def mobileResponse = journeySearchService.executeSearch(currentJourney)
			def existingJourney = mobileResponse.data.get('existingJourney')
			if(existingJourney) {
				forward action: 'findMatching', model: [currentJourney: currentJourney]
				return
			}
		}
		Journey journey = Journey.convert(currentJourney)
		if(!journey.id) {
			journeyDataService.createJourney(journey)
		}
		if(journey.id){
			session.currentJourney.isSaved = true
			session.currentJourney.id = journey.getId()
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
	def requestService() {
		def currentJourney = session.currentJourney
		def currentUser = getRacloopAuthenticatedUser()
		def matchedJourneyId = params.matchedJourneyId
		boolean isDummy =params.boolean('dummy')
		if(!currentJourney.user) {
			setUserInformation(currentUser, currentJourney)
			session.currentJourney = currentJourney
		}
		Journey myJourney = Journey.convert(currentJourney)
		if(workflowDataService.validateInvitationRequest(myJourney, matchedJourneyId)) {
			log.info("Requesting service. currentJourney.id : " + currentJourney.id + ", otherJourneyId : " + matchedJourneyId)
			if(currentJourney.isNewJourney()) {
				Journey savedJourney = workflowDataService.requestJourneyAndSave(myJourney, matchedJourneyId);
				currentJourney.id = savedJourney.getId()
			}
			else {
				workflowDataService.requestJourney(currentJourney.id, matchedJourneyId);
			}
		}
		else {
			flash.error = "Sorry, you cannot invite yourself."
		}
		forward action: 'activeJourneys'
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
		def matchedJourney = journeyDataService.findJourney(matchedJourneyId)
		if(!matchedJourney){
			isDummy = true
			matchedJourney = journeyDataService.findJourneyFromElasticSearch(matchedJourneyId, isDummy)
		}
		def matchedUser = User.findByUsername(matchedJourney.getEmail())
		boolean isThresholdReached = false
		/*int threshHold = Integer.valueOf(grailsApplication.config.grails.max.active.requests)
		if(currentJourney?.id) {
			requestCountMap = journeyService.findCountOfAllWorkflowRequestForAJourney(currentJourney.id)
		}
		def matchedJourney = journeyService.findMatchedJourneyById(matchedJourneyId, isDummy)
		def matchedUser = User.findByUsername(matchedJourney.user)
		boolean isThresholdReached = requestCountMap.totalCount >= threshHold
		if(isThresholdReached) {
			flash.error = "Sorry, you cannot send the request as you have reached the threshold limit. You already have ${requestCountMap.outgoingCount} active outgoing requests and ${requestCountMap.incomingCount} active incoming requests for this journey."
		}*/
		[matchedJourney: matchedJourney.convert(), matchedUser:matchedUser, isDummy: isDummy, showRequestButton:!isThresholdReached]
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
			def result =journeyDataService.findMyJourneys(currentUser.profile.mobile, GenericUtil.getCurrentDateInIST())
			result.each {it->
				journeys << journeyDataService.findChildJourneys(it.getId())
			}
			numberOfRecords = result?.size()
			
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
		def pairId = params.pairId
		workflowDataService.acceptRequest(pairId)
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
		def pairId = params.pairId
		workflowDataService.rejectRequest(pairId)
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
	@Deprecated
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
		def journey = journeyDataService.findJourney(journeyId)
		//chain(action: 'findMatching', model: [currentJourney: journey])
		forward action: 'findMatching', model: [currentJourney: journey.convert()]
	}
	
	
	def cancelRequest(){
		def pairId = params.pairId
		def myJourneyId = params.myJourneyId
		def currentUser = getRacloopAuthenticatedUser();
		workflowDataService.cancelMyRequest(pairId, myJourneyId, currentUser.getUsername())
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
		workflowDataService.cancelMyJourney(journeyId)
		redirect(action: "activeJourneys")
	}
	
	def searchWithExistingJourney() {
		def existingJourneyId = params.existingJourneyId
		def newJourney = params.newJourney
		def currentJourney
		def currentUser= getRacloopAuthenticatedUser()
		if("newJourney".equals(newJourney)) {
			workflowDataService.cancelMyJourney(existingJourneyId)
			currentJourney = new JourneyRequestCommand()
			bindData(currentJourney, params, [exclude: ['existingJourneyId', 'newJourney']])
			setDates(currentJourney)
			setUserInformation(currentUser, currentJourney)
			Journey journey = Journey.convert(currentJourney);
			journey.id = null;
			journeyDataService.createJourney(journey);
			currentJourney.id = journey.id;
		}
		else {
			Journey journey = journeyDataService.findJourney(existingJourneyId)
			currentJourney = journey.convert()
		}
		//chain(action: 'findMatching', model: [currentJourney: currentJourney])
		forward action: 'findMatching', model: [currentJourney: currentJourney]
		
	}
	
	def searchRouteAgain(){
		String journeyId = params.journeyId
		Journey journey = journeyDataService.findJourney(journeyId)
		if(journey){
			resetJourney(journey)
		}
		
		forward controller: 'userSession', action: 'search', model: [journeyInstance: journey?.convert()]
	}
	
	
	private void resetJourney(Journey journey) {
		journey.id = null
		//journey.dateOfJourneyString = null
		//journey.dateOfJourney = null
	}
	
	def getSibling(){
		String journeyId = params.journeyId
		List journeys = journeyDataService.findSiblingJourneys(journeyId)
		render(view:'siblingJourney', model: [journeys: journeys])
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
	Boolean isTaxi;
	Double tripDistance;
	String tripUnit;
	String ip; //should get from request
	Date createdDate = new Date();
	Boolean isSaved = false
	String photoUrl
	String actionOnSearchUi = "SEARCH"
	Integer tripTimeInSeconds
	
	static constraints = {
		id nullable : true
		/*name blank: true
		ip blank : true*/
	}
	
	String toString(){
		return "JourneyRequestCommand -> id : ${id} | name : ${name} | isTaxi : ${isTaxi} | dateOfJourneyString : ${dateOfJourneyString} | fromPlace : ${from} | toPlace : ${to}";
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
