import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.UserBase
import grails.util.Environment
import liquibase.util.csv.opencsv.CSVReader

import com.racloop.Journey
import com.racloop.JourneyRequestCommand
import com.racloop.User


class BootStrap {
	
	def grailsApplication
	def elasticSearchService
	def journeyManagerService
	//def journeyService
	def nimbleService
	def userService
	def adminsService

    def init = { servletContext ->
		internalBootStap(servletContext)
		elasticSearchService.init()
		Environment.executeForCurrentEnvironment {
			development {
				createIndexes();
				createUsers();
				createSampleData();
			}
		  }
    }
	
    def destroy = {
		elasticSearchService.destroy()
    }

	private internalBootStap(servletContext) {
		nimbleService.init()
	}
	
	private def createIndexes() {
		Date date = new Date().previous();
		elasticSearchService.createIndexIfNotExistsForDate(date);
		date = date.next();
		elasticSearchService.createIndexIfNotExistsForDate(date);
		date = date.next();
		elasticSearchService.createIndexIfNotExistsForDate(date);
		date = date.next();
		elasticSearchService.createIndexIfNotExistsForDate(date);
		date = date.next();
		elasticSearchService.createIndexIfNotExistsForDate(date);
		
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
			JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
			journeyCommand.dateOfJourney = new Date(time.timeInMillis)
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
		
		i == 0;
		time = Calendar.getInstance();	
		time.add(Calendar.MINUTE, 9);
		lines.each {  line ->
			i++;
			JourneyRequestCommand journeyCommand = new JourneyRequestCommand()
			journeyCommand.dateOfJourney = new Date(time.timeInMillis)
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
	}
}
