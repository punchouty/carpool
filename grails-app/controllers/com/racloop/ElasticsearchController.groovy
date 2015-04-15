package com.racloop

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONObject
import org.elasticsearch.common.joda.time.DateTime

import com.racloop.util.date.DateUtil

class ElasticsearchController {
	
	def rest
	def elasticSearchService

    def execute() { 
		String url =  "http://localhost:9200/" + params['url']
		String jsonString = params['json']
		def resp
		if(jsonString){
			resp = rest.post(url) {
				contentType: "application/vnd.org.jfrog.artifactory.security.Group+json" 
				json jsonString
				
			}
		}
		else {
			resp = rest.get(url)
		}
		JSON json = new JSONObject(resp.json)
		json.prettyPrint = true
		json.render response
	}
	
	def query() {
		
	}
	
	def addIndex(){
		
	}
	
	def executeAddIndex(){
		String message = null
		String indexUptoString = params['indexUptoString']
		DateTime indexUptoDate = DateUtil.convertUIDateToElasticSearchDate(indexUptoString)
		DateTime dateAfterThirtyDays = new DateTime().plusDays(30)
		if(dateAfterThirtyDays.compareTo(indexUptoDate)>0) {
			elasticSearchService.addNewIndex(new DateTime(), indexUptoDate)
			message ="Index added sucessfully!!!"
		}
		else {
			message = "You can only add index for next 30 days i.e. till $dateAfterThirtyDays"
		}
		
		render message
		
	}
}
