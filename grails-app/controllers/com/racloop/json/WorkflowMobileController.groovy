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
import com.racloop.GenericUtil;
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

class WorkflowMobileController {
	
	def journeyDataService
	def workflowDataService
	def journeySearchService
	
	def request() {
		def json = request.JSON
		log.info("json : ${json}")	
		String jsonMessage = null
		String jsonResponse = "error"
		def mobileResponse = new MobileResponse()
		render mobileResponse as JSON
	}
}
