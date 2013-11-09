package com.racloop;

import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexRequest
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
import org.elasticsearch.search.sort.GeoDistanceSortBuilder
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder


class ElasticSearchService {

	public static final int DISTANCE_FACTOR = 8
	public static final String TYPE_DRIVER = "driver"
	public static final String TYPE_RIDER = "rider"
	public static final DateTimeFormatter BASIC_DATE_FORMAT = ISODateTimeFormat.basicDateTimeNoMillis();
	public static final String DEFAULT_INDEX_NAME_FOMRAT = "dd-MMM-yyyy"
	public static final String DUMMY_INDEX_NAME = "dummy_data"
	def grailsApplication
	Node node

	def init() {
		log.info "Initialising elastic search client"
		node = NodeBuilder.nodeBuilder().node()
	}

	def destroy() {
		node.close()
	}

	def indexJourneyWithIndexCheck(Journey journey) {
		createIndexIfNotExistsForDate(journey.dateOfJourney);
		indexJourney(journey)
	}

	def indexJourney(User user, JourneyRequestCommand journey) {
		log.info "Adding record to elastic search ${journey}"
		def sourceBuilder = createJourneyJson(user, journey)
		String indexName = getIndexName(journey.dateOfJourney)
		String type = getType(journey);
		IndexRequest indexRequest = new IndexRequest(indexName, type).id(journey.id + '').source(sourceBuilder);
		node.client.index(indexRequest).actionGet();
		log.info "successfully indexed ${journey}"
	}

	def indexDummyJourney(JourneyRequestCommand journey) {
		log.info "Adding dummy record to elastic search ${journey}"
		def sourceBuilder = createDummyJourneyJson(journey)
		String indexName = DUMMY_INDEX_NAME
		String type = getType(journey);
		IndexRequest indexRequest = new IndexRequest(indexName, type).source(sourceBuilder);
		node.client.index(indexRequest).actionGet();
		log.info "successfully indexed dummy journey : ${journey}"
	}

	def createIndexIfNotExistsForDate(Date date) {
		String indexName = getIndexName(date)
		boolean isIndexExists = node.client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
		if(isIndexExists) {
			log.info "Index name : ${indexName} already exists"
		}
		else {
			log.info "Started creating Index name : ${indexName}"
			node.client.admin().indices().prepareCreate(indexName).execute().actionGet();
			log.info "Index name : ${indexName} created successfuly"
			log.info "Started creating type : ${TYPE_DRIVER} for index : ${indexName}"
			def builder = createMainIndexJson(TYPE_DRIVER)
			node.client.admin().indices().preparePutMapping(indexName).setType(TYPE_DRIVER).setSource(builder).execute().actionGet();
			log.info "Type : ${TYPE_DRIVER} for index : ${indexName} created successfully"

			log.info "Started creating type : ${TYPE_DRIVER} for index : ${indexName}"
			builder = createMainIndexJson(TYPE_RIDER)
			node.client.admin().indices().preparePutMapping(indexName).setType(TYPE_RIDER).setSource(builder).execute().actionGet();
			log.info "Type : ${TYPE_RIDER} for index : ${indexName} created successfully"
		}
	}
	
	def createDummyDataIndexIfNotExists() {
		String indexName = DUMMY_INDEX_NAME
		boolean isIndexExists = node.client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
		if(isIndexExists) {
			log.info "Index name : ${indexName} already exists"
		}
		else {
			log.info "Started creating Dummy Index name : ${indexName}"
			node.client.admin().indices().prepareCreate(indexName).execute().actionGet();
			log.info "Dummy Index name : ${indexName} created successfuly"
			log.info "Started creating type : ${TYPE_DRIVER} for Dummy index : ${indexName}"
			def builder = createDummyDataIndexJson(TYPE_DRIVER)
			node.client.admin().indices().preparePutMapping(indexName).setType(TYPE_DRIVER).setSource(builder).execute().actionGet();
			log.info "Type : ${TYPE_DRIVER} for Dummy index : ${indexName} created successfully"

			log.info "Started creating type : ${TYPE_DRIVER} for Dummy index : ${indexName}"
			builder = createDummyDataIndexJson(TYPE_RIDER)
			node.client.admin().indices().preparePutMapping(indexName).setType(TYPE_RIDER).setSource(builder).execute().actionGet();
			log.info "Type : ${TYPE_RIDER} for Dummy index : ${indexName} created successfully"
		}
	}
	
	def closeIndexIfExistsForDate(Date date) {
		String indexName = getIndexName(date)
		boolean isIndexExists = node.client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
		if(isIndexExists) {
			log.info "Index name : ${indexName} exists"
			log.info "Started closing Index name : ${indexName}"
			node.client.admin().indices().prepareClose(indexName).execute().actionGet();
			log.info "Index name : ${indexName} closed successfully"
		}
		else {
			log.warn "Index name : ${indexName} does not exists"
		}
	}

	private def createMainIndexJson(String type) {
		XContentBuilder builder = XContentFactory.jsonBuilder().
		startObject().
			startObject(type).
				startObject("properties").
					startObject("user").
					field("type", "string").field("index", "no").
					endObject().
					startObject("name").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("dateOfJourney").
					field("type", "date").field("format", "basic_date_time_no_millis").
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
					field("type", "date").field("format", "basic_date_time_no_millis").
					endObject().
				endObject().
			endObject().
		endObject();
		return builder;
	}

	private def createDummyDataIndexJson(String type) {
		XContentBuilder builder = XContentFactory.jsonBuilder().
		startObject().
			startObject(type).
				startObject("properties").
					startObject("name").
					field("type", "string").field("index", "not_analyzed").
					endObject().
					startObject("dateOfJourney").
					field("type", "date").field("format", "basic_date_time_no_millis").
					endObject().
					startObject("from").
					field("type", "geo_point").field("lat_lon", "true").field("geohash", "true").
					endObject().
					startObject("to").
					field("type", "geo_point").field("lat_lon", "true").field("geohash", "true").
					endObject().
				endObject().
			endObject().
		endObject();
		return builder;
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
				field("dateOfJourney", dateOfJourney.toString(BASIC_DATE_FORMAT)).
				field("fromPlace", journey.fromPlace).
				field("from", from).
				field("toPlace", journey.toPlace).
				field("to", to).
				field("requesterIp", journey.ip).
				field("createdDate", createdDate.toString(BASIC_DATE_FORMAT)).
			endObject();
		return builder;
	}
	
	private def createDummyJourneyJson(JourneyRequestCommand journey) {
		GeoPoint from = new GeoPoint(journey.fromLatitude, journey.fromLongitude)
		GeoPoint to = new GeoPoint(journey.toLatitude, journey.toLongitude)
		DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
		DateTime createdDate = new DateTime(journey.createdDate)
		XContentBuilder builder = XContentFactory.jsonBuilder().
			startObject().
				field("name", journey.name).
				field("dateOfJourney", dateOfJourney.toString(BASIC_DATE_FORMAT)).
				field("from", from).
				field("to", to).
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
		FilterBuilder filter = FilterBuilders.andFilter(
			//FilterBuilders.limitFilter(10),
			FilterBuilders.rangeFilter("dateOfJourney").from(start.toString(BASIC_DATE_FORMAT)).to(end.toString(BASIC_DATE_FORMAT)),
			FilterBuilders.geoDistanceFilter("from").point(journey.fromLatitude, journey.fromLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC),
			FilterBuilders.geoDistanceFilter("to").point(journey.toLatitude, journey.toLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC),
			FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter("user", user.username))
			
		);
		GeoDistanceSortBuilder sorter = SortBuilders.geoDistanceSort("from");
		sorter.point(journey.fromLatitude, journey.fromLongitude);
		sorter.order(SortOrder.ASC);
		def journeys = []
		try {			
			SearchResponse searchResponse = node.client.prepareSearch(indexName)
				.setTypes(searchTypeOpposite)
				.setSearchType(SearchType.QUERY_AND_FETCH)
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
	
	def searchDummyData(JourneyRequestCommand journey) {
		String indexName = DUMMY_INDEX_NAME;
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
			FilterBuilders.rangeFilter("dateOfJourney").from(start.toString(BASIC_DATE_FORMAT)).to(end.toString(BASIC_DATE_FORMAT)),
			FilterBuilders.geoDistanceFilter("from").point(journey.fromLatitude, journey.fromLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC),
			FilterBuilders.geoDistanceFilter("to").point(journey.toLatitude, journey.toLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC)			
		);
		GeoDistanceSortBuilder sorter = SortBuilders.geoDistanceSort("from");
		sorter.point(journey.fromLatitude, journey.fromLongitude);
		sorter.order(SortOrder.ASC);
		def journeys = []
		try {
			SearchResponse searchResponse = node.client.prepareSearch(indexName)
				.setTypes(searchTypeOpposite)
				.setSearchType(SearchType.QUERY_AND_FETCH)
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

	private JourneyRequestCommand parseJourneyFromSearchHit(SearchHit searchHit) {
		JourneyRequestCommand journeyTemp = new JourneyRequestCommand();
		journeyTemp.id = searchHit.getSource().get('id');
		journeyTemp.name = searchHit.getSource().get('name');
		String dateStr = searchHit.getSource().get('dateOfJourney');
		DateTime dateTime = BASIC_DATE_FORMAT.parseDateTime(dateStr);
		journeyTemp.dateOfJourney = dateTime.toDate();
		journeyTemp.fromPlace = searchHit.getSource().get('fromPlace');
		journeyTemp.toPlace = searchHit.getSource().get('toPlace')
		return journeyTemp
	}

	private JourneyRequestCommand parseDummyJourneyFromSearchHit(SearchHit searchHit) {
		JourneyRequestCommand journeyTemp = new JourneyRequestCommand();
		journeyTemp.name = searchHit.getSource().get('name');
		String dateStr = searchHit.getSource().get('dateOfJourney');
		DateTime dateTime = BASIC_DATE_FORMAT.parseDateTime(dateStr);
		journeyTemp.dateOfJourney = dateTime.toDate();
		GeoPoint from = searchHit.getSource().get('from');
		GeoPoint to = searchHit.getSource().get('to');
		journeyTemp.fromLatitude = from.lat();
		journeyTemp.fromLongitude = from.lon();
		journeyTemp.toLatitude = to.lat();
		journeyTemp.toLongitude = to.lon();
		return journeyTemp
	}
	
	
	
	private JourneyRequestCommand parseJourneyFromGetResponse(GetResponse getResponse) {
		JourneyRequestCommand journeyTemp = new JourneyRequestCommand();
		journeyTemp.id = getResponse.getSource().get('id');
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
		return journeyTemp
	}

	private String getIndexName(Date dateOfJourney) {
		String indexName = dateOfJourney.format(DEFAULT_INDEX_NAME_FOMRAT);
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
}
