package com.racloop

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
		
		def outgoingRequest =[] 
		def incomingRequest = []
		
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
			journeyCommand.user = user3.username
			journeyManagerService.createJourney(user3, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
			outgoingRequest<<journeyCommand
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

			journeyCommand.user = user4.username
			journeyManagerService.createJourney(user4, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
			incomingRequest<<journeyCommand
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
			journeyCommand.user = user2.username
			journeyManagerService.createJourney(user2, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
			outgoingRequest<<journeyCommand
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
			journeyCommand.user = user1.username
			journeyManagerService.createJourney(user1, journeyCommand)
			time.add(Calendar.MINUTE, timeInterval);
			incomingRequest<<journeyCommand
		}
		
		for ( x in 0..5 ) {
			try {
				JourneyRequestCommand requestObj = outgoingRequest.get(x)
				outgoingRequest.remove(x)
				JourneyRequestCommand responseObj = incomingRequest.get(x)
				incomingRequest.remove(x)
				//journeyManagerService.saveJourneyAndInitiateWorkflow(requestObj, responseObj)
			}
			catch(Exception e) {
				
				log.error "error while saving sample data for workflow", e
			}
		}
	}
	
}
