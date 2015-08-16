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
	
	String urlPrefixGoogleAnalytics = null;
	String urlPrefixAffel = null;
	
	def init() {// Initialized in Bootstrap.groovy
		
	}

	@Queue(name= Constant.ANALYTICS_QUEUE)
    def processAnalytics(def messageMap) {
		log.info(messageMap.toString())
    }
}
