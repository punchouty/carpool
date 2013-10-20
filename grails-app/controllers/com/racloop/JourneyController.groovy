package com.racloop

import java.util.Date;

import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject

class JourneyController {
	
	def grailsApplication
	def journeyService

    def save(JourneyRequestCommand command) { 
		log.info params
		if(command.validate()) {
			println 'valid'
		}
		else {
			println 'invalid'
		}
		render 'Done'
//		def dateOfJourney = params.date('travelDate', grailsApplication.config.grails.ui.dateFormat)
//		def validStartTime = params.date('validStartTime', grailsApplication.config.grails.ui.dateFormat)
//		Journey journeyInstance = new Journey(params);
//		journeyInstance.dateOfJourney = dateOfJourney;
//		journeyInstance.validStartTime = validStartTime;
//		def currentUser = getAuthenticatedUser();
//		boolean success  = journeyService.saveJourney(currentUser, journeyInstance)
//		if(success) {
//			redirect(action: "results", params: [user : currentUser, journeyInstance: journeyInstance])
//		}
//		else {
//			redirect(controller: "staticPage", action: "home", params: [journeyInstance: journeyInstance])
//		}
	}
	
	def activeJourneys() {
		def currentUser = getAuthenticatedUser();
		def activeJourneys = currentUser.journeys;
	}
	
	def results() {
		def journeys = journeyService.search(params.user, params.journeyInstance)
		int numberOfRecords = 0;
		if(journeys.size > 0) {
			numberOfRecords = journeys.size
		}
		[journeys : journeys, numberOfRecords : numberOfRecords]
	}
}

class JourneyRequestCommand {
	
	String dateOfJourney;
	String validStartTime;
	String fromPlace;
	Double fromLatitude;
	Double fromLongitude;
	String toPlace;
	Double toLatitude;
	Double toLongitude;
	Boolean isDriver;
	Double tripDistance;
	Double tripUnit;
	
	static constraints = {
		dateOfJourney blank : false, matches : /\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d([+-][0-2]\d:[0-5]\d|Z)/ //http://stackoverflow.com/questions/3143070/javascript-regex-iso-datetime
		validStartTime blank : false, matches : /\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d([+-][0-2]\d:[0-5]\d|Z)/
		fromPlace blank : false
		fromLatitude nullable : false
		fromLongitude nullable : false
		toPlace blank : false
		toLatitude nullable : false
		toLongitude nullable : false
		isDriver nullable : false
		tripDistance nullable : false
		tripUnit nullable : false
	}
	
	String toString(){
		return "isDriver : ${isDriver} | date : ${dateOfJourney} | fromPlace : ${fromPlace} | toPlace : ${toPlace}";
	}
}
