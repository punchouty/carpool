package com.racloop

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONObject
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.node.Node

import com.google.gson.JsonObject;

class ElasticsearchController {
	
	def rest

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
}
