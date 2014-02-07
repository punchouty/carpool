package com.racloop

import grails.util.Environment
import liquibase.util.csv.opencsv.CSVReader

class SampleDataController {
	
	def grailsApplication
	def elasticSearchService
	def journeyManagerService

    def index() { 
		render "Empty Implementation"
	}
	
	def delete() {		
		Environment.executeForCurrentEnvironment {
			development {
				elasticSearchService.deleteSampleData();
				render "Delete Complete"
			}
			test {
				render "Empty Implementation"
			}
			production {
				render "Empty Implementation"
			}
		}		
	}
	
	def populate() {	
		Environment.executeForCurrentEnvironment {
			development {
				populateData();
				render "Population Complete"
			}
			test {
				render "Empty Implementation"
			}
			production {
				render "Empty Implementation"
			}
		}	
	}
	
	private populateData() {
		log.info "Removing old sample data if it exists"
		elasticSearchService.deleteSampleData();
		log.info "Old sample data removed"
		int timeInterval = 15
		User rajan = User.findByUsername('rajan');
		log.info("Found user rajan : ${rajan.username}")
		User tamanna = User.findByUsername('tamanna');
		log.info("Found user tamanna : ${tamanna.username}")
		User khwaish = User.findByUsername('khwaish');
		log.info("Found user khwaish : ${khwaish.username}")
		String dateFormat = grailsApplication.config.grails.dateFormat
		CSVReader reader = new CSVReader(new InputStreamReader(this.class.classLoader.getResourceAsStream(grailsApplication.config.grails.startup.sampleData.file)));
		List lines = reader.readAll();
		
		int i = 0;
		Calendar time = Calendar.getInstance();
		
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
			journeyManagerService.createJourney(tamanna, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		time = Calendar.getInstance();
		time.add(Calendar.MINUTE, 10);
		lines.each {  line ->
			i++;
			JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
			journeyCommand.dateOfJourney = new Date(time.timeInMillis)
			journeyCommand.isMale = false
			journeyCommand.fromPlace = line[11]
			journeyCommand.fromLatitude = Double.parseDouble(line[9])
			journeyCommand.fromLongitude = Double.parseDouble(line[10])
			journeyCommand.toPlace = line[6]
			journeyCommand.toLatitude = Double.parseDouble(line[4])
			journeyCommand.toLongitude = Double.parseDouble(line[5])
			journeyCommand.isDriver = true
			journeyManagerService.createJourney(khwaish, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		
		
		i = 0;
		time = Calendar.getInstance();
		time.add(Calendar.MINUTE, 15);
		lines.each {  line ->
			i++;
			JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
			journeyCommand.dateOfJourney = new Date(time.timeInMillis)
			journeyCommand.isMale = true
			journeyCommand.fromPlace = line[11]
			journeyCommand.fromLatitude = Double.parseDouble(line[9])
			journeyCommand.fromLongitude = Double.parseDouble(line[10])
			journeyCommand.toPlace = line[6]
			journeyCommand.toLatitude = Double.parseDouble(line[4])
			journeyCommand.toLongitude = Double.parseDouble(line[5])
			journeyCommand.isDriver = true
			journeyManagerService.createJourney(rajan, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		time = Calendar.getInstance();
		time.add(Calendar.MINUTE, 5);
		
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
			journeyManagerService.createJourney(tamanna, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		time = Calendar.getInstance();
		time.add(Calendar.MINUTE, 10);
		lines.each {  line ->
			i++;
			JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
			journeyCommand.dateOfJourney = new Date(time.timeInMillis)
			journeyCommand.isMale = false
			journeyCommand.fromPlace = line[11]
			journeyCommand.fromLatitude = Double.parseDouble(line[9])
			journeyCommand.fromLongitude = Double.parseDouble(line[10])
			journeyCommand.toPlace = line[6]
			journeyCommand.toLatitude = Double.parseDouble(line[4])
			journeyCommand.toLongitude = Double.parseDouble(line[5])
			journeyCommand.isDriver = false
			journeyManagerService.createJourney(khwaish, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		
		
		i = 0;
		time = Calendar.getInstance();
		time.add(Calendar.MINUTE, 15);
		lines.each {  line ->
			i++;
			JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
			journeyCommand.dateOfJourney = new Date(time.timeInMillis)
			journeyCommand.isMale = true
			journeyCommand.fromPlace = line[11]
			journeyCommand.fromLatitude = Double.parseDouble(line[9])
			journeyCommand.fromLongitude = Double.parseDouble(line[10])
			journeyCommand.toPlace = line[6]
			journeyCommand.toLatitude = Double.parseDouble(line[4])
			journeyCommand.toLongitude = Double.parseDouble(line[5])
			journeyCommand.isDriver = false
			journeyManagerService.createJourney(rajan, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
		}
	}
}
