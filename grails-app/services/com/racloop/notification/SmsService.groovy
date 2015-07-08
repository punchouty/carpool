package com.racloop.notification

import grails.plugin.jms.Queue
import grails.transaction.Transactional
import groovy.text.Template

import com.racloop.Constant
import com.racloop.SmsFailure
import com.racloop.journey.workkflow.WorkflowStatus


@Transactional
class SmsService {
	
	static exposes = ['jms']
	def grailsApplication
	def jmsService
	def rest
	Map<String, Template> templates = new HashMap<String, Template>();
	public static final String VERIFICATOIN_KEY = "verification_key";
	String urlPrefixIndividual = null;
	String urlPrefixBulk = null;
	String adminSosContactOne = null;
	String adminSosContactTwo = null;
	
	def init() {// Initialized in Bootstrap.groovy
		def engine = new groovy.text.SimpleTemplateEngine()
		templates.put(VERIFICATOIN_KEY, engine.createTemplate(grailsApplication.config.sms.templates.verification));
		templates.put(WorkflowStatus.REQUESTED.status, engine.createTemplate(grailsApplication.config.sms.templates.newRequest));
		templates.put(WorkflowStatus.ACCEPTED.status, engine.createTemplate(grailsApplication.config.sms.templates.acceptRequest));
		templates.put(WorkflowStatus.REJECTED.status, engine.createTemplate(grailsApplication.config.sms.templates.rejectRequest));
		templates.put(WorkflowStatus.CANCELLED.status, engine.createTemplate(grailsApplication.config.sms.templates.cancelRequest));
		templates.put(WorkflowStatus.CANCELLED_BY_REQUESTER.status, engine.createTemplate(grailsApplication.config.sms.templates.cancelRequest));
		templates.put(Constant.SOS_KEY, engine.createTemplate(grailsApplication.config.sms.templates.sos));
		templates.put(Constant.SOS_USER_KEY, engine.createTemplate(grailsApplication.config.sms.templates.sosUser));
		templates.put(Constant.SOS_ADMIN_KEY, engine.createTemplate(grailsApplication.config.sms.templates.sosAdmin));
		templates.put(Constant.NEW_PASSWORD_KEY, engine.createTemplate(grailsApplication.config.sms.templates.newPassword));
		urlPrefixIndividual = grailsApplication.config.sms.url + '?&UserName=' + grailsApplication.config.sms.username + '&Password=' + grailsApplication.config.sms.password + '&Type=Individual&Mask=' + grailsApplication.config.sms.mask
		urlPrefixBulk = grailsApplication.config.sms.url + '?&UserName=' + grailsApplication.config.sms.username + '&Password=' + grailsApplication.config.sms.password + '&Type=Bulk&Mask=' + grailsApplication.config.sms.mask
		adminSosContactOne = grailsApplication.config.grails.sms.emergency.one
		adminSosContactTwo = grailsApplication.config.grails.sms.emergency.two
	}
	
	@Queue(name= Constant.NOTIFICATION_SMS_QUEUE) //also defined in Constant.java. Grails issue //TODO can we use Constant.MOBILE_VERIFICATION_QUEUE ????
	def sendWorkflowSms(def messageMap) {
		def message = null;
		String to = messageMap['to'] 
		String state = messageMap['state']
		switch(state) {
			case WorkflowStatus.REQUESTED.status : // send request from search result - resultant user should receive email and sms and push
				message = templates.get(WorkflowStatus.REQUESTED.status).make(messageMap).toString();
				break
			case WorkflowStatus.ACCEPTED.status : // other user accepted request
				message = templates.get(WorkflowStatus.ACCEPTED.status).make(messageMap).toString();
				break
			case WorkflowStatus.REJECTED.status : // other user rejected request
				message = templates.get(WorkflowStatus.REJECTED.status).make(messageMap).toString();
				break
			case WorkflowStatus.CANCELLED.status : // i am canceling previous accepted request
				message = templates.get(WorkflowStatus.CANCELLED.status).make(messageMap).toString();
				break
			case WorkflowStatus.CANCELLED_BY_REQUESTER.status : // i am canceling my own earlier request 
				message = templates.get(WorkflowStatus.CANCELLED_BY_REQUESTER.status).make(messageMap).toString();
				break
			default :
				log.error "No state worklfow detected $state"

		}
		if(message) {
			String restUrl = urlPrefixIndividual + '&To=' + to + '&Message=' + message;
			sendMessage(restUrl, message, to);
		}
	}
	
	@Queue(name= Constant.MOBILE_VERIFICATION_QUEUE) //also defined in Constant.java. Grails issue //TODO can we use Constant.MOBILE_VERIFICATION_QUEUE ????
    def sendVerificationSms(def messageMap) {
		String mobile = messageMap[Constant.MOBILE_KEY]
		String verificationCode = messageMap[Constant.VERIFICATION_CODE_KEY]
		def message = templates.get(VERIFICATOIN_KEY).make(['verificationCode' : verificationCode]).toString();
		String restUrl = urlPrefixIndividual + '&To=' + mobile + '&Message=' + message;
		log.info('Sending SMS to sms provider with URL : ' + restUrl)
		sendMessage(restUrl, message, mobile);
    }
	
	@Queue(name= Constant.MOBILE_SOS_QUEUE) 
	def sendSos(def messageMap) {
		String mobile = messageMap[Constant.MOBILE_KEY]
		String email = messageMap[Constant.EMAIL_KEY]
		String fullName = messageMap[Constant.SOS_NAME_KEY]
		String emergencyContactOne = messageMap[Constant.EMERGENCY_CONTACT_ONE_KEY]
		String emergencyContactTwo = messageMap[Constant.EMERGENCY_CONTACT_TWO_KEY]
		String journeyIds = messageMap[Constant.JOURNEY_IDS_KEY]
		String latitude = messageMap[Constant.SOS_USER_LATITUDE]
		String longitude = messageMap[Constant.SOS_USER_LONGITUDE]
		
		def to = emergencyContactOne
		def message = templates.get(Constant.SOS_KEY).make(['name' : fullName, 'lat' : latitude, 'lng' : longitude]).toString();
		if(to != null && message != null) {
			String restUrl = urlPrefixIndividual + '&To=' + to + '&Message=' + message;
			sendMessage(restUrl, message, to);
		}
		
		to = emergencyContactTwo
		if(to != null && message != null) {
			String restUrl = urlPrefixIndividual + '&To=' + to + '&Message=' + message;
			sendMessage(restUrl, message, to);
		}
		to = mobile
		message = templates.get(Constant.SOS_USER_KEY).make(['name' : fullName, 'lat' : latitude, 'lng' : longitude]).toString();
		if(to != null && message != null) {
			String restUrl = urlPrefixIndividual + '&To=' + to + '&Message=' + message;
			sendMessage(restUrl, message, to);
		}
		
		to = adminSosContactOne
		message = templates.get(Constant.SOS_ADMIN_KEY).make([
			'name' : fullName, 
			'mobile' : mobile, 
			'email' : email, 
			'emergencyContactOne' : emergencyContactOne, 
			'emergencyContactTwo' : emergencyContactTwo, 
			'lat' : latitude, 
			'lng' : longitude, 
			'journeyIds' : journeyIds]).toString();
		if(to != null && message != null) {
			String restUrl = urlPrefixIndividual + '&To=' + to + '&Message=' + message;
			sendMessage(restUrl, message, to);
		}
		
		to = adminSosContactTwo
		if(to != null && message != null) {
			String restUrl = urlPrefixIndividual + '&To=' + to + '&Message=' + message;
			sendMessage(restUrl, message, to);
		}
	}
	
	@Queue(name= Constant.NOTIFICATION_MOBILE_FORGOT_PASSWORD_QUEUE)
	def sendForgotPassword(def messageMap) {
		String mobile = messageMap[Constant.MOBILE_KEY]
		String newPassword = messageMap[Constant.NEW_PASSWORD_KEY]
		def message = templates.get(Constant.NEW_PASSWORD_KEY).make(['newPassword' : newPassword]).toString();
		String restUrl = urlPrefixIndividual + '&To=' + mobile + '&Message=' + message;
		log.info('Sending SMS to sms provider with URL : ' + restUrl);
		sendMessage(restUrl, message, mobile);
	}
	
	def sendMessage(String restUrl, String message, String mobile) {
		Boolean smsEnabled = grailsApplication.config.grails.sms.enable
		if(smsEnabled) {
			def resp = rest.get(restUrl);
			if(resp.getStatus() != 200) {
				log.error ('SMS failure with service provider')
				log.info('Saving failure message to database')
				SmsFailure smsFailure = new SmsFailure();
				smsFailure.mobile = mobile
				smsFailure.message = message
				smsFailure.restUrl = restUrl
				smsFailure.serverResponse = resp.text
				smsFailure.save();
				return;
			}
		}
		else {
			log.info ("Mobile : ${mobile}, Message : ${message}")
		}
	}
}
