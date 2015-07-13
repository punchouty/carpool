package com.racloop.elasticsearch

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;

class IndexMetadata {
	
	public static final String JOURNEY_INDEX_NAME = "journey"
	public static final String DUMMY_INDEX_NAME = "dummy"
	public static final String DEFAULT_TYPE = "default_type";
	public static final String LOCATION_MASTER_INDEX_NAME = "main_landmarks"
	public static final String LOCATION_MASTER_INDEX_TYPE_NAME = "default_landmarks"
	
	private boolean isIndexExists(Node node, String indexName){
		boolean isIndexExists = node.client.admin().indices().prepareExists(indexName).execute().actionGet().isExists()
		return isIndexExists
	}
	
	public createJourneyIndex(Node node, String indexName){
		if(isIndexExists(node, indexName)) {
			log.info "Index name : ${indexName} already exists"
		}
		else {
			log.info "Index name : ${indexName} creation started"
			node.client.admin().indices().prepareCreate(indexName).execute().actionGet();
			log.info "${indexName} created successfully"
			def builder = createJourneyIndexJson();
			node.client.admin().indices().preparePutMapping(indexName).setType(DEFAULT_TYPE).setSource(builder).execute().actionGet();
			log.info "${indexName} created successfully with mapping"
			
		}
	}
	
	private def createJourneyIndexJson() {
		XContentBuilder builder = XContentFactory.jsonBuilder().
		startObject().
			startObject(DEFAULT_TYPE).
				startObject("properties").
					startObject("mobile").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("dateOfJourney").
					field("type", "date").
					endObject().
					startObject("name").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("isDriver").
					field("type", "boolean").
					endObject().
					startObject("isTaxi").
					field("type", "boolean").
					endObject().
					startObject("isMale").
					field("type", "boolean").
					endObject().
					startObject("from").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("fromGeoPoint").
					field("type", "geo_point").field("lat_lon", "true").field("geohash", "true").
					endObject().
					startObject("to").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("toGeoPoint").
					field("type", "geo_point").field("lat_lon", "true").field("geohash", "true").
					endObject().
					startObject("tripDistance").
					field("type", "double").
					endObject().
					startObject("photoUrl").
					field("type", "string").
					endObject().
					startObject("isDummy").
					field("type", "boolean").
					endObject().
					startObject("numberOfCopassengers").
					field("type", "integer").
					endObject().
					startObject("tripTimeInSeconds").
					field("type", "integer").
					endObject().
				endObject().
			endObject().
		endObject();
		return builder;
	}
	
	public createDummyIndex(Node node, String indexName){
		if(isIndexExists(node, indexName)) {
			log.info "Index name : ${indexName} already exists"
		}
		else {
			log.info "Index name : ${indexName} creation started"
			node.client.admin().indices().prepareCreate(indexName).execute().actionGet();
			log.info "${indexName} created successfully"
			def builder = createJourneyIndexJson();
			node.client.admin().indices().preparePutMapping(indexName).setType(DEFAULT_TYPE).setSource(builder).execute().actionGet();
			log.info "${indexName} created successfully with mapping"
		}
	}
}
