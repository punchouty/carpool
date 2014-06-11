package com.racloop.elasticsearch

import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.node.Node

import com.racloop.ElasticSearchService



class IndexDefinitor {
	
	public createMainIndex(Node node, String indexName){
		if(isIndexExists(node, indexName)) {
			log.info "Index name : ${indexName} already exists"
		}
		else {
			log.info "Started creating Index name : ${indexName}"
			node.client.admin().indices().prepareCreate(indexName).execute().actionGet();
			log.info "Index name : ${indexName} created successfuly"
			log.info "Started creating type : ${ElasticSearchService.TYPE_DRIVER} for index : ${indexName}"
			def builder = createMainIndexJson(ElasticSearchService.TYPE_DRIVER)
			node.client.admin().indices().preparePutMapping(indexName).setType(ElasticSearchService.TYPE_DRIVER).setSource(builder).execute().actionGet();
			log.info "Type : ${ElasticSearchService.TYPE_DRIVER} for index : ${indexName} created successfully"
	
			log.info "Started creating type : ${ElasticSearchService.TYPE_DRIVER} for index : ${indexName}"
			builder = createMainIndexJson(ElasticSearchService.TYPE_RIDER)
			node.client.admin().indices().preparePutMapping(indexName).setType(ElasticSearchService.TYPE_RIDER).setSource(builder).execute().actionGet();
			log.info "Type : ${ElasticSearchService.TYPE_RIDER} for index : ${indexName} created successfully"
		}
			
		
	}
	
	public createWorkflowIndex(Node node, String indexName) {
		if(isIndexExists(node, indexName)) {
			log.info "Index name : ${indexName} already exists"
		}
		else {
			log.info "Started creating Index name : ${indexName}"
			node.client.admin().indices().prepareCreate(indexName).execute().actionGet()
			log.info "Index name : ${indexName} created successfuly"
			def builder = createWorkflowIndexJson(ElasticSearchService.WORKFLOW)
			node.client.admin().indices().preparePutMapping(indexName).setType(ElasticSearchService.WORKFLOW).setSource(builder).execute().actionGet()
			
		}
	}
	
	public createMasterLocationIndex(Node node, String indexName, String indexType ) {
		if(isIndexExists(node, indexName)) {
			log.info "Index name : ${indexName} already exists"
		}
		else {
			log.info "Started creating master location Index name : ${indexName}"
			node.client.admin().indices().prepareCreate(indexName).execute().actionGet();
			def builder = createLocationMasterIndexJson(indexType);
			node.client.admin().indices().preparePutMapping(indexName).setType(indexType).setSource(builder).execute().actionGet();
			log.info "Master location index name : ${indexName} created successfuly"
		}
	}
	
	public createGeneratedDataIndex(Node node, String indexName) {
		if(isIndexExists(node, indexName)) {
			log.info "Index name : ${indexName} already exists"
		}
		else {
			log.info "Started creating Dummy Index name : ${indexName}"
			node.client.admin().indices().prepareCreate(indexName).execute().actionGet();
			log.info "Dummy Index name : ${indexName} created successfuly"
			log.info "Started creating type : ${ElasticSearchService.TYPE_DRIVER} for Dummy index : ${indexName}"
			def builder = createGeneratedDataJson(ElasticSearchService.TYPE_DRIVER)
			node.client.admin().indices().preparePutMapping(indexName).setType(ElasticSearchService.TYPE_DRIVER).setSource(builder).execute().actionGet();
			log.info "Type : ${ElasticSearchService.TYPE_DRIVER} for Dummy index : ${indexName} created successfully"

			log.info "Started creating type : ${ElasticSearchService.TYPE_DRIVER} for Dummy index : ${indexName}"
			builder = createGeneratedDataJson(ElasticSearchService.TYPE_RIDER)
			node.client.admin().indices().preparePutMapping(indexName).setType(ElasticSearchService.TYPE_RIDER).setSource(builder).execute().actionGet();
			log.info "Type : ${ElasticSearchService.TYPE_RIDER} for Dummy index : ${indexName} created successfully"
		}
	}
	
	private boolean isIndexExists(Node node, String indexName){
		boolean isIndexExists = node.client.admin().indices().prepareExists(indexName).execute().actionGet().isExists()
		return isIndexExists
	}

	
	private def createWorkflowIndexJson(String indexType) {
		XContentBuilder builder = XContentFactory.jsonBuilder().
				startObject().
					startObject(indexType).
						startObject("properties").
							startObject("requestJourneyId").
								field("type", "string").field("index", "not_analyzed").
							endObject().
							startObject("requestedFromPlace").
								field("type", "string").field("index", "not_analyzed").
							endObject().
							startObject("requestedToPlace").
								field("type", "string").field("index", "not_analyzed").
							endObject().
							startObject("requestUser").
								field("type", "string").field("index", "not_analyzed").
							endObject().
							startObject("requestedDateTime").
								field("type", "date").
							endObject().
							startObject("state").
								field("type", "string").field("index", "not_analyzed").
							endObject().
							startObject("matchedJourneyId").
								field("type", "string").field("index", "not_analyzed").
							endObject().
							startObject("matchedFromPlace").
								field("type", "string").field("index", "not_analyzed").
							endObject().
							startObject("matchedToPlace").
								field("type", "string").field("index", "not_analyzed").
							endObject().
							startObject("matchingUser").
								field("type", "string").field("index", "not_analyzed").
							endObject().
							startObject("matchedDateTime").
								field("type", "date").
							endObject().
							startObject("isRequesterDriving").
								field("type", "boolean").
							endObject().
						endObject().
					endObject().
				endObject();
		return builder;
	}
	
	private def createMainIndexJson(String type) {
		XContentBuilder builder = XContentFactory.jsonBuilder().
		startObject().
			startObject(type).
				startObject("properties").
					startObject("user").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("name").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("isMale").
					field("type", "boolean").
					endObject().
					startObject("dateOfJourney").
					field("type", "date").
					endObject().
					startObject("fromPlace").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("from").
					field("type", "geo_point").field("lat_lon", "true").field("geohash", "true").
					endObject().
					startObject("toPlace").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("to").
					field("type", "geo_point").field("lat_lon", "true").field("geohash", "true").
					endObject().
					startObject("requesterIp").
					field("type", "ip").
					endObject().
					startObject("createdDate").
					field("type", "date").
					endObject().
					startObject("tripDistance").
					field("type", "double").
					endObject().
				endObject().
			endObject().
		endObject();
		return builder;
	}
	
	private createLocationMasterIndexJson(String indexType) {
		XContentBuilder builder = XContentFactory.jsonBuilder().
		startObject().
			startObject(indexType).
				startObject("properties").
					startObject("name").
					field("type", "string").
					endObject().
					startObject("location").
					field("type", "geo_point").field("lat_lon", "true").field("geohash", "true").
					endObject().
				endObject().
			endObject().
		endObject();
		return builder;
	}
	
	private def createGeneratedDataJson(String type) {
		XContentBuilder builder = XContentFactory.jsonBuilder().
		startObject().
			startObject(type).
				startObject("properties").
					startObject("name").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("isMale").
					field("type", "boolean").
					endObject().
					startObject("dateOfJourney").
					field("type", "date").
					endObject().
					startObject("fromPlace").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("from").
					field("type", "geo_point").field("lat_lon", "true").field("geohash", "true").
					endObject().
					startObject("toPlace").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("to").
					field("type", "geo_point").field("lat_lon", "true").field("geohash", "true").
					endObject().
					startObject("createdDate").
					field("type", "date").
					endObject().
					startObject("tripDistance").
					field("type", "double").
					endObject().
				endObject().
			endObject().
		endObject();
		return builder;
	}

}
