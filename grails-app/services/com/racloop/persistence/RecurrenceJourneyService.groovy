package com.racloop.persistence

import grails.transaction.Transactional

import org.elasticsearch.common.joda.time.DateTime

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.racloop.RecurrenceJourneyIdResolver
import com.racloop.domain.Journey
import com.racloop.domain.RecurrenceJourney

@Transactional
class RecurrenceJourneyService {
	
	def awsService
	def journeyDataService
	
	def createNewRecurringJourney(String journeyId, String[] recurringDays) {
		Journey journey = journeyDataService.findJourney(journeyId)
		if(journey) {
			journey.setJourneyRecurrence(recurringDays as Set)
			for (String dayOfWeek : recurringDays ) {
				RecurrenceJourney recurrenceJourney = new RecurrenceJourney()
				recurrenceJourney.setId(RecurrenceJourneyIdResolver.generateIdFromDate(journey.getDateOfJourney(), dayOfWeek))
				recurrenceJourney.addParentJourney(journeyId)
				this.createRecurringJourney(recurrenceJourney)
			}
			
			journeyDataService.saveJourney(journey)
			
		}
	}
	
	def activateJourneys() {
		DateTime startDateTime = RecurrenceJourneyIdResolver.getFloorDateTimeFromCurrentDate()
		DateTime endDateTime = startDateTime.plusHours(18) 	//Read this from config
		List idsToFetch = getRecurringIdsToFetch(startDateTime, endDateTime) 
		List recurrenceJourneys = this.findRecurrenceJourneysByIds(idsToFetch as Set)
		createActualJourneys(recurrenceJourneys)
	}
	
	def createRecurringJourney(RecurrenceJourney recurrenceJourney){
		RecurrenceJourney savedrecurrenceJourney = awsService.dynamoDBMapper.save(recurrenceJourney);
		
	}
	
	def updateRecurringJourney(RecurrenceJourney recurrenceJourney) {
		awsService.dynamoDBMapper.save(recurrenceJourney, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE));
	}
	
	def findRecurrenceJourneyById(String id) {
		RecurrenceJourney recurrenceJourney = awsService.dynamoDBMapper.load(RecurrenceJourney.class, id);
		return recurrenceJourney;
	}
	
	def List findRecurrenceJourneysByIds(Set ids){
		def recurrenceJourneys = []
		ids.each{it ->
			RecurrenceJourney recurrenceJourney = findRecurrenceJourneyById(it)
			if(recurrenceJourney){
				recurrenceJourneys << recurrenceJourney
			}
			
		}
		return recurrenceJourneys
	}
	
	private createActualJourneys(List recurrenceJourneys) {
		for(RecurrenceJourney recurrenceJourney: recurrenceJourneys) {
			Set parentJourneyIds = recurrenceJourney.getParentJourenyIds()
			String id = recurrenceJourney.getId()
			DateTime dateTimeFromId = RecurrenceJourneyIdResolver.resolveDateFromId(id)
			for(String journeyId : parentJourneyIds) {
				Journey parentJourney = journeyDataService.findJourney(journeyId)
				if(parentJourney && (parentJourney.getDateOfJourney().compareTo(dateTimeFromId.toDate()) < 0)) {
					if(!validateIfJourneyExists(parentJourney, dateTimeFromId.minusMinutes(30), dateTimeFromId.plusMinutes(30))) {
						Journey newJourney = parentJourney.resetMe()
						newJourney.setDateOfJourney(dateTimeFromId.toDate())
						newJourney.setParentRecurringJourneyId(journeyId)
						newJourney.setCreatedDate(new Date())
						journeyDataService.createJourney(newJourney)
					}
				}
			}
		}
	}
	
	private boolean validateIfJourneyExists(Journey journey, DateTime startTime, DateTime endTime) {
		List journeys = journeyDataService.findMyJourneyBetweenDates(journey.getMobile(), startTime.toDate(), endTime.toDate())
		if(journeys) {
			return true
		}
		return false
	}
	
	private List<Integer> getRecurringIdsToFetch(DateTime start, DateTime end) {
		List<Integer> ids = new ArrayList<Integer>();
		DateTime temp = start;
		while (temp.compareTo(end) <= 0) {
			ids << RecurrenceJourneyIdResolver.generateIdFromDate(temp.toDate())
			temp = temp.plusMinutes(15)
		}
		
		return ids
		
	}
	
	def deleteAllRecurrence() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		PaginatedScanList<RecurrenceJourney> result = awsService.dynamoDBMapper.scan(RecurrenceJourney.class,  scanExpression);
		for (RecurrenceJourney data : result) {
			awsService.dynamoDBMapper.delete(data);
		}
	}
	
	def Set getAllRecurringJourneyForAUser(String userMobile){
		Date currentDate = new Date();
		Set result =[] as Set
		List futureJourneys = journeyDataService.findMyJourneys(userMobile, currentDate)
		for(Journey journey : futureJourneys) {
			if(journey.parentRecurringJourney()) {
				result<<journey
			}
		}
		List pastJourneys = journeyDataService.findMyHistory(userMobile, currentDate)
		for(Journey journey : pastJourneys) {
			if(journey.parentRecurringJourney()) {
				result<<journey
			}
		}
		
		return result
				
	} 
	
	def deleteRecurringJourney(String journeyId) {
		Journey journey = journeyDataService.findJourney(journeyId)
		if(journey) {
			for (String dayOfWeek : journey.getJourneyRecurrence() ) {
				RecurrenceJourney recurrenceJourney = this.findRecurrenceJourneyById(RecurrenceJourneyIdResolver.generateIdFromDate(journey.getDateOfJourney(), dayOfWeek))
				recurrenceJourney.removeParentJourney(journeyId)
				if(!recurrenceJourney.getParentJourenyIds()) {
					awsService.dynamoDBMapper.delete(recurrenceJourney);
				}
				else {
					this.updateRecurringJourney(recurrenceJourney)
				}
			}
			journey.setJourneyRecurrence(null)
			journey.setParentRecurringJourneyId(null)
			journeyDataService.saveJourney(journey)
		}
	}

	
}
