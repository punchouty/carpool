import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.UserBase
import grails.util.Environment
import liquibase.util.csv.opencsv.CSVReader

import com.racloop.Journey
import com.racloop.User


class BootStrap {
	
	def grailsApplication
	def elasticSearchService
	def journeyService
	def nimbleService
	def userService
	def adminsService

    def init = { servletContext ->
		internalBootStap(servletContext)
		elasticSearchService.init()
		Environment.executeForCurrentEnvironment {
			development {
				//createUsers();
				//createSampleData();
			}
		  }
    }
	
    def destroy = {
		elasticSearchService.destroy()
    }

	private internalBootStap(servletContext) {
		nimbleService.init()
	}
	
	private def createUsers() {
		if(!UserBase.findByUsername("tamanna")) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = "tamanna"
			user.pass = 'P@ssw0rd'
			user.passConfirm = 'P@ssw0rd'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Tamanna Punchouty"
			userProfile.owner = user
			user.profile = userProfile

			log.info("Creating default user account with username:tamanna")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example tamanna")
			}
		}
		
		if(!UserBase.findByUsername("rajan")) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = "rajan"
			user.pass = 'P@ssw0rd'
			user.passConfirm = 'P@ssw0rd'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Rajan Punchouty"
			userProfile.owner = user
			user.profile = userProfile

			log.info("Creating default user account with username:rajan")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example rajan")
			}
		}
		
		if(!UserBase.findByUsername("khwaish")) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = "khwaish"
			user.pass = 'P@ssw0rd'
			user.passConfirm = 'P@ssw0rd'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Khwaish Punchouty"
			userProfile.owner = user
			user.profile = userProfile

			log.info("Creating default user account with username:khwaish")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example khwaish")
			}
		}
	}
	
	private def createSampleData() {
		int timeInterval = 15
		User rajan = User.findByUsername('rajan');
		log.info("Found user rajan : ${rajan.username}")
		User tamanna = User.findByUsername('tamanna');
		log.info("Found user tamanna : ${tamanna.username}")
		User khwaish = User.findByUsername('khwaish');
		log.info("Found user khwaish : ${khwaish.username}")
		String dateFormat = grailsApplication.config.grails.dateFormat
		CSVReader reader = new CSVReader(new FileReader("docs\\other\\journey1.csv"));
		List lines = reader.readAll();
		
		int i = 0;		
		Calendar time = Calendar.getInstance();
		
		lines.each {  line ->
			i++;
			Journey journey = new Journey()
			journey.dateOfJourney = new Date(time.timeInMillis)
			journey.fromPlace = line[6]
			journey.fromLatitude = Double.parseDouble(line[4])
			journey.fromLongitude = Double.parseDouble(line[5])
			journey.toPlace = line[11]
			journey.toLatitude = Double.parseDouble(line[9])
			journey.toLongitude = Double.parseDouble(line[10])
			journey.isDriver = true
//			if(i%2 == 0) {
//				journey.isDriver = true
//			}
//			else {
//				journey.isDriver = false
//			}
			journeyService.saveJourney(tamanna, journey)
			time.add(Calendar.MINUTE, timeInterval);
		}
		
//		i = 0;
//		time = Calendar.getInstance();	
//		time.add(Calendar.MINUTE, 3);
//		
//		lines.each {  line ->
//			i++;
//			Journey journey = new Journey()
//			journey.dateOfJourney = new Date(time.timeInMillis)
//			journey.fromPlace = line[11]
//			journey.fromLatitude = Double.parseDouble(line[9])
//			journey.fromLongitude = Double.parseDouble(line[10])
//			journey.toPlace = line[6]
//			journey.toLatitude = Double.parseDouble(line[4])
//			journey.toLongitude = Double.parseDouble(line[5])
//			journey.isDriver = true
//			if(i%2 == 0) {
//				journey.isDriver = true
//			}
//			else {
//				journey.isDriver = false
//			}
//			journeyService.saveJourney(tamanna, journey)
//			time.add(Calendar.MINUTE, timeInterval);
//		}
		tamanna.activeJourneys.each { journey ->
			log.info "Tamanna - Processing journey record for elastic search ${journey.id}"
			elasticSearchService.indexJourneyWithIndexCheck(journey)
		}
		
//		i == 0;
//		time = Calendar.getInstance();	
//		time.add(Calendar.MINUTE, 6);
//		lines.each {  line ->
//			i++;
//			Journey journey = new Journey()
//			journey.dateOfJourney = new Date(time.timeInMillis)
//			journey.fromPlace = line[6]
//			journey.fromLatitude = Double.parseDouble(line[4])
//			journey.fromLongitude = Double.parseDouble(line[5])
//			journey.toPlace = line[11]
//			journey.toLatitude = Double.parseDouble(line[9])
//			journey.toLongitude = Double.parseDouble(line[10])
//			journey.isDriver = false
//			if(i%2 == 0) {
//				journey.isDriver = false
//			}
//			else {
//				journey.isDriver = true
//			}
//			journeyService.saveJourney(khwaish, journey)
//			time.add(Calendar.MINUTE, timeInterval);
//		}
		
		i == 0;
		time = Calendar.getInstance();	
		time.add(Calendar.MINUTE, 9);
		lines.each {  line ->
			i++;
			Journey journey = new Journey()
			journey.dateOfJourney = new Date(time.timeInMillis)
			journey.fromPlace = line[11]
			journey.fromLatitude = Double.parseDouble(line[9])
			journey.fromLongitude = Double.parseDouble(line[10])
			journey.toPlace = line[6]
			journey.toLatitude = Double.parseDouble(line[4])
			journey.toLongitude = Double.parseDouble(line[5])
			journey.isDriver = false
//			if(i%2 == 0) {
//				journey.isDriver = false
//			}
//			else {
//				journey.isDriver = true
//			}
			journeyService.saveJourney(khwaish, journey)
			time.add(Calendar.MINUTE, timeInterval);
		}
		khwaish.activeJourneys.each { journey ->
			log.info "Khwaish - Processing journey record for elastic search ${journey.id}"
			elasticSearchService.indexJourneyWithIndexCheck(journey)
		}
	}
}
