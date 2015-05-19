package com.racloop.integration

import org.elasticsearch.search.SearchHit;

import java.text.SimpleDateFormat;

import grails.transaction.Transactional
import grails.util.Environment;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.racloop.Constant;
import com.racloop.DistanceUtil;
import com.racloop.NamesUtil;
import com.racloop.Place;
import com.racloop.User;
import com.racloop.domain.Journey
import com.racloop.elasticsearch.IndexDefinitor
import com.racloop.elasticsearch.IndexMetadata;
import com.racloop.elasticsearch.IndexNameResolver

@Transactional
class SearchService {

	def grailsApplication
	public static final int DISTANCE_FACTOR = 8
	private static final int DAYS_OF_WEEK = 7
	private static final int WEEKS_IN_A_YEAR = 52
	private Node node
	private IndexNameResolver indexNameResolver = new IndexNameResolver()
	private DateTime FROM_DATE = new DateTime(2015,4,1,0,1)
	private DateTime TO_DATE = new DateTime(2015,11,1,0,1)

	def init(Node node) {
		log.info "Initialising elastic search client"
//		if (Environment.current == Environment.DEVELOPMENT) {
//			node = NodeBuilder.nodeBuilder().node();
//		}
//		else {
//			node = NodeBuilder.nodeBuilder().client(true).node();
//		}
		//node = NodeBuilder.nodeBuilder().node();
		this.node = node
	}

	def destroy() {
		//node.close()
	}

	/**** Journey Handling START (Main Index) ****/
	
	def createJourneyIndex() {
		IndexMetadata indexDefinitor = new IndexMetadata();
		indexDefinitor.createJourneyIndex(node, IndexMetadata.JOURNEY_INDEX_NAME);
	}

	def indexJourney(Journey journey, String id) {
		def sourceBuilder = createJourneyJson(journey)
		IndexRequest indexRequest = new IndexRequest(IndexMetadata.JOURNEY_INDEX_NAME, IndexMetadata.DEFAULT_TYPE, id).source(sourceBuilder);
		IndexResponse indexResponse = node.client.index(indexRequest).actionGet();
		log.info "Successfully indexed ${journey} with ${indexResponse.getId()}"
		return indexResponse.getId();
	}

	private def createJourneyJson(Journey journey) {
		GeoPoint fromGeoPoint = new GeoPoint(journey.getFromLatitude(), journey.getFromLongitude())
		GeoPoint toGeoPoint = new GeoPoint(journey.getToLatitude(), journey.getToLongitude())
		DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
		DateTime createdDate = new DateTime(journey.createdDate)
		XContentBuilder builder = XContentFactory.jsonBuilder().
			startObject().
				field("mobile", journey.getMobile()).
				field("dateOfJourney", dateOfJourney).
				field("email", journey.getEmail()).
				field("name", journey.getName()).
				field("from", journey.getFrom()).
				field("fromGeoPoint", fromGeoPoint).
				field("to", journey.getTo()).
				field("toGeoPoint", toGeoPoint).
				field("isDriver", journey.getIsDriver()).
				field("isMale", journey.getIsMale()).
				field("tripDistance", journey.getTripDistance()).
				field("photoUrl", journey.getPhotoUrl()).
				field("isDummy", journey.getIsDummy()).
			endObject();
		return builder;
	}

	Journey getJourney(String journeyId) {
		GetResponse getResponse = node.client.prepareGet(IndexMetadata.JOURNEY_INDEX_NAME, IndexMetadata.DEFAULT_TYPE, journeyId).execute().actionGet();
		Journey journey = null;
		if(getResponse.isExists()) {
			journey = parseJourneyFromGetResponse(getResponse);
			journey.setId(journeyId);
		}
		return journey;
	}

	private Journey parseJourneyFromGetResponse(GetResponse getResponse) {
		Journey journey = new Journey();
		journey.setMobile(getResponse.getSource().get('mobile'));
		SimpleDateFormat formatter = new SimpleDateFormat(Constant.DATE_FORMAT_DYNAMODB);
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = formatter.parse(getResponse.getSource().get('dateOfJourney'));
		journey.setDateOfJourney(date);
		journey.setEmail(getResponse.getSource().get('email'));
		journey.setName(getResponse.getSource().get('name'));
		journey.setFrom(getResponse.getSource().get('from'));
		GeoPoint fromGeoPoint = getResponse.getSource().get('fromGeoPoint')
		journey.setFromLatitude(fromGeoPoint.getLat());
		journey.setFromLongitude(fromGeoPoint.getLon());
		GeoPoint toGeoPoint = getResponse.getSource().get('toGeoPoint')
		journey.setToLatitude(toGeoPoint.getLat());
		journey.setToLongitude(toGeoPoint.getLon());
		journey.setIsDriver(getResponse.getSource().get('isDriver'));
		journey.setIsMale(getResponse.getSource().get('isMale'));
		journey.setTripDistance(getResponse.getSource().get('tripDistance'));
		journey.setPhotoUrl(getResponse.getSource().get('photoUrl'));
		return journey;
	}

	def deleteJourney(String journeyId) {
		DeleteResponse deleteResponse = node.client.prepareDelete(IndexMetadata.JOURNEY_INDEX_NAME, IndexMetadata.DEFAULT_TYPE, journeyId).execute().actionGet();
	}

	def deleteJourneyForDate(Date date) {
		// TODO - user delete by query API
	}

	/**
	 * Used for search in main index as well as dummy (or generated data) index
	 */
	def search(String indexName, Date timeOfJourney, Date validStartTime, String mobile, Double fromLat, Double fromLon, Double toLat, Double toLon) {
		log.info("search index ${indexName}")
		DateTime dateOfJourney = new DateTime(timeOfJourney);
		DateTime start = dateOfJourney.minusHours(4);
		DateTime end = dateOfJourney.plusHours(4);
		DateTime validTime = new DateTime(validStartTime)
		if(start.isBefore(validTime)) {
			start = validTime;
		}
		FilterBuilder dateRanageFilter = FilterBuilders.rangeFilter("dateOfJourney").from(start).to(end);

		Double tripDistance = DistanceUtil.distance(fromLat, fromLon, toLat, toLon);
		double filterDistance = tripDistance / DISTANCE_FACTOR;
		FilterBuilder geoDistanceFilterFrom = FilterBuilders.geoDistanceFilter("fromGeoPoint").point(fromLat, fromLon).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC);
		FilterBuilder geoDistanceFilterTo =FilterBuilders.geoDistanceFilter("toGeoPoint").point(toLat, toLon).distance(filterDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC)

		FilterBuilder filter = FilterBuilders.andFilter(
				dateRanageFilter,
				geoDistanceFilterFrom,
				geoDistanceFilterTo)

		if(mobile) {
			filter = FilterBuilders.andFilter(filter, FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter("mobile", mobile)));
		}

		GeoDistanceSortBuilder sorter = SortBuilders.geoDistanceSort("fromGeoPoint");
		sorter.point(fromLat, fromLon);
		sorter.order(SortOrder.ASC);

		FieldSortBuilder startTimeSorter = new FieldSortBuilder("dateOfJourney").order(SortOrder.ASC);

		//SearchHit[] hits = queryDocuments(indexName, "_all", filter, 100, sorter, startTimeSorter);
		SearchHit[] hits = queryDocuments(indexName, IndexMetadata.DEFAULT_TYPE, filter, 20, sorter, startTimeSorter);
		def searchResults = [];
		for (SearchHit searchHit : hits) {
			Journey item = parseJourneyFromSearchHit(searchHit);
			searchResults << item
		}
		return searchResults;
	}

	private SearchHit[] queryDocuments(String indexName, String indexType, FilterBuilder filter, int size, SortBuilder...sorters){
		SearchHit[] hits = []
		try {
			SearchRequestBuilder builder = node.client.prepareSearch(indexName)
					.setTypes(indexType)
					.setSearchType(SearchType.QUERY_THEN_FETCH)
					.setQuery(QueryBuilders.matchAllQuery())
					.setPostFilter(filter)   // Filter
			for(SortBuilder sorter : sorters) {
				builder.addSort(sorter)
			}
			builder.setSize(size)
			SearchResponse searchResponse = builder.execute().actionGet();
			SearchHits searchHits = searchResponse.getHits();
			hits = searchHits.hits;
		}
		catch (IndexMissingException exception) {
			log.error ("Index name ${indexName} does not exists", exception)
		}
		return hits
	}
	
	private Journey parseJourneyFromSearchHit(SearchHit searchHit) {
		Journey journey = new Journey();
		journey.setId(searchHit.id);
		journey.setMobile(searchHit.getSource().get('mobile'));
		SimpleDateFormat formatter = new SimpleDateFormat(Constant.DATE_FORMAT_DYNAMODB);
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = formatter.parse(searchHit.getSource().get('dateOfJourney'));
		journey.setDateOfJourney(date);
		journey.setEmail(searchHit.getSource().get('email'));
		journey.setName(searchHit.getSource().get('name'));
		journey.setFrom(searchHit.getSource().get('from'));
		GeoPoint fromGeoPoint = searchHit.getSource().get('fromGeoPoint')
		journey.setFromLatitude(fromGeoPoint.getLat());
		journey.setFromLongitude(fromGeoPoint.getLon());
		journey.setTo(searchHit.getSource().get('to'));
		GeoPoint toGeoPoint = searchHit.getSource().get('toGeoPoint')
		journey.setToLatitude(toGeoPoint.getLat());
		journey.setToLongitude(toGeoPoint.getLon());
		journey.setIsDriver(searchHit.getSource().get('isDriver'));
		journey.setIsMale(searchHit.getSource().get('isMale'));
		journey.setTripDistance(searchHit.getSource().get('tripDistance'));
		journey.setPhotoUrl(searchHit.getSource().get('photoUrl'));
		return journey;
	}
	
	
	
	def findAllJourneysForUserBetweenDates(String mobile, DateTime startDate, DateTime endDate) {
		FilterBuilder filter = FilterBuilders.andFilter(
			FilterBuilders.rangeFilter("dateOfJourney").gte(startDate),
			FilterBuilders.rangeFilter("dateOfJourney").lte(endDate),
			FilterBuilders.boolFilter().must(FilterBuilders.termFilter("mobile", mobile))
			
		)
		FieldSortBuilder  sorter = SortBuilders.fieldSort("dateOfJourney")
		sorter.order(SortOrder.ASC);
		def journeys = []
		SearchHit[] hits = queryDocuments(IndexMetadata.JOURNEY_INDEX_NAME, IndexMetadata.DEFAULT_TYPE, filter, 20, sorter)
		def searchResults = [];
		for (SearchHit searchHit : hits) {
			Journey item = parseJourneyFromSearchHit(searchHit);
			searchResults << item
		}
		return searchResults
	}
	
	/**
	 * DANGER - use in dev environment only
	 * @return
	 */
	def deleteAllJourneyData() {
		if (Environment.current == Environment.DEVELOPMENT) {
			log.info "Deleting INDEX ${IndexMetadata.JOURNEY_INDEX_NAME}"
			node.client.prepareDeleteByQuery(IndexMetadata.JOURNEY_INDEX_NAME).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
			log.info "Deleting INDEX ${IndexMetadata.DUMMY_INDEX_NAME}"
			node.client.prepareDeleteByQuery(IndexMetadata.DUMMY_INDEX_NAME).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
		}
	}
	
	/**** Journey Handling ENDS (Main Index) ****/

	/**** Generated dummy data Handling START ****/
	
	def createGeneratedJourneyIndex() {
		IndexMetadata indexDefinitor = new IndexMetadata();
		indexDefinitor.createDummyIndex(node, IndexMetadata.DUMMY_INDEX_NAME);
	}

	def indexGeneratedJourney(Journey journey) {
		log.info "Adding record to elastic search ${journey}"
		def sourceBuilder = createJourneyJson(journey)
		IndexRequest indexRequest = new IndexRequest(IndexMetadata.DUMMY_INDEX_NAME, IndexMetadata.DEFAULT_TYPE).source(sourceBuilder);
		IndexResponse indexResponse = node.client.index(indexRequest).actionGet();
		log.info "Successfully indexed ${journey} with ${indexResponse.getId()}"
		return indexResponse.getId();
	}
	
	def generateData(Date timeOfJourney, String mobile, Double fromLat, Double fromLon, Double toLat, Double toLon) {
		DateTime tempDate = new DateTime(timeOfJourney);
		if(Environment.current.getName() == "production") {
			if(tempDate.getHourOfDay() > 20 || tempDate.getHourOfDay() < 7) return [];
		}
		Random randomGenerator = new Random();
		Integer numberOfRecords = randomGenerator.nextInt(5);
		if(numberOfRecords < 2) numberOfRecords = 2;//expecting 2,3,4 number of records
		def journeys = [];
		def names = NamesUtil.getRandomBoyNames(numberOfRecords);
		double tripDistance = DistanceUtil.distance(fromLat, fromLon, toLat, toLon);
		def maxDistance = tripDistance / (DISTANCE_FACTOR + 1)
		Place [] fromPlaces = searchNearLocations(maxDistance, fromLat, fromLon, numberOfRecords);
		Place [] toPlaces = searchNearLocations(maxDistance, toLat, toLon, numberOfRecords);
		Random randomMinutesGenerator = new Random();
		int index = 0;
		final def random = new Random();
		final def possibleMinutes = [0, 15, 30, 45]
		if(fromPlaces.size() <= toPlaces.size()) {
			fromPlaces.each {
				def i = random.nextInt(possibleMinutes.size())
				tempDate = tempDate.plusMinutes(possibleMinutes[i]);
				def name = names[index]
				def fromPlace = it;
				def toPlace = toPlaces[index]
				Journey journey = new Journey();
				journey.mobile = '9800000000';
				journey.dateOfJourney = tempDate.toDate();
				journey.email= 'dummy@racloop.com';
				journey.name= name;
				journey.isDriver = false;
				journey.fromLatitude = fromPlace.location.lat();
				journey.fromLongitude = fromPlace.location.lon();
				journey.from = fromPlace.name
				journey.toLatitude = toPlace.location.lat();
				journey.toLongitude = toPlace.location.lon();
				journey.to = toPlace.name
				journey.tripDistance = 100;
				journey.isDummy = true;
				journey.id = indexGeneratedJourney(journey)
				journeys << journey
				index++
			}
		}
		else {
			toPlaces.each {
				def i = random.nextInt(possibleMinutes.size())
				tempDate = tempDate.plusMinutes(possibleMinutes[i]);
				def name = names[index]
				def toPlace = it;
				def fromPlace = fromPlaces[index]
				Journey journey = new Journey();
				journey.mobile = '9800000000';
				journey.dateOfJourney = tempDate.toDate();
				journey.email= 'dummy@racloop.com';
				journey.name= name;
				journey.isDriver = false;
				journey.fromLatitude = fromPlace.location.lat();
				journey.fromLongitude = fromPlace.location.lon();
				journey.from = fromPlace.name
				journey.toLatitude = toPlace.location.lat();
				journey.toLongitude = toPlace.location.lon();
				journey.to = toPlace.name
				journey.tripDistance = 100;
				journey.isDummy = true;
				journey.id = indexGeneratedJourney(journey)
				journeys << journey
				index++
			}
		}
		return journeys;
	}
	
	/**** Generated dummy data Handling START ****/
	
	/**** Places or Location Index Handling END ****/
	/**** Used for generated or dummy data ****/
	
	def createLocationIndex() { //used for dummy generated data
		String indexName = IndexMetadata.LOCATION_MASTER_INDEX_NAME;
		String indexType = IndexMetadata.LOCATION_MASTER_INDEX_TYPE_NAME;
		IndexDefinitor indexDefinitor = new IndexDefinitor();
		indexDefinitor.createMasterLocationIndex(node, indexName, indexType);
	}
	
	def indexLocation(Place place) {
		String indexName = IndexMetadata.LOCATION_MASTER_INDEX_NAME;
		String indexType = IndexMetadata.LOCATION_MASTER_INDEX_TYPE_NAME;
		log.debug "Adding record to location master index in elastic search ${place}"
		def sourceBuilder = createPlaceJson(place);
		IndexRequest indexRequest = new IndexRequest(indexName, indexType).source(sourceBuilder);
		node.client.index(indexRequest).actionGet();
		log.debug "Successfully indexed place : ${place}"
	}
	
	private def createPlaceJson(Place place) {
		XContentBuilder builder = XContentFactory.jsonBuilder().
			startObject().
				field("name", place.name).
				field("location", place.location).
			endObject();
		return builder;
	}
	
	def searchNearLocations(double maxDistance, double lattitude, double longitude, int maxRecords) {
		String indexName = IndexMetadata.LOCATION_MASTER_INDEX_NAME;
		String indexType = IndexMetadata.LOCATION_MASTER_INDEX_TYPE_NAME;
		FilterBuilder filter = FilterBuilders.geoDistanceFilter("location").point(lattitude, longitude).distance(maxDistance, DistanceUnit.KILOMETERS).optimizeBbox("memory").geoDistance(GeoDistance.PLANE)
		GeoDistanceSortBuilder sorter = SortBuilders.geoDistanceSort("location");
		sorter.point(lattitude, longitude);
		sorter.order(SortOrder.ASC);
		def places = [];

		SearchHit[] hits = queryDocuments(indexName, indexType, filter, maxRecords, sorter)
		for (SearchHit searchHit : hits) {
			Place place = parsePlaceFromSearchHit(searchHit);
			places << place
		}
		
		return places
	}
	
//	private SearchHit[] queryDocument(String[] indexName, String[] indexType, FilterBuilder filter, int size=100, SortBuilder...sorters){
//		SearchHit[] hits = []
//		try {
//		SearchRequestBuilder builder = node.client.prepareSearch(indexName)
//				.setTypes(indexType)
//				.setSearchType(SearchType.QUERY_THEN_FETCH)
//				.setQuery(QueryBuilders.matchAllQuery())
//				.setPostFilter(filter)   // Filter
//		for(SortBuilder sorter : sorters) {
//			builder.addSort(sorter)
//		}
//		builder.setSize(size)
//		SearchResponse searchResponse = builder.execute().actionGet();
//		SearchHits searchHits = searchResponse.getHits();
//		hits = searchHits.hits;
//		}
//		catch (IndexMissingException exception) {
//			log.error ("Index name ${indexName} does not exists", exception)
//		}
//		return hits
//	}
	
	private Place parsePlaceFromSearchHit(SearchHit searchHit) {
		Place place = new Place();
		place.name = searchHit.getSource().get('name');
		place.location = searchHit.getSource().get('location');
		return place;
	}
	
	/**** Location Index Handling ENDS ****/
	
}
