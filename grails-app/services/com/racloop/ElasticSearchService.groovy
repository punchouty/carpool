package com.racloop;

import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.format.DateTimeFormatter
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat
import org.elasticsearch.common.unit.DistanceUnit
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.index.query.FilterBuilder
import org.elasticsearch.index.query.FilterBuilders
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.indices.IndexMissingException
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.GeoDistanceSortBuilder
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder

import com.racloop.elasticsearch.IndexDefinitor
import com.racloop.workflow.JourneyWorkflow


class ElasticSearchService {

	def grailsApplication
	public static final int DISTANCE_FACTOR = 8
	public static final String TYPE_DRIVER = "driver"
	public static final String TYPE_RIDER = "rider"
	public static final String WORKFLOW = "workflow"
	public static final DateTimeFormatter BASIC_DATE_FORMAT = ISODateTimeFormat.dateOptionalTimeParser();
	private Node node

	def init() {
		log.info "Initialising elastic search client"
		node = NodeBuilder.nodeBuilder().node()
	}

	def destroy() {
		node.close()
	}

//	def indexJourneyWithIndexCheck(Journey journey) {
//		createIndexIfNotExistsForDate(journey.dateOfJourney);
//		indexJourney(journey)
//	}

	def indexJourney(User user, JourneyRequestCommand journey) {
		log.info "Adding record to elastic search ${journey}"
		def sourceBuilder = createJourneyJson(user, journey)
		String indexName = getIndexName(journey.dateOfJourney)
		String type = getType(journey);
		IndexRequest indexRequest = new IndexRequest(indexName, type).id(journey.id + '').source(sourceBuilder);
		node.client.index(indexRequest).actionGet();
		log.info "Successfully indexed ${journey}"
	}

	def indexGeneratedJourney(User user, JourneyRequestCommand journey) {
		String id = UUID.randomUUID().toString()
		String indexName = grailsApplication.config.grails.generatedData.index.name
		log.debug "Adding generated record to elastic search ${journey}"
		def sourceBuilder = createdGeneratedJourneyJson(user, journey)
		String type = getType(journey);
		IndexRequest indexRequest = new IndexRequest(indexName, type, id).source(sourceBuilder);
		node.client.index(indexRequest).actionGet();
		log.debug "Successfully indexed generated journey : ${journey}"
		return id
	}
	
	def indexLocation(Place place) {
		String indexName = grailsApplication.config.grails.masterData.places.index.name
		String type = grailsApplication.config.grails.masterData.places.index.type;		
		log.debug "Adding record to location master index in elastic search ${place}"
		def sourceBuilder = createPlaceJson(place);
		IndexRequest indexRequest = new IndexRequest(indexName, type).source(sourceBuilder);
		node.client.index(indexRequest).actionGet();
		log.debug "Successfully indexed place : ${place}"
	}

	def createMainIndex() {
		Date date = new Date().previous();//get yesterday date
		//Create indexes for past week and future week
		for (int i = 0; i < 10; i++) {
			String indexName = getIndexName(date)
			IndexDefinitor indexDefinitor = new IndexDefinitor()
			indexDefinitor.createMainIndex(node, indexName)
			date = date.next()
		}		
	}
	
	def closeMainIndexForPreviousWeek() {
		Calendar time = Calendar.getInstance();
		time.add(Calendar.WEEK_OF_MONTH, -3);
		String indexName = getIndexName(time.getTime());
		boolean isIndexExists = node.client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
		if(isIndexExists) {
			log.info "Index name : ${indexName} exists"
			log.info "Started closing Index name : ${indexName}"
			CloseIndexResponse response = node.client.admin().indices().prepareClose(indexName).execute().actionGet();
			if(response.isAcknowledged()) {
				log.info "Index name : ${indexName} closed successfully"
			}
			else {
				log.warn "Index name : ${indexName} not closed because it is acknowledge"
			}
		}
		else {
			log.warn "Index name : ${indexName} does not exists"
		}
	}
	
	def createMasterLocationIndexIfNotExists() {
		String indexName = grailsApplication.config.grails.masterData.places.index.name
		String indexType = grailsApplication.config.grails.masterData.places.index.type
		IndexDefinitor indexDefinitor = new IndexDefinitor()
		indexDefinitor.createMasterLocationIndex(node, indexName, indexType)
	}
	
	/**
	 * @return
	 */
	def createGeneratedDataIndexIfNotExists() {
		String indexName = grailsApplication.config.grails.generatedData.index.name
		IndexDefinitor indexDefinitor = new IndexDefinitor()
		indexDefinitor.createGeneratedDataIndex(node, indexName)
	}


	private def createJourneyJson(User user, JourneyRequestCommand journey) {
		GeoPoint from = new GeoPoint(journey.fromLatitude, journey.fromLongitude)
		GeoPoint to = new GeoPoint(journey.toLatitude, journey.toLongitude)
		DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
		DateTime createdDate = new DateTime(journey.createdDate)
		XContentBuilder builder = XContentFactory.jsonBuilder().
			startObject().
				field("user", user.username).
				field("name", user.profile.fullName).
				field("isMale", user.profile.isMale).
				field("dateOfJourney", dateOfJourney).
				field("fromPlace", journey.fromPlace).
				field("from", from).
				field("toPlace", journey.toPlace).
				field("to", to).
				//field("requesterIp", journey.ip).
				field("createdDate", createdDate).
				field("tripDistance", journey.tripDistance).
			endObject();
		return builder;
	}
	
	private def createdGeneratedJourneyJson(User user, JourneyRequestCommand journey) {
		GeoPoint from = new GeoPoint(journey.fromLatitude, journey.fromLongitude)
		GeoPoint to = new GeoPoint(journey.toLatitude, journey.toLongitude)
		DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
		DateTime createdDate = new DateTime(journey.createdDate)
		boolean isMale = true;
		if(user) {
			isMale = user.profile.isMale;
		}
		XContentBuilder builder = XContentFactory.jsonBuilder().
			startObject().
				field("user", journey.user).
				field("name", journey.name).
				field("isMale", isMale).
				field("dateOfJourney", dateOfJourney).
				field("fromPlace", journey.fromPlace).
				field("from", from).
				field("toPlace", journey.toPlace).
				field("to", to).
				field("createdDate", createdDate).
				field("tripDistance", journey.tripDistance).
			endObject();
		return builder;
	}
	
	private def createPlaceJson(Place place) {
		XContentBuilder builder = XContentFactory.jsonBuilder().
			startObject().
				field("name", place.name).
				field("location", place.location).
			endObject();
		return builder;
	}

	JourneyRequestCommand getJourney(def journeyId, boolean isDriver, def dateOfJourney) {
		String indexName = getIndexName(dateOfJourney);
		String searchType = null;
		if(isDriver) {
			searchType = TYPE_DRIVER;
		}
		else {
			searchType = TYPE_RIDER;
		}
		GetResponse getResponse = node.client.prepareGet(indexName, searchType, journeyId).execute().actionGet();
		return parseJourneyFromGetResponse(getResponse);
	}

	def search(User user, JourneyRequestCommand journey) {
		String indexName = getIndexName(journey.dateOfJourney);
		String searchTypeOpposite = null;
		if(journey.isDriver) {
			searchTypeOpposite = TYPE_RIDER;
		}
		else {
			searchTypeOpposite = TYPE_DRIVER;
		}
		double filterDistance = journey.tripDistance / DISTANCE_FACTOR;
		DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
		DateTime start = dateOfJourney.minusHours(4);
		DateTime end = dateOfJourney.plusHours(4);
		DateTime validStartTime = new DateTime(journey.validStartTime)
		if(start.isBefore(validStartTime)) {
			start = validStartTime;
		}
		FilterBuilder filter = null;
		if(user) {
			filter = FilterBuilders.andFilter(
				//FilterBuilders.limitFilter(10),
				FilterBuilders.rangeFilter("dateOfJourney").from(start).to(end),
				FilterBuilders.geoDistanceFilter("from").point(journey.fromLatitude, journey.fromLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC),
				FilterBuilders.geoDistanceFilter("to").point(journey.toLatitude, journey.toLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC),
				FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter("user", user.username))
				
			);
		}
		else {
			filter = FilterBuilders.andFilter(
				//FilterBuilders.limitFilter(10),
				FilterBuilders.rangeFilter("dateOfJourney").from(start).to(end),
				FilterBuilders.geoDistanceFilter("from").point(journey.fromLatitude, journey.fromLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC),
				FilterBuilders.geoDistanceFilter("to").point(journey.toLatitude, journey.toLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC)
				
			);
		}
		GeoDistanceSortBuilder sorter = SortBuilders.geoDistanceSort("from");
		sorter.point(journey.fromLatitude, journey.fromLongitude);
		sorter.order(SortOrder.ASC);
		def journeys = []
		try {			
			SearchResponse searchResponse = node.client.prepareSearch(indexName)
				.setTypes(searchTypeOpposite)
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.matchAllQuery())             // Query
				.setFilter(filter)   // Filter
				.addSort(sorter)
				.execute()
				.actionGet();
			SearchHits searchHits = searchResponse.getHits();
			SearchHit[] hits = searchHits.hits;
			for (SearchHit searchHit : hits) {
				JourneyRequestCommand journeyTemp = parseJourneyFromSearchHit(searchHit);
				journeys << journeyTemp
			}
		}
		catch (IndexMissingException exception) {
			log.error "Index name ${indexName} does not exists"
		}
		return journeys
	}
	
	def searchPossibleExistingJourneyForUser(User user, JourneyRequestCommand journey) {
		String indexName = getIndexName(journey.dateOfJourney);
		String searchType = null;
		if(journey.isDriver) {
			searchType = TYPE_DRIVER;
		}
		else {
			searchType = TYPE_RIDER;
		}
		double filterDistance = Double.valueOf(grailsApplication.config.grails.approx.distance.to.match)
		DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
		DateTime start = dateOfJourney.minusMinutes(Integer.valueOf(grailsApplication.config.grails.approx.time.to.match))
		DateTime end = dateOfJourney.plusMinutes(Integer.valueOf(grailsApplication.config.grails.approx.time.to.match))
		FilterBuilder filter = null;
		filter = FilterBuilders.andFilter(
			//FilterBuilders.limitFilter(10),
			FilterBuilders.rangeFilter("dateOfJourney").from(start).to(end),
			FilterBuilders.geoDistanceFilter("from").point(journey.fromLatitude, journey.fromLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC),
			FilterBuilders.geoDistanceFilter("to").point(journey.toLatitude, journey.toLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC),
			FilterBuilders.boolFilter().must(FilterBuilders.termFilter("user", user.username))
			
		)
		GeoDistanceSortBuilder sorter = SortBuilders.geoDistanceSort("from");
		sorter.point(journey.fromLatitude, journey.fromLongitude);
		sorter.order(SortOrder.ASC);
		def journeys = []
		try {
			SearchResponse searchResponse = node.client.prepareSearch(indexName)
				.setTypes(searchType)
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.matchAllQuery())             // Query
				.setFilter(filter)   // Filter
				.addSort(sorter)
				.execute()
				.actionGet();
			SearchHits searchHits = searchResponse.getHits();
			SearchHit[] hits = searchHits.hits;
			for (SearchHit searchHit : hits) {
				JourneyRequestCommand journeyTemp = parseJourneyFromSearchHit(searchHit);
				journeys << journeyTemp
			}
		}
		catch (IndexMissingException exception) {
			log.error "Index name ${indexName} does not exists"
		}
		return journeys
	}
	/**
	 * 
	 * @param user
	 * @param journey
	 * @return
	 */
	def searchGeneratedData(JourneyRequestCommand journey) {
		String indexName = grailsApplication.config.grails.generatedData.index.name;
		String searchTypeOpposite = null;
		if(journey.isDriver) {
			searchTypeOpposite = TYPE_RIDER;
		}
		else {
			searchTypeOpposite = TYPE_DRIVER;
		}
		double filterDistance = journey.tripDistance / DISTANCE_FACTOR;
		DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
		DateTime start = dateOfJourney.minusHours(4);
		DateTime end = dateOfJourney.plusHours(4);
		DateTime validStartTime = new DateTime(journey.validStartTime)
		if(start.isBefore(validStartTime)) {
			start = validStartTime;
		}
		FilterBuilder filter = FilterBuilders.andFilter(
			//FilterBuilders.limitFilter(10),
			FilterBuilders.rangeFilter("dateOfJourney").from(start).to(end),
			FilterBuilders.geoDistanceFilter("from").point(journey.fromLatitude, journey.fromLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.PLANE),
			FilterBuilders.geoDistanceFilter("to").point(journey.toLatitude, journey.toLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.PLANE)			
		);
		GeoDistanceSortBuilder sorter = SortBuilders.geoDistanceSort("from");
		sorter.point(journey.fromLatitude, journey.fromLongitude);
		sorter.order(SortOrder.ASC);
		def journeys = []
		try {
			SearchResponse searchResponse = node.client.prepareSearch(indexName)
				.setTypes(searchTypeOpposite)
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.matchAllQuery())             // Query
				.setFilter(filter)   // Filter
				.addSort(sorter)
				.execute()
				.actionGet();
			SearchHits searchHits = searchResponse.getHits();
			SearchHit[] hits = searchHits.hits;
			for (SearchHit searchHit : hits) {
				JourneyRequestCommand journeyTemp = parseJourneyFromSearchHit(searchHit);
				journeys << journeyTemp
			}
		}
		catch (IndexMissingException exception) {
			log.error "Index name ${indexName} does not exists"
		}
		return journeys
	}
	
	def searchNearLocations(double maxDistance, double lattitude, double longitude, int maxRecords) {
		String indexName = grailsApplication.config.grails.masterData.places.index.name;
		String indexType = grailsApplication.config.grails.masterData.places.index.type
//		FilterBuilder filter = FilterBuilders.andFilter(
//			FilterBuilders.limitFilter(maxRecords),
//			FilterBuilders.geoDistanceFilter("location").point(lattitude, longitude).distance(maxDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC)
//		);
		FilterBuilder filter = FilterBuilders.geoDistanceFilter("location").point(lattitude, longitude).distance(maxDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.PLANE)
		GeoDistanceSortBuilder sorter = SortBuilders.geoDistanceSort("location");
		sorter.point(lattitude, longitude);
		sorter.order(SortOrder.ASC);
		def places = [];
		try {
			SearchResponse searchResponse = node.client.prepareSearch(indexName)
				.setTypes(indexType)
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.matchAllQuery())             // Query
				.setFilter(filter)   // Filter
				.addSort(sorter)
				.setSize(maxRecords)
				.execute()
				.actionGet();
			SearchHits searchHits = searchResponse.getHits();
			SearchHit[] hits = searchHits.hits;
			for (SearchHit searchHit : hits) {
				Place place = parsePlaceFromSearchHit(searchHit);
				places << place
			}
		}
		catch (IndexMissingException exception) {
			log.error "Index name ${indexName} does not exists"
		}
		return places
	}
	
	def deleteSampleData() {
		Calendar time = Calendar.getInstance();
		Date date = new Date(time.timeInMillis);
		String indexName = getIndexName(date);
		log.info "Removing data from journeys"
		DeleteByQueryResponse response = node.client.prepareDeleteByQuery(indexName).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
		log.info "Data removed from journeys successfully"
	}
	
	private Place parsePlaceFromSearchHit(SearchHit searchHit) {
		Place place = new Place();
		place.name = searchHit.getSource().get('name');
		place.location = searchHit.getSource().get('location');
		return place;
	}

	private JourneyRequestCommand parseJourneyFromSearchHit(SearchHit searchHit) {
		JourneyRequestCommand journeyTemp = new JourneyRequestCommand();
		journeyTemp.id = searchHit.id
		journeyTemp.isDriver = searchHit.type==TYPE_DRIVER?true:false
		journeyTemp.user = searchHit.getSource().get('user');
		journeyTemp.name = searchHit.getSource().get('name');
		String dateStr = searchHit.getSource().get('dateOfJourney');
		DateTime dateTime = BASIC_DATE_FORMAT.parseDateTime(dateStr);
		journeyTemp.dateOfJourney = dateTime.toDate();
		journeyTemp.fromPlace = searchHit.getSource().get('fromPlace');
		journeyTemp.toPlace = searchHit.getSource().get('toPlace')
		journeyTemp.isSaved = true
		journeyTemp.isMale=searchHit.getSource().get('isMale')
		journeyTemp.tripDistance=searchHit.getSource().get('tripDistance')
		journeyTemp.fromLatitude=searchHit.getSource().get('from')?.get("lat")
		journeyTemp.fromLongitude=searchHit.getSource().get('from')?.get("lon")
		journeyTemp.toLatitude=searchHit.getSource().get('to')?.get("lat")
		journeyTemp.toLongitude=searchHit.getSource().get('to')?.get("lon")
		return journeyTemp
	}	
	
	private JourneyRequestCommand parseJourneyFromGetResponse(GetResponse getResponse) {
		JourneyRequestCommand journeyTemp = new JourneyRequestCommand();
		journeyTemp.id = getResponse.id
		journeyTemp.isDriver = getResponse.type==TYPE_DRIVER?true:false
		journeyTemp.user = getResponse.getSource().get('user');
		journeyTemp.name = getResponse.getSource().get('name');
		String dateStr = getResponse.getSource().get('dateOfJourney');
		DateTime dateTime = BASIC_DATE_FORMAT.parseDateTime(dateStr);
		journeyTemp.dateOfJourney = dateTime.toDate();
		journeyTemp.isDriver = getResponse.getSource().get('isDriver');
		journeyTemp.fromPlace = getResponse.getSource().get('fromPlace');
		GeoPoint from = getResponse.getSource().get('from');
		journeyTemp.fromLatitude = from.lat();
		journeyTemp.fromLongitude = from.lon();
		journeyTemp.toPlace = getResponse.getSource().get('toPlace');
		GeoPoint to = getResponse.getSource().get('to');
		journeyTemp.toLatitude = to.lat();
		journeyTemp.toLongitude = to.lon();
		journeyTemp.ip = getResponse.getSource().get('ip');
		journeyTemp.tripDistance=getResponse.getSource().get('tripDistance')
		journeyTemp.fromLatitude=getResponse.getSource().get('from')?.get("lat")
		journeyTemp.fromLongitude=getResponse.getSource().get('from')?.get("lon")
		journeyTemp.toLatitude=getResponse.getSource().get('to')?.get("lat")
		journeyTemp.toLongitude=getResponse.getSource().get('to')?.get("lon")
		
		return journeyTemp
	}

	private String getIndexName(Date dateOfJourney) {
		String indexName = dateOfJourney.format(grailsApplication.config.grails.journeyIndexNameFormat);
		return indexName.toLowerCase();
	}

	private String getType(JourneyRequestCommand journey) {
		if(journey.isDriver) {
			return TYPE_DRIVER
		}
		else {
			return TYPE_RIDER
		}
	}
	
	def createWorkflowIndex() {
		IndexDefinitor indexDefinitor = new IndexDefinitor()
		indexDefinitor.createWorkflowIndex(node, WORKFLOW.toLowerCase())

	}
	
	def indexWorkflow(JourneyWorkflow workflow) {
		log.info "Adding record to elastic search ${workflow}"
		def sourceBuilder = createWorkflowJson(workflow)
		IndexRequest indexRequest = new IndexRequest('workflow', WORKFLOW).id(workflow.id.toString()).source(sourceBuilder);
		node.client.index(indexRequest).actionGet();
		log.info "Successfully indexed ${workflow}"
	}
	
	private def createWorkflowJson(JourneyWorkflow workflow) {
		DateTime requestedDateTime = new DateTime(workflow.requestedDateTime)
		DateTime matchedDateTime = new DateTime(workflow.matchedDateTime)
		XContentBuilder builder = XContentFactory.jsonBuilder().
			startObject().
				field("requestJourneyId", workflow.requestJourneyId).
				field("requestedFromPlace", workflow.requestedFromPlace).
				field("requestedToPlace", workflow.requestedToPlace).
				field("requestUser", workflow.requestUser).
				field("requestedDateTime", requestedDateTime).
				field("state", workflow.state).
				field("matchingUser", workflow.matchingUser).
				field("matchedJourneyId", workflow.matchedJourneyId).
				field("matchedFromPlace", workflow.matchedFromPlace).
				field("matchedToPlace", workflow.matchedToPlace).
				field("matchedDateTime", matchedDateTime).
				field("isRequesterDriving", workflow.isRequesterDriving).
			endObject();
		return builder;
	}
	
	
	
	def findJourneyById(String matchedJourneyId , JourneyRequestCommand currentJourney, boolean isDummyData) {
		String indexName = null 
		if(isDummyData) {
			indexName = grailsApplication.config.grails.generatedData.index.name
		}
		else {
			indexName = getIndexName(currentJourney.dateOfJourney);
			
		}
		String searchTypeOpposite = null;
		if(currentJourney.isDriver) {
			searchTypeOpposite = TYPE_RIDER;
		}
		else {
			searchTypeOpposite = TYPE_DRIVER;
		}
		GetResponse response = node.client.prepareGet(indexName, searchTypeOpposite, matchedJourneyId).execute().actionGet();
		JourneyRequestCommand journeyTemp = parseJourneyFromGetResponse(response)
		return journeyTemp
	}
	
	def searchWorkflowRequestedByUser(User user) {
		String indexName = WORKFLOW.toLowerCase()
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.termFilter('requestUser', user.username),
			FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter("user", user.username))
			
		)
		
		FieldSortBuilder  sorter = SortBuilders.fieldSort("requestedDateTime")
		sorter.order(SortOrder.DESC);
		
		def workflows = searchWorkflow(indexName, WORKFLOW, filter, sorter)
		return workflows
	}
	
	def searchWorkflowMatchedForUser(User user) {
		String indexName = WORKFLOW.toLowerCase()
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.termFilter('matchingUser', user.username),
			FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter("user", user.username))
			
		)
		
		FieldSortBuilder  sorter = SortBuilders.fieldSort("matchedDateTime")
		sorter.order(SortOrder.DESC);
		
		def workflows = searchWorkflow(indexName, WORKFLOW, filter, sorter)
		return workflows
	}
	
	def searchWorkflowByRequestedJourney(JourneyRequestCommand command) {
		String indexName = WORKFLOW.toLowerCase()
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.termFilter('requestUser', command.user),
			FilterBuilders.termFilter('requestJourneyId', command.id)
			//FilterBuilders.termFilter('state', "")
		)
		
		def workflows = searchWorkflow(indexName, WORKFLOW, filter, null)
		
		return workflows
	}
	
	def findAllJourneysForUserAfterADate(User user, DateTime inputDate) {
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.rangeFilter("dateOfJourney").gte(inputDate),
			FilterBuilders.boolFilter().must(FilterBuilders.termFilter("user", user.username))
			
		)
		FieldSortBuilder  sorter = SortBuilders.fieldSort("dateOfJourney")
		sorter.order(SortOrder.ASC);
		def journeys = []
		try {
			SearchResponse searchResponse = node.client.prepareSearch()
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.matchAllQuery())             // Query
				.setFilter(filter)   // Filter
				.addSort(sorter)
				.setSize(100) //size
				.execute()
				.actionGet();
			SearchHits searchHits = searchResponse.getHits();
			SearchHit[] hits = searchHits.hits;
			for (SearchHit searchHit : hits) {
				JourneyRequestCommand journeyTemp = parseJourneyFromSearchHit(searchHit);
				journeys << journeyTemp
			}
		}
		catch (IndexMissingException exception) {
			log.error ("Index name does not exists", exception)
		}
		return journeys
	}
	
	private List searchWorkflow(String indexName, String type, FilterBuilder filter, FieldSortBuilder  sorter ) {
		def workflows = []
		try {
			SearchRequestBuilder searchRequestBuilder = node.client.prepareSearch(indexName)
			searchRequestBuilder = searchRequestBuilder.setTypes(WORKFLOW)
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.matchAllQuery())
				.setFilter(filter)   // Filter
			if(sorter) {
				searchRequestBuilder.addSort(sorter)
			}	
				
			SearchResponse searchResponse =searchRequestBuilder.execute().actionGet();
			SearchHits searchHits = searchResponse.getHits();
			SearchHit[] hits = searchHits.hits;
			for (SearchHit searchHit : hits) {
				JourneyWorkflow workflow = parseWorkflowFromSearchHit(searchHit);
				workflows << workflow
			}
		}
		catch (IndexMissingException exception) {
			log.error "Index name ${indexName} does not exists"
		}
		return workflows
	}
	
	private JourneyWorkflow parseWorkflowFromSearchHit(SearchHit searchHit) {
		JourneyWorkflow workflow = new JourneyWorkflow();
		String workflowId = searchHit.id
		workflow.id = UUID.fromString(workflowId)
		workflow.requestJourneyId = searchHit.getSource().get('requestJourneyId');
		String requestedDateTimeStr = searchHit.getSource().get('requestedDateTime');
		workflow.requestedDateTime = BASIC_DATE_FORMAT.parseDateTime(requestedDateTimeStr).toDate()
		workflow.requestedFromPlace = searchHit.getSource().get('requestedFromPlace');
		workflow.requestedToPlace = searchHit.getSource().get('requestedToPlace');
		workflow.requestUser = searchHit.getSource().get('requestUser');
		workflow.state = searchHit.getSource().get('state')
		workflow.matchingUser = searchHit.getSource().get('matchingUser');
		workflow.matchedJourneyId= searchHit.getSource().get('matchedJourneyId');
		workflow.matchedFromPlace = searchHit.getSource().get('matchedFromPlace');
		workflow.matchedToPlace = searchHit.getSource().get('matchedToPlace');
		String matchedDateTimeStr = searchHit.getSource().get('matchedDateTime');
		workflow.matchedDateTime = BASIC_DATE_FORMAT.parseDateTime(matchedDateTimeStr).toDate()

		workflow.isRequesterDriving = searchHit.getSource().get('isRequesterDriving');
		return workflow
	}
}
