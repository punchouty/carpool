package com.racloop

import java.util.Random;

import org.elasticsearch.common.geo.GeoPoint;
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
		if(tempDate.getHourOfDay() > 20 || tempDate.getHourOfDay() < 7) return [];
		def journeys = elasticSearchService.searchDummyData(command);
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
			GeoPoint from = new GeoPoint(command.fromLatitude, command.fromLongitude)
			GeoPoint to = new GeoPoint(command.toLatitude, command.toLongitude)
			Random randomMinutesGenerator = new Random();
			names.each {
				int randomMinutes = randomMinutesGenerator.nextInt(60);
				if(randomMinutes < 15) randomMinutes = 15;
				tempDate = tempDate.plusMinutes(randomMinutes);
				JourneyRequestCommand journey = new JourneyRequestCommand()
				journey.name = it;
				journey.dateOfJourney = tempDate.toDate()
				GeoPoint fromRandom = DummyDataUtil.random(from, maxDistance);
				journey.fromLatitude = fromRandom.lat();
				journey.fromLongitude = fromRandom.lon();
				GeoPoint toRandom = DummyDataUtil.random(to, maxDistance);
				journey.toLatitude = toRandom.lat();
				journey.toLongitude = toRandom.lon();
				elasticSearchService.indexDummyJourney(journey);
				journeys << journey
			}
		}
		return journeys;
	}
	
	/**
	 * @deprecated
	 */
	def makeSearchable(Journey journey) {
		elasticSearchService.indexJourney(journey);
		jmsService.send(queue:'msg.history', journey)
	}
	
	/**
	 * @deprecated
	 */
	private Journey createJourneyInstance(JourneyRequestCommand command) {
		Journey journey = new Journey()
		journey.dateOfJourney = command.dateOfJourney
		journey.isDriver = command.isDriver
		return journey;
	}
}
