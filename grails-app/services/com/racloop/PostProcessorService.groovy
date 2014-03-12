package com.racloop

import org.elasticsearch.common.geo.GeoPoint;

import grails.plugin.jms.Queue;

class PostProcessorService {
	
	static exposes = ['jms']
	def elasticSearchService
	
	@Queue(name= "msg.history.queue") //also defined in Constant.java
    def processHistory(def journeyId) {
		log.info "Recieved message with journeyId ${journeyId}"
		/*
		try {
			Journey journeyFromDb = Journey.get(journeyId);
			User user = journeyFromDb.user
			String stringId = journeyFromDb.id + "";
			JourneyRequestCommand journey = elasticSearchService.getJourney(stringId, journeyFromDb.isDriver, journeyFromDb.dateOfJourney);
			GeoPoint from = new GeoPoint(journey.fromLatitude, journey.fromLongitude);
			GeoPoint to = new GeoPoint(journey.toLatitude, journey.toLongitude);
			//processing from
			TravelHistory history = TravelHistory.findByUserAndGeoHash(user, from.geohash);
			if(history) {
				history.searchCount = history.searchCount + 1 
				history.lastUpdatedAt = new Date();
				history.save();
			}
			else {
				TravelHistory newHistory = new TravelHistory();
				newHistory.user = user;
				newHistory.place = journey.fromPlace;
				newHistory.geoHash = from.geohash;
				newHistory.latitude = journey.fromLatitude;
				newHistory.longitude = journey.fromLongitude;
				newHistory.save();
			}
			//processing to
			history = TravelHistory.findByUserAndGeoHash(user, to.geohash);
			if(history) {
				history.searchCount = history.searchCount + 1 
				history.lastUpdatedAt = new Date();
				history.save();
			}
			else {
				TravelHistory newHistory = new TravelHistory();
				newHistory.user = user;
				newHistory.place = journey.toPlace;
				newHistory.geoHash = to.geohash;
				newHistory.latitude = journey.toLatitude;
				newHistory.longitude = journey.toLongitude;
				newHistory.save();
			}
		}
		catch(Exception e) {
			log.error ("Problem for journeyId : ${journeyId} and exception is " , e);
		}
		log.info "message with journeyId ${journeyId} processed successfully"
		*/
		// explicitly return null to prevent unwanted replyTo queue attempt
		return null
    }
}
