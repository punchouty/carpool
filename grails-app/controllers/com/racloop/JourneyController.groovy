package com.racloop

import java.util.Date;

import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.format.DateTimeFormat
import org.elasticsearch.common.joda.time.format.DateTimeFormatter

class JourneyController {
	
	public static final DateTimeFormatter UI_DATE_FORMAT = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
	def grailsApplication
	def journeyService

    def save(JourneyRequestCommand command) { 
		def currentUser = getAuthenticatedUser();
		command.name = currentUser.profile.fullName
		command.ip = request.remoteAddr
		DateTime dateOfJourney = UI_DATE_FORMAT.parseDateTime(command.dateOfJourneyString);
		DateTime validStartTime = UI_DATE_FORMAT.parseDateTime(command.validStartTimeString);
		if(dateOfJourney != null && validStartTime != null && dateOfJourney.isAfter(validStartTime)) {
			command.dateOfJourney = dateOfJourney.toDate();	
			command.validStartTime = validStartTime.toDate();
			if(command.validate()) {
				session.currentJourney = command
				redirect(action: "results")
			}
			else {
				log.warn 'Error in command : ' + params
				redirect(controller: 'staticPage', action: "search")
			}
		}
		else {
			command.errors.rejectValue("travelDateString", "invalid.travel.date", [message(code: 'travel.date', default: 'Travel Date')] as Object[],
                          "Invalid travel date")
			log.warn 'Error in command travel dates : ' + params
			redirect(controller: 'staticPage', action: "search")
		}
	}
	
	def activeJourneys() {
		def currentUser = getAuthenticatedUser();
		def activeJourneys = currentUser.journeys;
	}
	
	def results() {
		def currentUser = getAuthenticatedUser();
		def currentJourney = session.currentJourney
		def journeys = journeyService.search(currentUser, currentJourney)
		int numberOfRecords = 0;
		if(journeys.size > 0) {
			numberOfRecords = journeys.size
		}
		[currentJourney: currentJourney, journeys : journeys, numberOfRecords : numberOfRecords]
	}
}

class JourneyRequestCommand {
	
	Long id;//will be assigned later
	String name; //Should get from user. 
	String dateOfJourneyString; //date as string
	Date dateOfJourney;
	String validStartTimeString; //date as string
	Date validStartTime;
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
	
	static constraints = {
		id nullable : true
		name blank: true
		ip blank : true
	}
	
	String toString(){
		return "name : ${name} | isDriver : ${isDriver} | dateOfJourneyString : ${dateOfJourneyString} | fromPlace : ${fromPlace} | toPlace : ${toPlace}";
	}
}
