package com.racloop

import java.util.Date;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.format.DateTimeFormat
import org.elasticsearch.common.joda.time.format.DateTimeFormatter

class JourneyController {
	
	public static final DateTimeFormatter UI_DATE_FORMAT = DateTimeFormat.forPattern("dd-MMMM-yyyy-hh:mm a");
	def grailsApplication
	def journeyService

    def findMatching(JourneyRequestCommand currentJourney) { 
		def currentUser = getAuthenticatedUser();
		boolean isDummyData = false
		currentJourney.name = currentUser.profile.fullName
		currentJourney.ip = request.remoteAddr
		DateTime dateOfJourney = UI_DATE_FORMAT.parseDateTime(currentJourney.dateOfJourneyString);
		DateTime validStartTime = UI_DATE_FORMAT.parseDateTime(currentJourney.validStartTimeString);
		if(dateOfJourney != null && validStartTime != null && dateOfJourney.isAfter(validStartTime)) {
			currentJourney.dateOfJourney = dateOfJourney.toDate();	
			currentJourney.validStartTime = validStartTime.toDate();
			if(currentJourney.validate()) {
				session.currentJourney = currentJourney
				def journeys = journeyService.search(currentUser, currentJourney)
				int numberOfRecords = 0;
				def names = [];
				if(journeys.size > 0) {
					numberOfRecords = journeys.size
				}
				else {
					journeys = journeyService.getDummyData(currentUser, currentJourney)
					numberOfRecords = journeys.size
					isDummyData = true;
				}
				render(view: "results", model: [currentUser: currentUser, currentJourney: currentJourney, journeys : journeys, numberOfRecords : numberOfRecords, isDummyData: isDummyData])
				
			}
			else {
				log.warn 'Error in command : ' + params
				redirect(controller: 'staticPage', action: "search")
			}
		}
		else {
			currentJourney.errors.rejectValue("travelDateString", "invalid.travel.date", [message(code: 'travel.date', default: 'Travel Date')] as Object[],
                          "Invalid travel date")
			log.warn 'Error in command travel dates : ' + params
			redirect(controller: 'staticPage', action: "search")
		}
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
		Journey journey = currentJourney.getJourney();
		journeyService.saveJourney(currentUser, journey)
		journeyService.makeSearchable(journey)
		session.currentJourney.isSaved = true
		flash.message "Successfully saved your request"
		redirect(controller: 'staticPage', action: "search")
	}
}

class JourneyRequestCommand {
	
	Long id;//will be assigned later
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
		name blank: true
		ip blank : true
	}
	
	String toString(){
		return "JourneyRequestCommand -> name : ${name} | isDriver : ${isDriver} | dateOfJourneyString : ${dateOfJourneyString} | fromPlace : ${fromPlace} | toPlace : ${toPlace}";
	}
}
