package com.racloop;

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
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.sort.SortBuilders


class ElasticSearchService {

	public final String TYPE_DRIVER = "driver"
	public final String TYPE_RIDER = "rider"
	public final DateTimeFormatter BASIC_DATE_FORMAT = ISODateTimeFormat.basicDateTimeNoMillis();
	public final String DEFAULT_INDEX_NAME_FOMRAT = "dd-MMM-yyyy"
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

	def indexJourney(Journey journey) {
		log.info "Adding record to elastic search ${journey}"
		def sourceBuilder = createJourneyJson(journey)
		String indexName = getIndexName(journey.dateOfJourney)
		String type = getType(journey);
		IndexRequest request = new IndexRequest(indexName, type).id(journey.id + '').source(sourceBuilder);
		node.client.index(request).actionGet();
		log.info "successfully indexed ${journey}"
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
			def builder = createTypeJson(TYPE_DRIVER)
			node.client.admin().indices().preparePutMapping(indexName).setType(TYPE_DRIVER).setSource(builder).execute().actionGet();
			log.info "Type : ${TYPE_DRIVER} for index : ${indexName} created successfully"

			log.info "Started creating type : ${TYPE_DRIVER} for index : ${indexName}"
			builder = createTypeJson(TYPE_RIDER)
			node.client.admin().indices().preparePutMapping(indexName).setType(TYPE_RIDER).setSource(builder).execute().actionGet();
			log.info "Type : ${TYPE_RIDER} for index : ${indexName} created successfully"
		}
	}

	private def createTypeJson(String type) {
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

	private def createJourneyJson(Journey journey) {
		GeoPoint from = new GeoPoint(journey.fromLatitude, journey.fromLongitude)
		GeoPoint to = new GeoPoint(journey.toLatitude, journey.toLongitude)
		DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
		DateTime createdDate = new DateTime(journey.createdDate)
		XContentBuilder builder = XContentFactory.jsonBuilder().
		startObject().
		field("user", journey.user.username).
		field("name", journey.user.profile.fullName).
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

	Journey getJourney(id) {
		return null
	}

	def search(User user, Journey journey) {
		String indexName = getIndexName(journey.dateOfJourney);
		String searchTypeOpposite = null;
		if(journey.isDriver) {
			searchTypeOpposite = TYPE_RIDER;
		}
		else {
			searchTypeOpposite = TYPE_DRIVER;
		}
		double filterDistance = journey.tripDistance / 5;
		DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
		DateTime start = dateOfJourney.minusHours(4);
		DateTime end = dateOfJourney.plusHours(4);
		DateTime validStartTime = new DateTime(journey.validStartTime)
		if(start.isBefore(validStartTime)) {
			start = validStartTime;
		}
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.rangeFilter("dateOfJourney").from(start.toString(BASIC_DATE_FORMAT)).to(end.toString(BASIC_DATE_FORMAT)),
			FilterBuilders.geoDistanceFilter("from").point(journey.fromLatitude, journey.fromLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.PLANE),
			FilterBuilders.geoDistanceFilter("to").point(journey.toLatitude, journey.toLongitude).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.PLANE),
			FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter("user", journey.user.username))
		);
		SortBuilder sorter = SortBuilders.geoDistanceSort("from");
		SearchResponse searchResponse = node.client.prepareSearch(indexName)
			.setTypes(searchTypeOpposite)
			.setSearchType(SearchType.QUERY_AND_FETCH)
			.setQuery(QueryBuilders.matchAllQuery())             // Query
			.setFilter(filter)   // Filter
			.setFrom(0).setSize(10).addSort(sorter).setExplain(true)
			.execute()
			.actionGet();
		SearchHits searchHits = searchResponse.getHits();
		SearchHit[] hits = searchHits.hits;
		def journeys = []
		for (SearchHit searchHit : hits) {
			Journey journeyTemp = parseJourney(searchHit);
			journeys << journeyTemp
		}
		return journeys
	}

	private Journey parseJourney(SearchHit searchHit) {
		Journey journeyTemp = new Journey();
		journeyTemp.id = searchHit.getSource().get('id');
		journeyTemp.name = searchHit.getSource().get('name');
		String dateStr = searchHit.getSource().get('dateOfJourney');
		DateTime dateTime = BASIC_DATE_FORMAT.parseDateTime(dateStr);
		journeyTemp.dateOfJourney = dateTime.toDate();
		journeyTemp.fromPlace = searchHit.getSource().get('fromPlace');
		journeyTemp.toPlace = searchHit.getSource().get('toPlace')
		return journeyTemp
	}

	private String getIndexName(Date dateOfJourney) {
		String indexNameFormat = null;
		String indexNameFormatType = grailsApplication.config.grails.indexNameFormatType;
		if(indexNameFormatType == "Monthly") {
			indexNameFormat = grailsApplication.config.grails.monthlyIndexNameFormat
		}
		else {
			indexNameFormat = grailsApplication.config.grails.dailyIndexNameFormat
		}
		if(!indexNameFormat) indexNameFormat = DEFAULT_INDEX_NAME_FOMRAT;
		String indexName = dateOfJourney.format(indexNameFormat);
		return indexName.toLowerCase();
	}

	private String getType(Journey journey) {
		if(journey.isDriver) {
			return TYPE_DRIVER
		}
		else {
			return TYPE_RIDER
		}
	}
}
