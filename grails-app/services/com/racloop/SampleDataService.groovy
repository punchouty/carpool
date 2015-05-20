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
		lines.each {  line ->
			i++;
			if(i%4 == 0) {
				JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
				journeyCommand.dateOfJourney = new Date(time.timeInMillis)
				journeyCommand.isMale = false
				journeyCommand.from = line[6]
				journeyCommand.fromLatitude = Double.parseDouble(line[4])
				journeyCommand.fromLongitude = Double.parseDouble(line[5])
				journeyCommand.to = line[11]
				journeyCommand.toLatitude = Double.parseDouble(line[9])
				journeyCommand.toLongitude = Double.parseDouble(line[10])
				journeyCommand.isDriver = true
				journeyCommand.tripDistance = 250.0d
				journeyCommand.user = user1.username
				journeyManagerService.createJourney(user1, journeyCommand)
				outgoingRequest<<journeyCommand
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.MINUTE, -90);
		//Delhi to Chandigarh - sample.rider
		lines.each {  line ->
			i++;
			if(i%4 == 1) {
				JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
				journeyCommand.dateOfJourney = new Date(time.timeInMillis)
				journeyCommand.isMale = false
				journeyCommand.from = line[6]
				journeyCommand.fromLatitude = Double.parseDouble(line[4])
				journeyCommand.fromLongitude = Double.parseDouble(line[5])
				journeyCommand.to = line[11]
				journeyCommand.toLatitude = Double.parseDouble(line[9])
				journeyCommand.toLongitude = Double.parseDouble(line[10])
				journeyCommand.isDriver = false
				journeyCommand.tripDistance = 250.0d
	
				journeyCommand.user = user2.username
				journeyManagerService.createJourney(user2, journeyCommand)
				incomingRequest<<journeyCommand
			}
			time.add(Calendar.MINUTE, timeInterval);
		}		
		i = 0;
		time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		//Delhi to Chandigarh - admin
		time.add(Calendar.MINUTE, -120);
		lines.each {  line ->
			i++;
			if(i%4 == 2) {
				JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
				journeyCommand.dateOfJourney = new Date(time.timeInMillis)
				journeyCommand.isMale = false
				journeyCommand.from = line[6]
				journeyCommand.fromLatitude = Double.parseDouble(line[4])
				journeyCommand.fromLongitude = Double.parseDouble(line[5])
				journeyCommand.to = line[11]
				journeyCommand.toLatitude = Double.parseDouble(line[9])
				journeyCommand.toLongitude = Double.parseDouble(line[10])
				journeyCommand.isDriver = true
				journeyCommand.tripDistance = 250.0d
				journeyCommand.user = user3.username
				journeyManagerService.createJourney(user3, journeyCommand)
				outgoingRequest<<journeyCommand
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.MINUTE, -150);
		//Delhi to Chandigarh - admin
		lines.each {  line ->
			i++;
			if(i%4 == 3) {
				JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
				journeyCommand.dateOfJourney = new Date(time.timeInMillis)
				journeyCommand.isMale = false
				journeyCommand.from = line[6]
				journeyCommand.fromLatitude = Double.parseDouble(line[4])
				journeyCommand.fromLongitude = Double.parseDouble(line[5])
				journeyCommand.to = line[11]
				journeyCommand.toLatitude = Double.parseDouble(line[9])
				journeyCommand.toLongitude = Double.parseDouble(line[10])
				journeyCommand.isDriver = false
				journeyCommand.tripDistance = 250.0d
				journeyCommand.user = user4.username
				journeyManagerService.createJourney(user4, journeyCommand)
				incomingRequest<<journeyCommand
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		for ( x in 0..2 ) {
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
