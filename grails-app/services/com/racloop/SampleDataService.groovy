package com.racloop

import grails.util.Environment
import liquibase.util.csv.opencsv.CSVReader

class SampleDataService {
	
	def grailsApplication
	def elasticSearchService
	def journeyManagerService
	
	def deleteSampleData() {
		elasticSearchService.deleteSampleData();
		elasticSearchService.deleteWorkflowData()
	}

    def populateSampleData() {
		
		int timeInterval = 10
		User user1 = User.findByUsername('admin@racloop.com');
		User user2 = User.findByUsername('sample.user@racloop.com');
		User user3 = User.findByUsername('sample.driver@racloop.com');
		User user4 = User.findByUsername('sample.rider@racloop.com');
		String dateFormat = grailsApplication.config.grails.dateFormat
		CSVReader reader = new CSVReader(new InputStreamReader(this.class.classLoader.getResourceAsStream(grailsApplication.config.grails.startup.sampleData.file)));
		List lines = reader.readAll();
		
		int i = 0;
		Calendar time = Calendar.getInstance();
		time.add(Calendar.MINUTE, 30);
		//Delhi to Chandigarh - sample.driver
		lines.each {  line ->
			i++;
			JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
			journeyCommand.dateOfJourney = new Date(time.timeInMillis)
			journeyCommand.isMale = false
			journeyCommand.fromPlace = line[6]
			journeyCommand.fromLatitude = Double.parseDouble(line[4])
			journeyCommand.fromLongitude = Double.parseDouble(line[5])
			journeyCommand.toPlace = line[11]
			journeyCommand.toLatitude = Double.parseDouble(line[9])
			journeyCommand.toLongitude = Double.parseDouble(line[10])
			journeyCommand.isDriver = true
			journeyCommand.tripDistance = 250.0d
			journeyManagerService.createJourney(user3, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		time = Calendar.getInstance();
		time.add(Calendar.MINUTE, 35);
		//Delhi to Chandigarh - sample.rider
		lines.each {  line ->
			i++;
			JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
			journeyCommand.dateOfJourney = new Date(time.timeInMillis)
			journeyCommand.isMale = false
			journeyCommand.fromPlace = line[6]
			journeyCommand.fromLatitude = Double.parseDouble(line[4])
			journeyCommand.fromLongitude = Double.parseDouble(line[5])
			journeyCommand.toPlace = line[11]
			journeyCommand.toLatitude = Double.parseDouble(line[9])
			journeyCommand.toLongitude = Double.parseDouble(line[10])
			journeyCommand.isDriver = false
			journeyCommand.tripDistance = 250.0d
			journeyManagerService.createJourney(user4, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
		}		
		i = 0;
		time = Calendar.getInstance();
		//Delhi to Chandigarh - admin
		time.add(Calendar.MINUTE, 40);
		lines.each {  line ->
			i++;
			JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
			journeyCommand.dateOfJourney = new Date(time.timeInMillis)
			journeyCommand.isMale = false
			journeyCommand.fromPlace = line[6]
			journeyCommand.fromLatitude = Double.parseDouble(line[4])
			journeyCommand.fromLongitude = Double.parseDouble(line[5])
			journeyCommand.toPlace = line[11]
			journeyCommand.toLatitude = Double.parseDouble(line[9])
			journeyCommand.toLongitude = Double.parseDouble(line[10])
			journeyCommand.isDriver = true
			journeyCommand.tripDistance = 250.0d
			journeyManagerService.createJourney(user1, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		time = Calendar.getInstance();
		time.add(Calendar.MINUTE, 45);
		//Delhi to Chandigarh - admin
		lines.each {  line ->
			i++;
			JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
			journeyCommand.dateOfJourney = new Date(time.timeInMillis)
			journeyCommand.isMale = false
			journeyCommand.fromPlace = line[6]
			journeyCommand.fromLatitude = Double.parseDouble(line[4])
			journeyCommand.fromLongitude = Double.parseDouble(line[5])
			journeyCommand.toPlace = line[11]
			journeyCommand.toLatitude = Double.parseDouble(line[9])
			journeyCommand.toLongitude = Double.parseDouble(line[10])
			journeyCommand.isDriver = false
			journeyCommand.tripDistance = 250.0d
			journeyManagerService.createJourney(user1, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
		}
	}
}
