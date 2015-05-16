package com.racloop.persistence

import liquibase.util.csv.opencsv.CSVReader;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.racloop.DistanceUtil;
import com.racloop.User;
import com.racloop.domain.Journey;

import grails.transaction.Transactional

@Transactional
class TestDataService {
	
	def grailsApplication;
	def journeyDataService;
	def searchService;
	
	def generateDataForDev() {
		def outgoingRequest =[]
		def incomingRequest = []
		
		int timeInterval = 15
		User user1 = User.findByUsername('admin@racloop.com');
		User user2 = User.findByUsername('user@racloop.com');
		User user3 = User.findByUsername('driver@racloop.com');
		User user4 = User.findByUsername('rider@racloop.com');
		String dateFormat = grailsApplication.config.grails.dateFormat
		CSVReader reader = new CSVReader(new InputStreamReader(this.class.classLoader.getResourceAsStream(grailsApplication.config.grails.startup.sampleData.file)));
		List lines = reader.readAll();
		
		int i = 0;
		Calendar time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.MINUTE, -60);
		//Delhi to Chandigarh - sample.driver
		User user = user1;
		lines.each {  line ->
			i++;
			if(i%4 == 0) {
				Journey journey = new Journey();
				journey.mobile = user.profile.mobile
				journey.dateOfJourney = new Date(time.timeInMillis)
				journey.email = user.profile.email
				journey.name = user.profile.fullName
				journey.from = line[6]
				journey.fromLatitude = Double.parseDouble(line[4])
				journey.fromLongitude = Double.parseDouble(line[5])
				journey.to = line[11]
				journey.toLatitude = Double.parseDouble(line[9])
				journey.toLongitude = Double.parseDouble(line[10])
				journey.isMale = true
				journey.isDriver = true
				journey.tripDistance = DistanceUtil.distance(journey.fromLatitude, journey.fromLongitude, journey.toLatitude, journey.toLongitude);
				journey.photoUrl = user.profile.getGravatarUri()
				journey.isDummy = true;
				journey.createdDate = new Date();
				journeyDataService.createJourney(journey);
				outgoingRequest<<journey
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.MINUTE, -90);
		user = user2;
		lines.each {  line ->
			i++;
			if(i%3 == 0) {
				Journey journey = new Journey();
				journey.mobile = user.profile.mobile
				journey.dateOfJourney = new Date(time.timeInMillis)
				journey.email = user.profile.email
				journey.name = user.profile.fullName
				journey.from = line[6]
				journey.fromLatitude = Double.parseDouble(line[4])
				journey.fromLongitude = Double.parseDouble(line[5])
				journey.to = line[11]
				journey.toLatitude = Double.parseDouble(line[9])
				journey.toLongitude = Double.parseDouble(line[10])
				journey.isMale = true
				journey.isDriver = true
				journey.tripDistance = DistanceUtil.distance(journey.fromLatitude, journey.fromLongitude, journey.toLatitude, journey.toLongitude);
				journey.photoUrl = user.profile.getGravatarUri()
				journey.isDummy = true;
				journey.createdDate = new Date();
				journeyDataService.createJourney(journey);
				outgoingRequest<<journey
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.MINUTE, -90);
		user = user3;
		lines.each {  line ->
			i++;
			if(i%2 == 0) {
				Journey journey = new Journey();
				journey.mobile = user.profile.mobile
				journey.dateOfJourney = new Date(time.timeInMillis)
				journey.email = user.profile.email
				journey.name = user.profile.fullName
				journey.from = line[6]
				journey.fromLatitude = Double.parseDouble(line[4])
				journey.fromLongitude = Double.parseDouble(line[5])
				journey.to = line[11]
				journey.toLatitude = Double.parseDouble(line[9])
				journey.toLongitude = Double.parseDouble(line[10])
				journey.isMale = true
				journey.isDriver = true
				journey.tripDistance = DistanceUtil.distance(journey.fromLatitude, journey.fromLongitude, journey.toLatitude, journey.toLongitude);
				journey.photoUrl = user.profile.getGravatarUri()
				journey.isDummy = true;
				journey.createdDate = new Date();
				journeyDataService.createJourney(journey);
				outgoingRequest<<journey
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.MINUTE, -90);
		user = user4;
		lines.each {  line ->
			i++;
			if(i%1 == 0) {
				Journey journey = new Journey();
				journey.mobile = user.profile.mobile
				journey.dateOfJourney = new Date(time.timeInMillis)
				journey.email = user.profile.email
				journey.name = user.profile.fullName
				journey.from = line[6]
				journey.fromLatitude = Double.parseDouble(line[4])
				journey.fromLongitude = Double.parseDouble(line[5])
				journey.to = line[11]
				journey.toLatitude = Double.parseDouble(line[9])
				journey.toLongitude = Double.parseDouble(line[10])
				journey.isMale = true
				journey.isDriver = true
				journey.tripDistance = DistanceUtil.distance(journey.fromLatitude, journey.fromLongitude, journey.toLatitude, journey.toLongitude);
				journey.photoUrl = user.profile.getGravatarUri()
				journey.isDummy = true;
				journey.createdDate = new Date();
				journeyDataService.createJourney(journey);
				outgoingRequest<<journey
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
	}
	
	def deleteDataForDev() {
		searchService.deleteAllJourneyData();
		User user1 = User.findByUsername('admin@racloop.com');
		journeyDataService.deleteJourneyForUser(user1.profile.mobile);
		User user2 = User.findByUsername('user@racloop.com');
		journeyDataService.deleteJourneyForUser(user2.profile.mobile);
		User user3 = User.findByUsername('driver@racloop.com');
		journeyDataService.deleteJourneyForUser(user3.profile.mobile);
		User user4 = User.findByUsername('rider@racloop.com');
		journeyDataService.deleteJourneyForUser(user4.profile.mobile);
		
		
	}
	
	def populateElasticSearch() {
		User user1 = User.findByUsername('admin@racloop.com');
		User user = user1;
		List<Journey> journeys = journeyDataService.findAllJourneyForUser(user.profile.mobile);
		for (Journey dbJourney: journeys) {
			Journey esJourney = searchService.getJourney(dbJourney.getEsJourneyId());
			if(esJourney == null) {
				log.info("In populateElasticSearch no data in ES ${dbJourney}")
				searchService.indexJourney(dbJourney, dbJourney.getEsJourneyId());
			}
			else {
				log.info("Found in ES ${dbJourney}")
			}
		}
		User user2 = User.findByUsername('user@racloop.com');
		user = user2;
		journeys = journeyDataService.findAllJourneyForUser(user.profile.mobile);
		for (Journey dbJourney: journeys) {
			Journey esJourney = searchService.getJourney(dbJourney.getEsJourneyId());
			if(esJourney == null) {
				log.info("In populateElasticSearch no data in ES ${dbJourney}")
				searchService.indexJourney(dbJourney, dbJourney.getEsJourneyId());
			}
			else {
				log.info("Found in ES ${dbJourney}")
			}
		}
		User user3 = User.findByUsername('driver@racloop.com');
		user = user3;
		journeys = journeyDataService.findAllJourneyForUser(user.profile.mobile);
		for (Journey dbJourney: journeys) {
			Journey esJourney = searchService.getJourney(dbJourney.getEsJourneyId());
			if(esJourney == null) {
				log.info("In populateElasticSearch no data in ES ${dbJourney}")
				searchService.indexJourney(dbJourney, dbJourney.getEsJourneyId());
			}
			else {
				log.info("Found in ES ${dbJourney}")
			}
		}
		User user4 = User.findByUsername('rider@racloop.com');
		user = user4;
		journeys = journeyDataService.findAllJourneyForUser(user.profile.mobile);
		for (Journey dbJourney: journeys) {
			Journey esJourney = searchService.getJourney(dbJourney.getEsJourneyId());
			if(esJourney == null) {
				log.info("In populateElasticSearch no data in ES ${dbJourney}")
				searchService.indexJourney(dbJourney, dbJourney.getEsJourneyId());
			}
			else {
				log.info("Found in ES ${dbJourney}")
			}
		}
	}
}
