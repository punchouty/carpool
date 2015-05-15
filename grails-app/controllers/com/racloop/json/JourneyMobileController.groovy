package com.racloop.json

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
	
	def shiroSecurityManager
	def userService
	def userManagerService
	def journeyManagerService
	def journeyService
	def journeyWorkflowService
	def jmsService
	LinkGenerator grailsLinkGenerator
	//static Map allowedMethods = [ login: 'POST', logout : 'POST', signup : 'POST', changePassword : 'POST', forgotPassword : 'POST' ] //TODO

    def index() { }
}
