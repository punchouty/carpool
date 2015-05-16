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
import com.racloop.journey.workkflow.WorkflowState
import com.racloop.mobile.data.response.MobileResponse
import com.racloop.staticdata.StaticData;
import com.racloop.util.date.DateUtil

import static com.racloop.util.date.DateUtil.convertUIDateToElasticSearchDate

class JourneyMobileController {
	
	def journeyDataService

    def myJourneys() {			
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		def currentUser = getAuthenticatedUser();
		String currentTime = params.currentTime;//json?.currentTime
		log.info "myJourneys currentUser : ${currentUser.profile.email} currentTime : ${currentTime}"
		if(currentUser) {
			//ISODateTimeFormat.dateTimeNoMillis();
			def journeys = journeyDataService.findMyJourneys(currentUser.profile.mobile, uiToDynamoDbFormat(currentTime));
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
		def mobileResponse = new MobileResponse()
		def json = request.JSON
		def currentUser = getAuthenticatedUser();
		String currentTime = params.currentTime;//json?.currentTime
		log.info "myHistory currentUser : ${currentUser.profile.email} currentTime : ${currentTime}"
		if(currentUser) {
			def journeys = journeyDataService.findMyHistory(currentUser.profile.mobile, uiToDynamoDbFormat(currentTime));
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
	
	private String uiToDynamoDbFormat(String source) {
		SimpleDateFormat uiFormatter = new SimpleDateFormat(Constant.DATE_FORMAT_UI);
		Date date = uiFormatter.parse(source);
		SimpleDateFormat dBFormatter = new SimpleDateFormat(Constant.DATE_FORMAT_DYNAMODB);
		dBFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String output = dBFormatter.format(date);
		log.info "source time ${source}, output time ${output}"
		return output;
	}
}
