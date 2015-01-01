package com.racloop.notification

import java.util.HashMap;
import java.util.Map;

import com.racloop.Constant;
import com.racloop.SmsFailure;

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
	String urlPrefixIndividual = null;
	String urlPrefixBulk = null;
	
	def init() {// Initialized in Bootstrap.groovy
		def engine = new groovy.text.SimpleTemplateEngine()
		templates.put(VERIFICATOIN_KEY, engine.createTemplate(grailsApplication.config.sms.templates.verification));
		templates.put(NEW_KEY, engine.createTemplate(grailsApplication.config.sms.templates.newRequest));
		templates.put(ACCEPT_KEY, engine.createTemplate(grailsApplication.config.sms.templates.acceptRequest));
		templates.put(REJECT_KEY, engine.createTemplate(grailsApplication.config.sms.templates.rejectRequest));
		templates.put(CANCEL_KEY, engine.createTemplate(grailsApplication.config.sms.templates.cancelRequest));
		urlPrefixIndividual = grailsApplication.config.sms.url + '?&UserName=' + grailsApplication.config.sms.username + '&Password=' + grailsApplication.config.sms.password + '&Type=Individual&Mask=' + grailsApplication.config.sms.mask
		urlPrefixBulk = grailsApplication.config.sms.url + '?&UserName=' + grailsApplication.config.sms.username + '&Password=' + grailsApplication.config.sms.password + '&Type=Bulk&Mask=' + grailsApplication.config.sms.mask
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
