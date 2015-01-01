package com.racloop.notification

import java.util.HashMap;
import java.util.Map;

import com.racloop.Constant;
import com.racloop.SmsFailure;
import com.racloop.journey.workkflow.WorkflowState;

import grails.plugin.jms.Queue;
import grails.transaction.Transactional
import groovy.text.Template;

import org.springframework.web.util.UriUtils;


@Transactional
class SmsService {
	
	static exposes = ['jms']
	def grailsApplication
	def jmsService
	def rest
	Map<String, Template> templates = new HashMap<String, Template>();
	public static final String VERIFICATOIN_KEY = "verification_key";
	public static final String NEW_KEY = "new_key";
	public static final String ACCEPT_KEY = "accept_key";
	public static final String REJECT_KEY = "reject_key";
	public static final String CANCEL_KEY = "cancel_key";
	public static final String CANCEL_BY_REQUESTOR_KEY = "cancel_by_requestor_key";
	String urlPrefixIndividual = null;
	String urlPrefixBulk = null;
	
	def init() {// Initialized in Bootstrap.groovy
		def engine = new groovy.text.SimpleTemplateEngine()
		templates.put(VERIFICATOIN_KEY, engine.createTemplate(grailsApplication.config.sms.templates.verification));
		templates.put(NEW_KEY, engine.createTemplate(grailsApplication.config.sms.templates.newRequest));
		templates.put(ACCEPT_KEY, engine.createTemplate(grailsApplication.config.sms.templates.acceptRequest));
		templates.put(REJECT_KEY, engine.createTemplate(grailsApplication.config.sms.templates.rejectRequest));
		templates.put(CANCEL_KEY, engine.createTemplate(grailsApplication.config.sms.templates.cancelRequest));
		templates.put(CANCEL_BY_REQUESTOR_KEY, engine.createTemplate(grailsApplication.config.sms.templates.cancelRequest));
		urlPrefixIndividual = grailsApplication.config.sms.url + '?&UserName=' + grailsApplication.config.sms.username + '&Password=' + grailsApplication.config.sms.password + '&Type=Individual&Mask=' + grailsApplication.config.sms.mask
		urlPrefixBulk = grailsApplication.config.sms.url + '?&UserName=' + grailsApplication.config.sms.username + '&Password=' + grailsApplication.config.sms.password + '&Type=Bulk&Mask=' + grailsApplication.config.sms.mask
	}
	
	@Queue(name= "msg.sms.notification.queue") //also defined in Constant.java. Grails issue //TODO can we use Constant.MOBILE_VERIFICATION_QUEUE ????
	def sendWorkflowSms(def messageMap) {
		def message = null;
		String to = messageMap['to'] 
		String state = messageMap['state']
		switch(state) {
			case WorkflowState.INITIATED.state : // send request from search result - resultant user should receive email and sms and push
				message = templates.get(NEW_KEY).make(messageMap).toString();
				break
			case WorkflowState.ACCEPTED.state : // other user accepted request
				message = templates.get(ACCEPT_KEY).make(messageMap).toString();
				break
			case WorkflowState.REJECTED.state : // other user rejected request
				message = templates.get(REJECT_KEY).make(messageMap).toString();
				break
			case WorkflowState.CANCELLED.state : // i am canceling previous accepted request
				message = templates.get(CANCEL_KEY).make(messageMap).toString();
				break
			case WorkflowState.CANCELLED_BY_REQUESTER.state : // i am canceling my own earlier request 
				message = templates.get(CANCEL_BY_REQUESTOR_KEY).make(messageMap).toString();
				break
			default :
				log.error "No state worklfow detected $state"

		}
		if(message) {
			String restUrl = urlPrefixIndividual + '&To=' + to + '&Message=' + message;
			def resp = rest.get(restUrl);
			if(resp.getStatus() != 200) {
				log.error ('SMS failure with service provider')
				log.info('Saving failure message to database')
				SmsFailure smsFailure = new SmsFailure();
				smsFailure.mobile = to
				smsFailure.message = message
				smsFailure.restUrl = restUrl
				smsFailure.serverResponse = resp.text
				smsFailure.save();
				return;
			}
		}
	}
	
	@Queue(name= "msg.notification.mobile.verification.queue") //also defined in Constant.java. Grails issue //TODO can we use Constant.MOBILE_VERIFICATION_QUEUE ????
    def sendVerificationSms(def messageMap) {
		String mobile = messageMap[Constant.MOBILE_KEY]
		String verificationCode = messageMap[Constant.VERIFICATION_CODE_KEY]
		def message = templates.get(VERIFICATOIN_KEY).make(['verificationCode' : verificationCode]).toString();
		String restUrl = urlPrefixIndividual + '&To=' + mobile + '&Message=' + message;
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
}
