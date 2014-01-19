package com.racloop

import grails.util.Environment

import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.common.joda.time.DateTime

class JourneyService {

    def elasticSearchService
	def jmsService
	
	def saveJourney(Journey journey) {
		if(journey.validate()) {
			if(journey.save(flush: true)) {
				return true;
			}
			else {
				return false;
			}
		}
		return false
	}
	
	/**
	 * @deprecated
	 */
	def saveJourney(User user, Journey journey) {
		journey.user = user
		//user.addToActiveJourneys(journey);
		if(journey.validate()) {
			DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
			DateTime validStartTime = new DateTime(journey.validStartTime)
			journey.save();
			return true;
		}
		else {
			log.error "Erorr created journey - ${journey}"
			journey.errors.each {
				log.error "Error in domain : ${it}"
			}
			return false;
		}
    }
	
	/**
	 * @deprecated
	 */
	def createJourney(User user, JourneyRequestCommand command) {
		Journey journey = createJourneyInstance(command)
		journey.user = user
		user.addToActiveJourneys(journey);
		if(journey.validate()) {
			DateTime dateOfJourney = new DateTime(journey.dateOfJourney)
			DateTime validStartTime = new DateTime(journey.validStartTime)
			journey.save(flush : true);
			return true;
		}
		else {
			log.error "Erorr created journey - ${journey}"
			journey.errors.each {
				log.error "Error in domain : ${it}"
			}
			return false;
		}
    }
	
	def search(User user, JourneyRequestCommand journey) {
		def journeys = elasticSearchService.search(user, journey);
		return journeys;
	}
	
	def getDummyData(JourneyRequestCommand command) {
		DateTime tempDate = new DateTime(command.dateOfJourney);
		if(Environment.current.getName() == "production") {
			if(tempDate.getHourOfDay() > 20 || tempDate.getHourOfDay() < 7) return [];
		}
		def journeys = elasticSearchService.searchGeneratedData(command);
		if(journeys.size == 0) {
			Random randomGenerator = new Random();
			Integer numberOfRecords = randomGenerator.nextInt(5);
			if(numberOfRecords < 2) numberOfRecords = 2;//expecting 2,3,4 number of records
			journeys = [];
			def names = [];
			if(command.isDriver) {
				names = NamesUtil.getRandomNames(numberOfRecords);
			}
			else {
				names = NamesUtil.getRandomBoyNames(numberOfRecords);
			}
			def maxDistance = command.tripDistance / (ElasticSearchService.DISTANCE_FACTOR + 1)
			
			Place [] fromPlaces = elasticSearchService.searchNearLocations(maxDistance, command.fromLatitude, command.fromLongitude, numberOfRecords)
			Place [] toPlaces = elasticSearchService.searchNearLocations(maxDistance, command.toLatitude, command.toLongitude, numberOfRecords)
			Random randomMinutesGenerator = new Random();
			int index = 0;
			if(fromPlaces.size() <= toPlaces.size()) {
				fromPlaces.each {
					int fiveMinuteIntervalIndex = randomMinutesGenerator.nextInt(12);
					int randomMinutes = 15;
					if(fiveMinuteIntervalIndex > 3) randomMinutes = fiveMinuteIntervalIndex * 5;
					tempDate = tempDate.plusMinutes(randomMinutes);
					def name = names[index]
					def fromPlace = it;
					def toPlace = toPlaces[index]
					JourneyRequestCommand journey = new JourneyRequestCommand()
					journey.name = name;
					journey.dateOfJourney = tempDate.toDate()
					journey.fromLatitude = fromPlace.location.lat();
					journey.fromLongitude = fromPlace.location.lon();
					journey.fromPlace = fromPlace.name
					journey.toLatitude = toPlace.location.lat();
					journey.toLongitude = toPlace.location.lat();
					journey.toPlace = toPlace.name
					elasticSearchService.indexGeneratedJourney(journey);
					journeys << journey
					index++
				}
			}
			else {
				toPlaces.each {
					int fiveMinuteIntervalIndex = randomMinutesGenerator.nextInt(12);
					int randomMinutes = 15;
					if(fiveMinuteIntervalIndex > 3) randomMinutes = fiveMinuteIntervalIndex * 5;
					tempDate = tempDate.plusMinutes(randomMinutes);
					def name = names[index]
					def toPlace = it;
					def fromPlace = toPlaces[index]
					JourneyRequestCommand journey = new JourneyRequestCommand()
					journey.name = name;
					journey.dateOfJourney = tempDate.toDate()
					journey.fromLatitude = fromPlace.location.lat();
					journey.fromLongitude = fromPlace.location.lon();
					journey.fromPlace = fromPlace.name
					journey.toLatitude = toPlace.location.lat();
					journey.toLongitude = toPlace.location.lat();
					journey.toPlace = toPlace.name
					elasticSearchService.indexGeneratedJourney(journey);
					journeys << journey
					index++
				}				
			}
		}
		return journeys;
	}
}
