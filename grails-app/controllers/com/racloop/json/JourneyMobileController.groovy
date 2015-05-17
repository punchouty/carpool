package com.racloop.json

import java.text.SimpleDateFormat;

import grails.converters.JSON
import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.ProfileBase

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.DisabledAccountException
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha256Hash
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.format.DateTimeFormat
import org.elasticsearch.common.joda.time.format.DateTimeFormatter
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;

import com.racloop.Constant;
import com.racloop.ElasticSearchService;
import com.racloop.GenericStatus;
import com.racloop.JourneyRequestCommand
import com.racloop.Profile;
import com.racloop.Sos;
import com.racloop.User
import com.racloop.domain.Journey;
import com.racloop.journey.workkflow.WorkflowState
import com.racloop.mobile.data.response.MobileResponse
import com.racloop.staticdata.StaticData;
import com.racloop.util.date.DateUtil

import static com.racloop.util.date.DateUtil.convertUIDateToElasticSearchDate

class JourneyMobileController {
	
	def journeyDataService
	def workflowDataService
	
	def search() {
		def json = request.JSON
		String dateOfJourneyString = json?.dateOfJourneyString
		String validStartTimeString = json?.validStartTimeString
		String jsonMessage = null
		String jsonResponse = "error"
		def mobileResponse = new MobileResponse()
		def errors = null
		def searchResultMap = null
		JourneyRequestCommand currentJourney = convertJsonToJourneyObject(json)
		JourneyRequestCommand currentJourneyFromRequest = request.getAttribute('currentJourney')
		if(currentJourneyFromRequest) {
			currentJourney = currentJourneyFromRequest
		}
		def currentUser = getAuthenticatedUser();
		if(!currentUser) {
			currentUser = User.findByUsername(currentJourney.user);
		}
		if(currentUser) {
			currentJourney.user = currentUser.username
			currentJourney.name = currentUser.profile.fullName
			currentJourney.isMale = currentUser.profile.isMale
			currentJourney.ip = request.remoteAddr
			boolean isValidDate = setDates(currentJourney)
			if(!isValidDate){
				mobileResponse.success = false
				mobileResponse.message = "Invalid Date"
			}
			else {
				if(currentJourney.dateOfJourney && currentJourney.validStartTime && currentJourney.dateOfJourney.after(currentJourney.validStartTime)) {
					def returnValue = workflowDataService.processSearch(currentJourney.getDateOfJourney(), currentJourney.getValidStartTime(), currentUser.profile.mobile, currentJourney.getFromLatitude(), currentJourney.getFromLongitude(), currentJourney.getToLatitude(), currentJourney.getToLongitude())
					if(returnValue instanceof Journey) {
						mobileResponse.data = ['existingJourney':returnValue.convert(),'currentJourney':currentJourney]
						mobileResponse.success = true
						mobileResponse.total = 0
						mobileResponse.message = "Existing journey found!"
						mobileResponse.existingJourney=true
					}
					else {
						def journeys = [];
						for (Journey dbJourney: returnValue) {
							journeys << dbJourney.convert()
						}
						mobileResponse.data = journeys
						mobileResponse.success = true
						mobileResponse.total = journeys.size()
						mobileResponse.existingJourney=false
					}
				}
				else {
					mobileResponse.message = "Invalid travel date and time"
					mobileResponse.success = false
					mobileResponse.total =0
				}
			}
		}
		else {
			mobileResponse.message = "User is not logged in. Cannot fetch search results"
			mobileResponse.success = false
			mobileResponse.total =0
		}
		
		
		render mobileResponse as JSON
	}

    def myJourneys() {			
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		def currentUser = getAuthenticatedUser();
		String currentTime = params.currentTime;//json?.currentTime
		log.info "myJourneys currentUser : ${currentUser.profile.email} currentTime : ${currentTime}"
		if(currentUser) {
			def journeys = journeyDataService.findMyJourneys(currentUser.profile.mobile, DateUtil.uiDateStringToJavaDate(currentTime));
			def oldJourneys = [];
			for (Journey dbJourney: journeys) {
				oldJourneys << dbJourney.convert();
			}
			mobileResponse.data = oldJourneys
			mobileResponse.success = true
			mobileResponse.total = oldJourneys?.size()
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
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		def currentUser = getAuthenticatedUser();
		String currentTime = params.currentTime;//json?.currentTime
		log.info "myHistory currentUser : ${currentUser.profile.email} currentTime : ${currentTime}"
		if(currentUser) {
			def journeys = journeyDataService.findMyHistory(currentUser.profile.mobile, DateUtil.uiDateStringToJavaDate(currentTime));
			def oldJourneys = [];
			for (Journey dbJourney: journeys) {
				oldJourneys << dbJourney.convert();
			}
			mobileResponse.data = oldJourneys
			mobileResponse.success = true
			mobileResponse.total = oldJourneys?.size()
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
		currentJourney.validStartTimeString = json?.validStartTimeString
		currentJourney.fromPlace = json?.fromPlace
		currentJourney.fromLatitude = convertToDouble(json?.fromLatitude)
		currentJourney.fromLongitude = convertToDouble(json?.fromLongitude)
		currentJourney.toPlace = json?.toPlace
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
