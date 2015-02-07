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
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder

import com.racloop.elasticsearch.IndexDefinitor
import com.racloop.journey.workkflow.WorkflowState
import com.racloop.util.date.DateUtil;
import com.racloop.workflow.JourneyWorkflow
import static com.racloop.elasticsearch.WorkflowIndexFields.*
import static com.racloop.util.date.DateUtil.convertElasticSearchDateToDateTime



class ElasticSearchService {

	def grailsApplication
	public static final int DISTANCE_FACTOR = 8
	public static final String TYPE_DRIVER = "driver"
	public static final String TYPE_RIDER = "rider"
	public static final String WORKFLOW = "workflow"
	public static final String JOURNEY = "journey"
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
		String indexName = JOURNEY//getIndexName(journey.dateOfJourney)
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

	@Deprecated
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
	
	def createMainJourneyIndex() {
		IndexDefinitor indexDefinitor = new IndexDefinitor()
		indexDefinitor.createMainIndex(node, JOURNEY)
	}
	
	@Deprecated
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
				field("photoUrl", user.facebookId ? user.getFacebookProfilePic(): user.profile.getGravatarUri()).
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
		String indexName = JOURNEY//getIndexName(dateOfJourney);
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
		String indexName = JOURNEY//getIndexName(journey.dateOfJourney);
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
		FilterBuilder deletedFilter = getFilterOnDeletedJourney()
		FilterBuilder dateRanageFilter=getFilterOnJourneyStart(start, end)
		FilterBuilder geoDistanceFilterFrom = getFilterOnJourneyFromPositon(journey, filterDistance)
		FilterBuilder geoDistanceFilterTo = getFilterOnJourneyToPositon(journey, filterDistance)
		
		FilterBuilder filter = FilterBuilders.andFilter(
			//FilterBuilders.limitFilter(10),
			dateRanageFilter,
			geoDistanceFilterFrom,
			geoDistanceFilterTo,
			deletedFilter)
		
		if(user) {
			filter = FilterBuilders.andFilter(filter, FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter("user", user.username)));
		}
		
		GeoDistanceSortBuilder sorter = getJourneyFromGeoDistanceSorter(journey);
		FieldSortBuilder startTimeSorter = getJourneyStartTimeSorter()
		return searchJourney(indexName, [TYPE_DRIVER,TYPE_RIDER] as String[], filter , sorter, startTimeSorter)
	}

	private GeoDistanceSortBuilder getJourneyFromGeoDistanceSorter(JourneyRequestCommand journey) {
		GeoDistanceSortBuilder sorter = SortBuilders.geoDistanceSort("from");
		sorter.point(journey.fromLatitude, journey.fromLongitude);
		sorter.order(SortOrder.ASC)
		return sorter
	}

	private FieldSortBuilder getJourneyStartTimeSorter() {
		FieldSortBuilder startTimeSorter = new FieldSortBuilder("dateOfJourney").order(SortOrder.ASC)
		return startTimeSorter
	}

	
	
	def searchPossibleExistingJourneyForUser(User user, JourneyRequestCommand journey) {
		String indexName = JOURNEY.toLowerCase()//getIndexName(journey.dateOfJourney);
		
		double filterDistance = Double.valueOf(grailsApplication.config.grails.approx.distance.to.match)
		DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
		DateTime start = dateOfJourney.minusMinutes(Integer.valueOf(grailsApplication.config.grails.approx.time.to.match))
		DateTime end = dateOfJourney.plusMinutes(Integer.valueOf(grailsApplication.config.grails.approx.time.to.match))
		FilterBuilder filter = null;
		filter = FilterBuilders.andFilter(
			//FilterBuilders.limitFilter(10),
			getFilterOnJourneyStart(start, end),
			getFilterOnJourneyFromPositon(journey, filterDistance),
			getFilterOnJourneyToPositon(journey, filterDistance),
			getFilterOnDeletedJourney(),
			FilterBuilders.boolFilter().must(FilterBuilders.termFilter("user", user.username))
			
		)
		GeoDistanceSortBuilder sorter = getJourneyFromGeoDistanceSorter(journey)
		return searchJourney(indexName, [TYPE_DRIVER,TYPE_RIDER] as String[], filter, sorter)
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
			getFilterOnJourneyStart(start, end),
			getFilterOnDeletedJourney(),
			getFilterOnJourneyFromPositon(journey, filterDistance),
			getFilterOnJourneyToPositon(journey, filterDistance)			
		);
		GeoDistanceSortBuilder sorter = getJourneyFromGeoDistanceSorter(journey)
		return searchJourney(indexName, [TYPE_DRIVER,TYPE_RIDER] as String[], filter, sorter)
		
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

		SearchHit[] hits = queryDocument(indexName, [indexType] as String[], filter, maxRecords, sorter)
		for (SearchHit searchHit : hits) {
			Place place = parsePlaceFromSearchHit(searchHit);
			places << place
		}
		
		return places
	}
	
	def deleteSampleData() {
		Calendar time = Calendar.getInstance();
		Date date = new Date(time.timeInMillis);
		String indexName = JOURNEY//getIndexName(date);
		log.info "Removing data from journeys"
		DeleteByQueryResponse response = node.client.prepareDeleteByQuery(indexName).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
		log.info "Data removed from journeys successfully"
	}
	
	def deleteWorkflowData() {
		String indexName = WORKFLOW
		log.info "Removing data from workflows"
		DeleteByQueryResponse response = node.client.prepareDeleteByQuery(indexName).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
		log.info "Data removed from workflows successfully"
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
		journeyTemp.photoUrl = searchHit.getSource().get('photoUrl')
		String dateStr = searchHit.getSource().get('dateOfJourney');
		DateTime dateTime = convertElasticSearchDateToDateTime(dateStr)
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
		journeyTemp.photoUrl = getResponse.getSource().get('photoUrl');
		String dateStr = getResponse.getSource().get('dateOfJourney');
		DateTime dateTime = convertElasticSearchDateToDateTime(dateStr)
		journeyTemp.dateOfJourney = dateTime.toDate();
		//journeyTemp.isDriver = getResponse.getSource().get('isDriver');
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
		journeyTemp.isSaved = true
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
		IndexRequest indexRequest = new IndexRequest(WORKFLOW, WORKFLOW).id(workflow.id.toString()).refresh(true).source(sourceBuilder);
		node.client.index(indexRequest).actionGet();
		log.info "Successfully indexed ${workflow}"
	}
	
	private def createWorkflowJson(JourneyWorkflow workflow) {
		DateTime requestedDateTime = new DateTime(workflow.requestedDateTime)
		DateTime matchedDateTime = new DateTime(workflow.matchedDateTime)
		XContentBuilder builder = XContentFactory.jsonBuilder().
			startObject().
				field(REQUEST_JOURNEY_ID, workflow.requestJourneyId).
				field(REQUEST_FROM_PLACE, workflow.requestedFromPlace).
				field(REQUEST_TO_PLACE, workflow.requestedToPlace).
				field(REQUEST_USER, workflow.requestUser).
				field(REQUEST_DATE_TIME, requestedDateTime).
				field(STATE, workflow.state).
				field(MACTHING_USER, workflow.matchingUser).
				field(MATCHED_JOURNEY_ID, workflow.matchedJourneyId).
				field(MATCHED_FROM_PLACE, workflow.matchedFromPlace).
				field(MATCHED_TO_PLACE, workflow.matchedToPlace).
				field(MATCHED_DATE_TIME, matchedDateTime).
				field(IS_REQUESTER_DRIVING, workflow.isRequesterDriving).
				field(IS_MATCHED_USER_DRIVING, workflow.isMatchedUserDriving).
			endObject();
		return builder;
	}
	
	def findJourneyById(String journeyId, String indexName) {
		GetResponse response =node.client().prepareGet().setId(journeyId).setIndex(indexName).execute().actionGet();
		//GetResponse response = node.client.prepareGet(indexName, indexType, journeyId).execute().actionGet();
		JourneyRequestCommand journeyTemp = parseJourneyFromGetResponse(response)
		return journeyTemp
	}
	
	def findJourneyById(String journeyId, boolean isDummyData) {
		String indexName = resolveIndexName(isDummyData)
		return findJourneyById(journeyId, indexName)
	}
	
	public void markJourneyAsDeleted(JourneyRequestCommand journey) {
		String indexName = JOURNEY.toLowerCase()
		def myMap =["isDeleted":true]
		String indexType = null;
		if(journey.isDriver) {
			indexType = TYPE_DRIVER;
		}
		else {
			indexType = TYPE_RIDER;
		}
		node.client().prepareUpdate(indexName,indexType,journey.id).setDoc(myMap).setRefresh(true).execute().actionGet()
	}
	
	private String resolveIndexName(boolean isDummy) {
		String indexName = null
		if(isDummy) {
			indexName = grailsApplication.config.grails.generatedData.index.name
		}
		else {
			indexName = JOURNEY//getIndexName(currentJourney.dateOfJourney);
			
		}
		
		return indexName
	}
	
	def findWorkfowById(String workflowId) {
		String indexName = WORKFLOW.toLowerCase()
		GetResponse response = node.client.prepareGet(indexName, WORKFLOW, workflowId).execute().actionGet();
		JourneyWorkflow journeyWorkflow = parseWorkflowFromGetResponse(response)
		return journeyWorkflow
	}
	
	def searchWorkflowRequestedByUser(User user) {
		String indexName = WORKFLOW.toLowerCase()
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.termFilter(REQUEST_USER, user.username),
			FilterBuilders.boolFilter().must(FilterBuilders.termFilter("user", user.username))
			
		)
		
		FieldSortBuilder  sorter = SortBuilders.fieldSort(REQUEST_DATE_TIME)
		sorter.order(SortOrder.DESC);
		
		def workflows = searchWorkflow(indexName, WORKFLOW, filter, sorter)
		return workflows
	}
	
	def searchWorkflowRequestedByUserForAJourney(String journeyId, User user) {
		String indexName = WORKFLOW.toLowerCase()
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.termFilter(REQUEST_USER, user.username),
			FilterBuilders.termFilter(REQUEST_JOURNEY_ID, journeyId)
		)
		
		FieldSortBuilder  sorter = SortBuilders.fieldSort(REQUEST_DATE_TIME)
		sorter.order(SortOrder.DESC);
		
		def workflows = searchWorkflow(indexName, WORKFLOW, filter, sorter)
		return workflows
	}
	
	def searchWorkflowMatchedForUser(User user) {
		String indexName = WORKFLOW.toLowerCase()
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.termFilter(MACTHING_USER, user.username),
			FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter("user", user.username))
			
		)
		
		FieldSortBuilder  sorter = SortBuilders.fieldSort(MATCHED_DATE_TIME)
		sorter.order(SortOrder.DESC);
		
		def workflows = searchWorkflow(indexName, WORKFLOW, filter, sorter)
		return workflows
	}
	
	def searchWorkflowMatchedForUserForAJourney(String journeyId, User user) {
		String indexName = WORKFLOW.toLowerCase()
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.termFilter(MACTHING_USER, user.username),
			FilterBuilders.termFilter(MATCHED_JOURNEY_ID, journeyId)
			
		)
		
		FieldSortBuilder  sorter = SortBuilders.fieldSort("matchedDateTime")
		sorter.order(SortOrder.DESC);
		
		def workflows = searchWorkflow(indexName, WORKFLOW, filter, sorter)
		return workflows
	}
	
	def searchWorkflowByRequestedJourney(JourneyRequestCommand command) {
		String indexName = WORKFLOW.toLowerCase()
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.termFilter(REQUEST_USER, command.user),
			FilterBuilders.termFilter(REQUEST_JOURNEY_ID, command.id)
			//FilterBuilders.termFilter('state', "")
		)
		
		def workflows = searchWorkflow(indexName, WORKFLOW, filter, null)
		
		return workflows
	}
	
	def searchWorkflowByMatchedJourney(JourneyRequestCommand command) {
		String indexName = WORKFLOW.toLowerCase()
		
		FilterBuilder incomingRequestFilter = FilterBuilders.andFilter(
			FilterBuilders.termFilter(MACTHING_USER, command.user),
			FilterBuilders.termFilter(MATCHED_JOURNEY_ID, command.id)
			//FilterBuilders.termFilter('state', "")
		)
		
		
		def workflows = searchWorkflow(indexName, WORKFLOW, incomingRequestFilter, null)
		
		return workflows
	}
	
	def searchActiveWorkflowByMatchedJourney(String indexField ,String value) {
		String indexName = WORKFLOW.toLowerCase()
		
		FilterBuilder incomingRequestFilter = FilterBuilders.andFilter(
			FilterBuilders.termFilter(indexField, value),
			FilterBuilders.termsFilter(STATE, WorkflowState.INITIATED.state, WorkflowState.ACCEPTED.state)
		)
		
		
		def workflows = searchWorkflow(indexName, WORKFLOW, incomingRequestFilter, null)
		
		return workflows
	}
	
	def searchWorkflowByJourneyTuple(String requestJourneyId, String matchedJourneyId) {
		String indexName = WORKFLOW.toLowerCase()
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.termFilter(REQUEST_JOURNEY_ID, requestJourneyId),
			FilterBuilders.termFilter(MATCHED_JOURNEY_ID, matchedJourneyId)
			
		)
		
		def workflows = searchWorkflow(indexName, WORKFLOW, filter, null)
		return workflows
	}
	
	
	def findAllJourneysForUserAfterADate(User user, DateTime inputDate) {
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.rangeFilter("dateOfJourney").gte(inputDate),
			FilterBuilders.boolFilter().must(FilterBuilders.termFilter("user", user.username),
				getFilterOnDeletedJourney())
			
		)
		FieldSortBuilder  sorter = SortBuilders.fieldSort("dateOfJourney")
		sorter.order(SortOrder.ASC);
		def journeys = []
		SearchHit[] hits = queryDocument(JOURNEY, [TYPE_DRIVER,TYPE_RIDER] as String[], filter, 100, sorter)
		for (SearchHit searchHit : hits) {
			JourneyRequestCommand journeyTemp = parseJourneyFromSearchHit(searchHit);
			journeys << journeyTemp
		}
		
		return journeys
	}
	
	def int countOfActiveOutgoingRequestsForAJourney(String jounrenyId) {
		
	}
	
	def int countOfActiveIncomingRequestsForAJourney(String jounrenyId) {
	
}
	
	private List searchWorkflow(String indexName, String type, FilterBuilder filter, FieldSortBuilder  sorter ) {
		def workflows = []

		SearchHit[] hits = queryDocument(indexName, [type] as String[], filter, 10, sorter)
		for (SearchHit searchHit : hits) {
			JourneyWorkflow workflow = parseWorkflowFromSearchHit(searchHit);
			workflows << workflow
		}

		return workflows
	}
	
	private JourneyWorkflow parseWorkflowFromSearchHit(SearchHit searchHit) {
		JourneyWorkflow workflow = new JourneyWorkflow();
		String workflowId = searchHit.id
		workflow.id = UUID.fromString(workflowId)
		workflow.requestJourneyId = searchHit.getSource().get(REQUEST_JOURNEY_ID);
		String requestedDateTimeStr = searchHit.getSource().get(REQUEST_DATE_TIME);
		workflow.requestedDateTime = convertElasticSearchDateToDateTime(requestedDateTimeStr).toDate()
		workflow.requestedFromPlace = searchHit.getSource().get(REQUEST_FROM_PLACE);
		workflow.requestedToPlace = searchHit.getSource().get(REQUEST_TO_PLACE);
		workflow.requestUser = searchHit.getSource().get(REQUEST_USER);
		workflow.state = searchHit.getSource().get(STATE)
		workflow.matchingUser = searchHit.getSource().get(MACTHING_USER);
		workflow.matchedJourneyId= searchHit.getSource().get(MATCHED_JOURNEY_ID);
		workflow.matchedFromPlace = searchHit.getSource().get(MATCHED_FROM_PLACE);
		workflow.matchedToPlace = searchHit.getSource().get(MATCHED_TO_PLACE);
		String matchedDateTimeStr = searchHit.getSource().get(MATCHED_DATE_TIME);
		workflow.matchedDateTime = convertElasticSearchDateToDateTime(matchedDateTimeStr).toDate()

		workflow.isRequesterDriving = searchHit.getSource().get(IS_REQUESTER_DRIVING);
		workflow.isMatchedUserDriving = searchHit.getSource().get(IS_MATCHED_USER_DRIVING);
		return workflow
	}
	
	private JourneyWorkflow parseWorkflowFromGetResponse(GetResponse getResponse) {
		JourneyWorkflow workflow = new JourneyWorkflow();
		String workflowId = getResponse.id
		workflow.id = UUID.fromString(workflowId)
		workflow.requestJourneyId = getResponse.getSource().get(REQUEST_JOURNEY_ID);
		String requestedDateTimeStr = getResponse.getSource().get(REQUEST_DATE_TIME);
		workflow.requestedDateTime = convertElasticSearchDateToDateTime(requestedDateTimeStr).toDate()
		workflow.requestedFromPlace = getResponse.getSource().get(REQUEST_FROM_PLACE);
		workflow.requestedToPlace = getResponse.getSource().get(REQUEST_TO_PLACE);
		workflow.requestUser = getResponse.getSource().get(REQUEST_USER);
		workflow.state = getResponse.getSource().get(STATE)
		workflow.matchingUser = getResponse.getSource().get(MACTHING_USER);
		workflow.matchedJourneyId= getResponse.getSource().get(MATCHED_JOURNEY_ID);
		workflow.matchedFromPlace = getResponse.getSource().get(MATCHED_FROM_PLACE);
		workflow.matchedToPlace = getResponse.getSource().get(MATCHED_TO_PLACE);
		String matchedDateTimeStr = getResponse.getSource().get(MATCHED_DATE_TIME);
		workflow.matchedDateTime = convertElasticSearchDateToDateTime(matchedDateTimeStr).toDate()

		workflow.isRequesterDriving = getResponse.getSource().get(IS_REQUESTER_DRIVING);
		workflow.isMatchedUserDriving = getResponse.getSource().get(IS_MATCHED_USER_DRIVING);
		return workflow
	}
	
	public void updateWorkflowState (String workflowId, String newState){
		def myMap =["state":newState]
		node.client().prepareUpdate(WORKFLOW.toLowerCase(),WORKFLOW,workflowId).setDoc(myMap).setRefresh(true).execute().actionGet()
			//setScript("ctx._state=\"" + newState+ "\"").execute().get()
		
	}
	
	private FilterBuilder getFilterOnDeletedJourney () {
		FilterBuilder deletedFilter =FilterBuilders.missingFilter("isDeleted").existence(true).nullValue(true)
		return deletedFilter
	}
	
	private FilterBuilder getFilterOnJourneyStart(DateTime start, DateTime end) {
		return FilterBuilders.rangeFilter("dateOfJourney").from(start).to(end)
	}
	
	private FilterBuilder getFilterOnJourneyFromPositon(JourneyRequestCommand journey, double filterDistance) {
		FilterBuilder geoDistanceFilterFrom =FilterBuilders.geoDistanceFilter("from").point(journey.fromLatitude, journey.fromLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC)
		return geoDistanceFilterFrom
	}
	
	private FilterBuilder getFilterOnJourneyToPositon(JourneyRequestCommand journey, double filterDistance) {
		FilterBuilder geoDistanceFilterTo =FilterBuilders.geoDistanceFilter("to").point(journey.toLatitude, journey.toLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC)
	
		return geoDistanceFilterTo
	}
	
	
	
	
	private searchJourney(String indexName, String[] type, FilterBuilder filter, int size=100, SortBuilder...sorters) {
		def journeys = []

		SearchHit[] hits = queryDocument(indexName, type, filter, size, sorters)
		for (SearchHit searchHit : hits) {
			JourneyRequestCommand journeyTemp = parseJourneyFromSearchHit(searchHit);
			journeys << journeyTemp
		}

		return journeys
	}
	
	private SearchRequestBuilder enrichBuilderWithSortInformation (SearchRequestBuilder builder, SortBuilder...sorters) {
		for(SortBuilder sorter : sorters) {
			builder.addSort(sorter)
		}
		
		return builder
	}
	
	private SearchHit[] queryDocument(String indexName, String[] indexType, FilterBuilder filter, int size=100, SortBuilder...sorters){
		SearchHit[] hits = []
		try {
		SearchRequestBuilder builder = node.client.prepareSearch(indexName)
				.setTypes(indexType)
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.matchAllQuery())
				.setPostFilter(filter)   // Filter
		enrichBuilderWithSortInformation(builder, sorters)
		builder.setSize(size)
		SearchResponse searchResponse = builder.execute().actionGet();
		SearchHits searchHits = searchResponse.getHits();
		hits = searchHits.hits;
		}
		catch (IndexMissingException exception) {
			log.error "Index name ${indexName} does not exists"
		}
		return hits
	}
	
	def findAllJourneysForUserBeforeADate(User user, DateTime inputDate) {
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.rangeFilter("dateOfJourney").lte(inputDate),
			FilterBuilders.boolFilter().must(FilterBuilders.termFilter("user", user.username),
				getFilterOnDeletedJourney())
			
		)
		FieldSortBuilder  sorter = SortBuilders.fieldSort("dateOfJourney")
		sorter.order(SortOrder.DESC);
		def journeys = []
		SearchHit[] hits = queryDocument(JOURNEY, [TYPE_DRIVER,TYPE_RIDER] as String[], filter, 100, sorter)
		for (SearchHit searchHit : hits) {
			JourneyRequestCommand journeyTemp = parseJourneyFromSearchHit(searchHit);
			journeys << journeyTemp
		}
		
		return journeys
	}
}
