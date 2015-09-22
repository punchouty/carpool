package com.racloop.persistence

import grails.transaction.Transactional

import java.text.ParseException
import java.text.SimpleDateFormat

import org.elasticsearch.common.joda.time.DateTime

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import com.racloop.GenericUtil
import com.racloop.User
import com.racloop.domain.Journey
import com.racloop.domain.JourneyPair
import com.racloop.domain.Route
import com.racloop.domain.UserJourney
import com.racloop.domain.WayPoint
import com.racloop.journey.workkflow.WorkflowStatus

@Transactional
class JourneyDataService {
	
	def grailsApplication
	def awsService
	def searchService
	def jmsService
	def journeyPairDataService

	def createJourney(Journey journey) {
		this.saveJourney(journey)
		log.info("createJourney - ${journey}");
		searchService.indexJourney(journey, journey.getId());
	}
	
	def allJourneyData(String id) {
		Journey currentJourney = awsService.dynamoDBMapper.load(Journey.class, id);
		return allJourneyData(currentJourney)
	}
	
	def allJourneyData(Journey currentJourney){
		Set<JourneyPair> journeyPairs = new HashSet<JourneyPair>();
		HashMap<String, Journey> allRelatedJourneysAsMap = new HashMap<String, Journey>()
		Set<String> journeyPairIds = currentJourney.getJourneyPairIds();
		Set<String> ids = new HashSet<String>()
		journeyPairIds.each { pairId ->
			JourneyPair journeyPair = journeyPairDataService.findPairById(pairId);
			String journeyId = journeyPair.initiatorJourneyId;
			ids << journeyId;
			Journey journey = allRelatedJourneysAsMap.get(journeyId);
			if(journey == null) {
				Journey journeyDb = findJourney(journeyId)
				allRelatedJourneysAsMap.put(journeyId, journeyDb);
			}
			journeyId = journeyPair.recieverJourneyId
			ids << journeyId;
			journey = allRelatedJourneysAsMap.get(journeyId);
			if(journey == null) {
				Journey journeyDb = findJourney(journeyId)
				allRelatedJourneysAsMap.put(journeyId, journeyDb);
			}
			journeyPairs << journeyPair
		}
		UserJourney userJourney = new UserJourney(currentJourney, journeyPairs, allRelatedJourneysAsMap, ids);
		return userJourney;
	}
	
	def getJourneyForReview(String journeyId) {
		Journey currentJourney = findJourney(journeyId)//awsService.dynamoDBMapper.load(journeyId);
		Set<String> journeyPairIds = currentJourney.getJourneyPairIds();
		journeyPairIds.each { pairId ->
			JourneyPair journeyPair = journeyPairDataService.findPairById(pairId);
			if(journeyPair.getInitiatorStatusAsEnum() == WorkflowStatus.ACCEPTED) {
				String otherJourneyId = journeyPair.initiatorJourneyId;
				if(journeyId.equals(otherJourneyId)) {
					otherJourneyId = journeyPair.recieverJourneyId
				}
				Journey journeyOther = findJourney(otherJourneyId);
				journeyOther.setMyPairId(pairId)
				currentJourney.getRelatedJourneys().add(journeyOther);
			}
		}
		return currentJourney
	}
	
	/**
	 * Find Journey from Dynamo DB
	 */
	def findJourney(String journeyId) {
		Journey currentJourney = awsService.dynamoDBMapper.load(Journey.class, journeyId);
		return currentJourney;
	}
	
	def findJourneyFromElasticSearch(String journeyId, boolean isDummy = false) {
		Journey currentJourney = searchService.getJourney(journeyId, isDummy)
		return currentJourney;
	}
	
	/**
	 * Find Journey from Dynamo DB
	 */
	def findAllJourneyForUser(String mobile) {
		log.info("findAllJourneyForUser - mobile : ${mobile}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withIndexName("Mobile-DateOfJourney-index").withConsistentRead(false);
		List<Journey> journeys = awsService.dynamoDBMapper.query(Journey.class, queryExpression);
		return journeys;
	}
	
	/**
	 * Find Journey from Dynamo DB
	 */
	def findJourney(String mobile, Date dateOfJourney) {
		String currentTimeStr = GenericUtil.javaDateToDynamoDbDateString(dateOfJourney);
		log.info("findJourney - mobile : ${mobile}, currentTimeStr : ${currentTimeStr}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		journeyKey.dateOfJourney = dateOfJourney;
		Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(currentTimeStr.toString()));
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withRangeKeyCondition("DateOfJourney", rangeKeyCondition);
		List<Journey> journeys = awsService.dynamoDBMapper.query(Journey.class, queryExpression);
		return journeys;
	}
	
	/**
	 * Find Journey from Dynamo DB
	 */
	def saveJourney(Journey currentJourney) {
		if(!currentJourney.photoUrl){
			log.warn "Photo URL not present in journey. Going to DB to fetch it. Journey is: ${currentJourney}"
			User user = User.findByUsername(currentJourney.getUser())
			if(user) {
				currentJourney.photoUrl = user.getPhotoUrl()
			}
		}
		awsService.dynamoDBMapper.save(currentJourney, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE));
	}
	
	def updateElasticsearchForPassangeCountIfRequired(String journeyId, int numberOfCopassengers) {
		Journey journey = searchService.getJourney(journeyId);
		if(journey != null && journey.numberOfCopassengers != numberOfCopassengers) {
			searchService.updateJourneyForCopassangers(journeyId, numberOfCopassengers);
		}
	}
	
	/**
	 * Current Journey is from Elastic Search
	 */
	def findCurrentJourney(String mobile, DateTime current) {
		DateTime startDate = null
		DateTime endDate = null
		Integer rangeFactor = Integer.valueOf(grailsApplication.config.grails.approx.time.range);
		startDate = current.minusMinutes(rangeFactor);
		endDate = current.plusMinutes(rangeFactor);
		def journeys = searchService.findAllJourneysForUserBetweenDates(mobile, startDate, endDate);
		def currentJourney = null
		if(journeys.size() > 0) currentJourney = journeys[0]
		return currentJourney;
	}
	
	/**
	 * My Journey from DynamoDb
	 */
	def findMyJourneys(String mobile, Date currentTime) {
		//Map<Journey, List<Journey>> returnMap = new HashMap<Journey, List<Journey>>();
		String currentTimeStr = GenericUtil.javaDateToDynamoDbDateString(currentTime);
		log.info("mobile : ${mobile}, currentTimeStr : ${currentTimeStr}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT.toString()).withAttributeValueList(new AttributeValue().withS(currentTimeStr.toString()));
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withRangeKeyCondition("DateOfJourney", rangeKeyCondition).withIndexName("Mobile-DateOfJourney-index").withConsistentRead(false);
		List<Journey> journeys = awsService.dynamoDBMapper.query(Journey.class, queryExpression);
		def result =[]
		for(Journey journey:journeys){
			result << journey
		}
		enrichMyJourneys(result)
		return result;
//		def returnJourneys = [];
//		journeys.each { indexJourney ->
//			Journey enrichedJourney = enrichJourney(indexJourney);
//			if(enrichedJourney != null) {
//				returnJourneys << enrichedJourney
//			}
//			else {
//				log.error("Invalid Journey : ${indexJourney}")
//			}
//		}
//		return returnJourneys;
	}
	
	private void enrichMyJourneys(List myJourneys) {
		for(Journey myJourney : myJourneys) {
			Journey expandedJourney = this.findChildJourneys(myJourney.getId())
			for (JourneyPair pair : expandedJourney.getJourneyPairs()) {
				if(pair.getInitiatorJourneyId().equals(myJourney.getId())) {
					myJourney.addToOutgoingPair(pair.getId())
				}
				else {
					myJourney.addToIncomingPair(pair.getId())
				}
			}
		}
	}
	
	def findChildJourneys(String journeyId , boolean includeAllPairs = true) {
		Journey journey = findJourney(journeyId);
		if(!journey) {
			return null
		}
		Set<String> pairIds = journey.journeyPairIds
		boolean isJourneyAccepted = false
		pairIds.each { id ->
			JourneyPair journeyPair = journeyPairDataService.findPairById(id);
			if(journeyPair != null) {
				if(shouldIncludeJourneyPair(journeyPair, includeAllPairs)){
					journey.getJourneyPairs().add(journeyPair);
					Journey otherJourney = null;
					if(journey.id == journeyPair.initiatorJourneyId) {
						String otherJourneyId = journeyPair.recieverJourneyId;
						otherJourney = findJourney(otherJourneyId);
						if(otherJourney != null) {
							journeyPair.initiatorJourney = journey;
							journeyPair.recieverJourney = otherJourney;
							if(journeyPair.isPairAccepted()) {
								isJourneyAccepted = true
							}
							otherJourney.setMyStatus(journeyPair.getInitiatorStatus());
							otherJourney.setMyDirection(journeyPair.getInitiatorDirection());
							otherJourney.setMyPairId(journeyPair.getId());
							otherJourney.setMyActions(journeyPair.getInitiatorStatusAsEnum().getActions());
							
							journey.getRelatedJourneys().add(otherJourney);
						}
						else {
							log.error("Invalid state for journey : ${journey}. \nOther Journey id not valid ${otherJourneyId}");
						}
					}
					else if(journey.id == journeyPair.recieverJourneyId) {
						String otherJourneyId = journeyPair.initiatorJourneyId;
						otherJourney = findJourney(otherJourneyId);
						if(otherJourney != null) {
							journeyPair.initiatorJourney = otherJourney;
							journeyPair.recieverJourney = journey;
							if(journeyPair.isPairAccepted()) {
								isJourneyAccepted = true
							}
							otherJourney.setMyStatus(journeyPair.getRecieverStatus());
							otherJourney.setMyDirection(journeyPair.getRecieverDirection());
							otherJourney.setMyPairId(journeyPair.getId());
							otherJourney.setMyActions(journeyPair.getRecieverStatusAsEnum().getActions());
							
							journey.getRelatedJourneys().add(otherJourney);
						}
						else {
							log.error("Invalid state for journey : ${journey}. \nOther Journey id not valid ${otherJourneyId}");
						}
					}
					else {
						log.error("Invalid state for journey : ${journey}. \nNone of the ids in pair are related to current journey");
					}
				}
				else {
					log.info("Not including journey pair in the result. Jounrey pair is ${journeyPair}")
				}
				
			}
			else {
				log.error("Invalid state for journey : ${journey}. \n JourneyPair provided for id does not exists ${id}");
			}
		}
		if(isJourneyAccepted) {
			journey.setHasAcceptedRequest(true)
		}
		return journey;
	}
	
	private boolean shouldIncludeJourneyPair(JourneyPair pair, boolean includeAllPairs){
		boolean shouldInclude = true
		if(includeAllPairs){
			return true
		}
		else {
			if(WorkflowStatus.canBeIgnored(pair.getInitiatorStatus())) {
				shouldInclude = false
			}
			else {
				shouldInclude = true
			}
			
		}
		
		return shouldInclude
	}
	
	def findSiblingJourneys(String journeyId) {
		Journey journey = this.findChildJourneys(journeyId, false)
		def siblingJourneys = journey.getRelatedJourneys();
		siblingJourneys.add(journey);
		return new ArrayList<?>(siblingJourneys);
	}
	
	/**
	 * My History from Dynamo DB
	 */
	def findMyHistory(String mobile, Date currentTime) {
		String currentTimeStr = GenericUtil.javaDateToDynamoDbDateString(currentTime);
		log.info("mobile : ${mobile}, currentTimeStr : ${currentTimeStr}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		Condition rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.LT.toString()).withAttributeValueList(new AttributeValue().withS(currentTimeStr.toString()));
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withRangeKeyCondition("DateOfJourney", rangeKeyCondition).withIndexName("Mobile-DateOfJourney-index").withLimit(15).withConsistentRead(false);
		List<Journey> journeys = awsService.dynamoDBMapper.query(Journey.class, queryExpression);
		def returnJourneys = [];
		for(Journey journey:journeys){
			returnJourneys << journey
		}
		/*journeys.each { indexJourney ->
			Journey enrichedJourney = enrichJourney(indexJourney);
			if(enrichedJourney != null) {
				returnJourneys << enrichedJourney
			}
			else {
				log.error("Invalid Journey : ${indexJourney}")
			}
		}*/
		return returnJourneys;
	}
	
	
	/**
	 * Used in TestDataService only
	 */
	def deleteJourneyForUser(String mobile) {
		log.info("deleteJourneyForUser Starting deleting all journey for user with mobile : ${mobile}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withIndexName("Mobile-DateOfJourney-index").withConsistentRead(false);
		List<Journey> journeys = awsService.dynamoDBMapper.query(Journey.class, queryExpression);
		def journeyPairs = []
		for (Journey dbJourney: journeys) {
			Set pairIds = dbJourney.getJourneyPairIds()
			pairIds.each {it ->
				def pair = journeyPairDataService.findPairById(it)
				if(pair != null) journeyPairs << pair
			}
		}
		if(journeyPairs){
			awsService.dynamoDBMapper.batchDelete(journeyPairs);
		}
		awsService.dynamoDBMapper.batchDelete(journeys);
	}
	
	def makeJourneyNonSearchable(String journeyId, boolean isDummy = false){
		if(isDummy){
			searchService.deleteDummyJourney(journeyId)
		}
		else {
			searchService.deleteJourney(journeyId)
		}
	}
	
	def makeJourneySearchable (Journey journey){
		searchService.indexJourney(journey, journey.getId())
	}
	
	def makeJourneySearchable (String journeyId){
		Journey journey = findJourney(journeyId)
		if(journey) {
			searchService.indexJourney(journey, journey.getId())
		}
	}
	
	def deleteAllDataForUser(String mobile) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Preparing User data for mobile : " + mobile);
		List<Journey> journeys = findAllJourneyForUser(mobile);
		Set<Journey> journeysToBeDeleted = new HashSet<Journey>();
		Set<JourneyPair> journeyPairsToBeDeleted = new HashSet<JourneyPair>();
		journeys.each { journey ->
			journeysToBeDeleted.add(journey);
			String [] pairs = journey.journeyPairIds
			pairs.each { pairId ->
				JourneyPair journeyPair = journeyPairDataService.findPairById(pairId);
				journeyPairsToBeDeleted.add(journeyPair);
				journeysToBeDeleted.add(findJourney(journeyPair.initiatorJourneyId));
				journeysToBeDeleted.add(findJourney(journeyPair.recieverJourneyId));
				buffer.append("Delete PairId : " + pairId + ", journeyPair.initiatorJourneyId : " + journeyPair.initiatorJourneyId  + ", journeyPair.recieverJourneyId : " + journeyPair.recieverJourneyId + "</br>");
			}
		}
		journeysToBeDeleted.each { journey ->
			if(journey.isDummy) {
				searchService.deleteJourney(journey.id);
				buffer.append("Delete Dummy Journey in elasticsearch : " + journey.id + "</br>");
			}
			else {
				searchService.deleteJourney(journey.id);
				buffer.append("Delete Journey in elasticsearch : " + journey.id + "</br>");
			}
		}
		def tmpPairs = new ArrayList<JourneyPair>();
		tmpPairs.addAll(journeyPairsToBeDeleted);
		def tmpJourneys = new ArrayList<Journey>();
		tmpJourneys.addAll(journeysToBeDeleted);
		awsService.dynamoDBMapper.batchDelete(tmpPairs);
		awsService.dynamoDBMapper.batchDelete(tmpJourneys);
		return buffer.toString();
	}
	
	def findAllJourneysForADate(Date inputDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date dateKey = null;
		try {
			dateKey = dateFormat.parse(dateFormat.format(inputDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Journey journeyKey = new Journey();
		journeyKey.setDateKey(dateKey);
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withIndexName("DateKey-index").withConsistentRead(false);
		List<Journey> journeys = awsService.dynamoDBMapper.query(Journey.class, queryExpression);
		return journeys;
	}
	
	def List<WayPoint> getRouteWayPoint(Journey currentJourney, String matchedJourneyId){
		currentJourney.setName("You")
		Route route = null
		List matchedJourenys = this.findJourneyForRouteDetail(matchedJourneyId)
		if(matchedJourenys?.size()==1) {
			route = new Route(currentJourney, matchedJourenys.get(0))
		}
		else if(matchedJourenys?.size()==2){
			route = new Route(currentJourney, matchedJourenys.get(0), matchedJourenys.get(1))
		}
		if(route){
			return route.getWayPoints()
		}
		return null
		
	}
	
	
	
	def List<WayPoint> getRouteWayPointForMyJourney(String journeyId){
		Journey currentJourney = this.findJourney(journeyId)
		currentJourney.setName("You")
		Route route = null
		List matchedJourenys = this.findJourneyForRouteDetail(journeyId)
		if(matchedJourenys?.size() == 1) {
			route = new Route(matchedJourenys.get(0))
		}
		else if(matchedJourenys?.size()==2){
			route = new Route(matchedJourenys.get(0), matchedJourenys.get(1))
		}
		else if(matchedJourenys?.size()==3){
			route = new Route(matchedJourenys.get(0), matchedJourenys.get(1), matchedJourenys.get(2))
		}
		if(route){
			return route.getWayPoints()
		}
		return null
		
	}
	
	private List findJourneyForRouteDetail(String journeyId) {
		Journey journey = this.findJourney(journeyId)
		if(journey) {
			def myList = [journey]
			return myList
			//return this.findSiblingJourneys(journeyId)
		}
		else {
			journey = this.findJourneyFromElasticSearch(journeyId, true)
			def returnList = [journey]
			return returnList
		}
		return null
	}
		
	private List findMyJourneyBetweenDates(String mobile, Date startTime, Date endTime) {
		String startTimeStr = GenericUtil.javaDateToDynamoDbDateString(startTime);
		String endTimeStr = GenericUtil.javaDateToDynamoDbDateString(endTime);
		log.info("findMyJourneyBetweenDates: mobile : ${mobile}, startTime : ${startTimeStr}, endTime :${endTimeStr}");
		Journey journeyKey = new Journey();
		journeyKey.mobile = mobile;
		Condition bwtweenDateRangeKeyCondition =new Condition().withComparisonOperator(ComparisonOperator.BETWEEN).withAttributeValueList(new AttributeValue().withS(startTimeStr.toString()), new AttributeValue().withS(endTimeStr.toString()))
		DynamoDBQueryExpression<Journey> queryExpression = new DynamoDBQueryExpression<Journey>().withHashKeyValues(journeyKey).withRangeKeyCondition("DateOfJourney", bwtweenDateRangeKeyCondition).withIndexName("Mobile-DateOfJourney-index").withConsistentRead(false);
		List<Journey> journeys = awsService.dynamoDBMapper.query(Journey.class, queryExpression);
		def returnJourneys = [];
		for(Journey journey:journeys){
			returnJourneys << journey
		}
		return returnJourneys;
	}
	
	def Journey findAcceptedRequestForAJourney(String myJourneyId) {
		Journey myJourney = findJourney(myJourneyId)
		Journey otherJourney = null
		for (String pairId : myJourney.getJourneyPairIds()) {
			JourneyPair pair  = journeyPairDataService.findPairById(pairId)
			if(pair?.getInitiatorStatus()?.equals(WorkflowStatus.ACCEPTED.getStatus())) {
				String otherJourneyId = pair.getInitiatorJourneyId().equals(myJourney.getId()) ? pair.getRecieverJourneyId() : pair.getInitiatorJourneyId()
				otherJourney = findJourney(otherJourneyId)
				break;
			}
		}
		return otherJourney
	}
	
	def List findAllRequstedJourneysForAJourney(String myJourneyId) {
		Journey myJourney = findJourney(myJourneyId)
		List result = []
		for (String pairId : myJourney.getJourneyPairIds()) {
			JourneyPair pair  = journeyPairDataService.findPairById(pairId)
			if(pair?.getInitiatorStatus()?.equals(WorkflowStatus.REQUESTED.getStatus()) && pair?.getInitiatorJourneyId()?.equals(myJourneyId)) {
				String otherJourneyId =pair.getRecieverJourneyId()
				Journey otherJourney = findJourney(otherJourneyId)
				if(otherJourney) {
					result << otherJourney
				}
			}
		}
		
	}
	
	def reloadESDataFromDynamoDB(final DateTime journeyDateFrom) {
		DateTime journeyDate = new DateTime(journeyDateFrom)
		Integer daysForward = Integer.valueOf(grailsApplication.config.grails.max.days.to.search)
		DateTime journeyDateUpto = journeyDateFrom.plusDays(daysForward)
		while (journeyDate.compareTo(journeyDateUpto) <= 0) {
			log.info "Trying to load data for date: ${journeyDate}"
			List journeys = findAllJourneysForADate(journeyDateFrom.toDate())
			for(Journey journey : journeys) {
				searchService.indexJourney(journey, journey.getId());
			}
			log.info "Data loaded for date: ${journeyDate}"
			journeyDate = journeyDate.plusDays(1)
		}
	}
	
	def List getJourneyReport(Date inputDate) {
		Map returnMap = [:]
		Set pairIds = [] as Set
		List acceptedJourneys = []
		List nonAcceptedJourneys = []
		List journeys = this.findAllJourneysForADate(inputDate)
		for(Journey journey : journeys) {
			if(journey.getJourneyPairIds()) {
				pairIds.addAll(journey.getJourneyPairIds())
			}
		}
		List journeyPairs = journeyPairDataService.findPairsByIds(pairIds)
		for (JourneyPair pair : journeyPairs) {
			pair.setInitiatorJourney(this.findJourney(pair.getInitiatorJourneyId()))
			pair.setRecieverJourney(this.findJourney(pair.getRecieverJourneyId()))
		}
		return journeyPairs
	}
	
}
