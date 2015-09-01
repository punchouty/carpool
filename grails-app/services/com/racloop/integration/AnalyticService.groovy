package com.racloop.integration

import org.apache.log4j.spi.LoggerFactory;

import com.racloop.Constant;

import grails.plugin.jms.Queue;
import grails.transaction.Transactional


@Transactional
class AnalyticService {
	
	static exposes = ['jms']
	def grailsApplication
	def rest
	
	String urlGoogle = "https://www.google.com/analytics/";
	
	def init() {// Initialized in Bootstrap.groovy
		
	}

	//@Queue(name= Constant.ANALYTICS_QUEUE)
    def processAnalytics(def messageMap) {
		log.info(messageMap.toString())
		String payload = "v=1&tid=UA-66121379-3&t=pageview&ua=${messageMap.userAgent}&cid=${messageMap.user}&uip=${messageMap.ip}&dp=%2F${messageMap.controllerName}%2F${ messageMap.actionName}";
		def resp = rest.post(urlGoogle) { 
			json payload_data  : payload
		}
		if(resp.getStatus() != 200) {
			log.error "Issue connecting google : " + resp.getStatus()
			log.error "Json : " + resp.json
			log.error "text : " + resp.text
		}
		else {
			log.info("Successfully logged google");
		}
    }
}
