package com.racloop.persistence

import liquibase.util.csv.opencsv.CSVReader;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.racloop.Constant;
import com.racloop.DistanceUtil;
import com.racloop.Profile;
import com.racloop.User;
import com.racloop.domain.Journey;
import com.racloop.domain.RacloopUser;

import grails.plugin.nimble.InstanceGenerator;
import grails.plugin.nimble.core.UserBase;
import grails.transaction.Transactional

@Transactional
class TestDataService {
	
	def grailsApplication;
	def journeyDataService;
	def searchService;
	def userDataService;
	def userService;
	def roleService;
	def groupService;
	
	
	
	def createUsers() {
		String sampleUser = 'user@racloop.com'
		String sampleDriver ='driver@racloop.com'
		String sampleRider = 'rider@racloop.com'
		//mail.live.com - user : sample.user@racloop.com, password : S@pient1
		if(!UserBase.findByUsername(sampleUser)) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = sampleUser
			user.pass = 'qwert'
			user.passConfirm = 'qwert'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Sample User"
			userProfile.email = sampleUser
			userProfile.owner = user
			userProfile.isMale = true
			userProfile.mobile = '7307392447'
			user.profile = userProfile

			log.info("Creating default user account with username: ${user.username}")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example ${user.username}")
			}
			else {
				RacloopUser racloopUser = userDataService.findUserByMobile(userProfile.mobile)
				if(racloopUser == null) {
					racloopUser = new RacloopUser();
					racloopUser.setMobile(userProfile.mobile)
					racloopUser.setEmail(userProfile.email);
					racloopUser.setFullName(userProfile.fullName)
					racloopUser.setEmailHash(userProfile.emailHash)
					userDataService.saveUser(racloopUser)
				}
				else {
					log.info("Username: ${user.username} already there in dynamodb")
				}
			}
		}
		
		//mail.live.com - user : sample.driver@racloop.com, password : S@pient1
		if(!UserBase.findByUsername(sampleDriver)) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = sampleDriver
			user.pass = 'qwert'
			user.passConfirm = 'qwert'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Sample Driver"
			userProfile.email = sampleDriver
			userProfile.owner = user
			userProfile.isMale = true
			userProfile.mobile = '9646698749'
			user.profile = userProfile

			log.info("Creating default user account with username: ${user.username}")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example ${user.username}")
			}
			else {
				RacloopUser racloopUser = userDataService.findUserByMobile(userProfile.mobile)
				if(racloopUser == null) {
					racloopUser = new RacloopUser();
					racloopUser.setMobile(userProfile.mobile)
					racloopUser.setEmail(userProfile.email);
					racloopUser.setFullName(userProfile.fullName)
					racloopUser.setEmailHash(userProfile.emailHash)
					userDataService.saveUser(racloopUser)
				}
				else {
					log.info("Username: ${user.username} already there in dynamodb")
				}
			}
		}
		
		//mail.live.com - user : sample.rider@racloop.com, password : S@pient1
		if(!UserBase.findByUsername(sampleRider)) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = sampleRider
			user.pass = 'qwert'
			user.passConfirm = 'qwert'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Sample Rider"
			userProfile.email = sampleRider
			userProfile.owner = user
			userProfile.isMale = false
			userProfile.mobile = '9646695649'
			user.profile = userProfile

			log.info("Creating default user account with username: ${user.username}")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example ${user.username}")
			}
			else {
				RacloopUser racloopUser = userDataService.findUserByMobile(userProfile.mobile)
				if(racloopUser == null) {
					racloopUser = new RacloopUser();
					racloopUser.setMobile(userProfile.mobile)
					racloopUser.setEmail(userProfile.email);
					racloopUser.setFullName(userProfile.fullName)
					racloopUser.setEmailHash(userProfile.emailHash)
					userDataService.saveUser(racloopUser)
				}
				else {
					log.info("Username: ${user.username} already there in dynamodb")
				}
			}
		}
	}
	
	def generateDataForDev(int numberOfrecordsToGenerate) {
		boolean generateYesterdayData = false;
		def outgoingRequest =[]
		def incomingRequest = []
		if(numberOfrecordsToGenerate < 1) numberOfrecordsToGenerate = 1;
		int timeInterval = 30
		User user1 = User.findByUsername('admin@racloop.com');
		User user2 = User.findByUsername('user@racloop.com');
		User user3 = User.findByUsername('driver@racloop.com');
		User user4 = User.findByUsername('rider@racloop.com');
		String dateFormat = grailsApplication.config.grails.dateFormat
		CSVReader reader = new CSVReader(new InputStreamReader(this.class.classLoader.getResourceAsStream(grailsApplication.config.grails.startup.sampleData.file)));
		List lines = reader.readAll();
		
		int i = 0;
		int generatedRecordCount = 0;
		Calendar time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.MINUTE, -60);
		if(generateYesterdayData) time.add(Calendar.HOUR, -22);
		//Delhi to Chandigarh - sample.driver
		User user = user1;
		lines.each {  line ->
			i++;
			if(i%4 == 3) {
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
				journey.isDummy = false;
				journey.createdDate = new Date();
				if(generatedRecordCount < numberOfrecordsToGenerate) {
					journeyDataService.createJourney(journey);
					outgoingRequest<<journey
					generatedRecordCount++;
				}
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		generatedRecordCount = 0;
		time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.MINUTE, -90);
		if(generateYesterdayData) time.add(Calendar.HOUR, -22);
		user = user2;
		lines.each {  line ->
			i++;
			if(i%4 == 2) {
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
				if(generatedRecordCount < numberOfrecordsToGenerate) {
					journeyDataService.createJourney(journey);
					outgoingRequest<<journey
					generatedRecordCount++;
				}
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		generatedRecordCount = 0;
		time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.MINUTE, -90);
		if(generateYesterdayData) time.add(Calendar.HOUR, -22);
		user = user3;
		lines.each {  line ->
			i++;
			if(i%4 == 1) {
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
				if(generatedRecordCount < numberOfrecordsToGenerate) {
					journeyDataService.createJourney(journey);
					outgoingRequest<<journey
					generatedRecordCount++;
				}
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
		
		i = 0;
		generatedRecordCount = 0;
		time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.MINUTE, -90);
		if(generateYesterdayData) time.add(Calendar.HOUR, -22);
		user = user4;
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
				if(generatedRecordCount < numberOfrecordsToGenerate) {
					journeyDataService.createJourney(journey);
					outgoingRequest<<journey
					generatedRecordCount++;
				}
			}
			time.add(Calendar.MINUTE, timeInterval);
		}
	}
	
	def deleteDataForDev() {
		//searchService.deleteAllJourneyData();
		User user1 = User.findByUsername('admin@racloop.com');
		journeyDataService.deleteJourneyForUser(user1.profile.mobile);
		searchService.deleteAllJourneyDataForUser(user1.profile.mobile);
		
		User user2 = User.findByUsername('user@racloop.com');
		journeyDataService.deleteJourneyForUser(user2.profile.mobile);
		searchService.deleteAllJourneyDataForUser(user2.profile.mobile);
		
		User user3 = User.findByUsername('driver@racloop.com');
		journeyDataService.deleteJourneyForUser(user3.profile.mobile);
		searchService.deleteAllJourneyDataForUser(user3.profile.mobile);
		
		User user4 = User.findByUsername('rider@racloop.com');
		journeyDataService.deleteJourneyForUser(user4.profile.mobile);
		searchService.deleteAllJourneyDataForUser(user4.profile.mobile);
		
		journeyDataService.deleteJourneyForUser(Constant.DUMMY_USER_MOBILE);
		searchService.deleteAllDummyData();
		
		
	}
	
	def deleteDataForUser(String secret, String mobile) {
		String deletePassword = grailsApplication.config.grails.delete.user.password
		if(secret.equals(deletePassword)) {
			Profile profile = Profile.findByMobile(mobile);
			if(profile != null) {
				User user = profile.owner;
				journeyDataService.deleteJourneyForUser(mobile);
				searchService.deleteAllJourneyDataForUser(mobile);
				return "successfully deleted journey data for user"
			}
			else {
				return "Unable to find user with mobile :  ${mobile}";
			}
		}
		else {
			return "Wrong password";
		}
	}
	
	def deleteUser(String secret, String mobile) {
		String deletePassword = grailsApplication.config.grails.delete.user.password
		if(secret.equals(deletePassword)) {
			Profile profile = Profile.findByMobile(mobile);
			if(profile.email.equals('admin@racloop.com')) {
				return "You cannot remove admin from system";
			}
			if(profile != null) {
				User user = User.findByUsername(profile.email);
				if(user) {
					log.info("Deleting User : " + user.username)
					journeyDataService.deleteJourneyForUser(user.profile.mobile);
					searchService.deleteAllJourneyDataForUser(user.profile.mobile);
					userDataService.deleteUserByMobile(user.profile.mobile);//delete from dynamo db
					user.groups.each { group ->
						groupService.deleteMember(user, group)
					}
					user.roles.each { role ->
						roleService.deleteMember(user, role)
					}
					//user2.profile.delete();//delete from mysql
					user.delete();//delete from mysql
				}
				
				return "successfully deleted user and associated journeys"
			}
			else {
				return "Unable to find user with mobile :  ${mobile}";
			}
		}
		else {
			return "Wrong password";
		}
	}
	
	def deleteAll() {
		User user1 = User.findByUsername('admin@racloop.com');
		if(user1) {
			journeyDataService.deleteJourneyForUser(user1.profile.mobile);
			searchService.deleteAllJourneyDataForUser(user1.profile.mobile);
		}
		
		User user2 = User.findByUsername('user@racloop.com');
		if(user2) {
			journeyDataService.deleteJourneyForUser(user2.profile.mobile);
			searchService.deleteAllJourneyDataForUser(user2.profile.mobile);
		}
		
		User user3 = User.findByUsername('driver@racloop.com');
		if(user3) {
			journeyDataService.deleteJourneyForUser(user3.profile.mobile);
			searchService.deleteAllJourneyDataForUser(user3.profile.mobile);
		}
		
		User user4 = User.findByUsername('rider@racloop.com');
		if(user2) {
			journeyDataService.deleteJourneyForUser(user4.profile.mobile);
			searchService.deleteAllJourneyDataForUser(user4.profile.mobile);
		}
		searchService.deleteAllDummyData();
		
		if(user2) {
			userDataService.deleteUserByMobile(user2.profile.mobile);//delete from dynamo db
			user2.groups.each { group ->
				groupService.deleteMember(user2, group)
			}
			user2.roles.each { role ->
				roleService.deleteMember(user2, role)
			}
			user2.profile.delete();//delete from mysql
			user2.delete(flush : true);//delete from mysql
		}
		
		if(user3) {
			userDataService.deleteUserByMobile(user3.profile.mobile);//delete from dynamo db
			user3.groups.each { group ->
				groupService.deleteMember(user3, group)
			}
			user3.roles.each { role ->
				roleService.deleteMember(user3, role)
			}
			user3.profile.delete();//delete from mysql
			user3.delete(flush : true);//delete from mysql
		}
		
		if(user2) {
			userDataService.deleteUserByMobile(user4.profile.mobile);//delete from dynamo db
			user4.groups.each { group ->
				groupService.deleteMember(user4, group)
			}
			user4.roles.each { role ->
				roleService.deleteMember(user4, role)
			}
			user4.profile.delete();//delete from mysql
			user4.delete(flush : true);//delete from mysql
		}
	}
	
	def refreshAll(int recordCount) {
		deleteAll();
		createUsers();
		generateDataForDev(recordCount);
	}
	
	def populateElasticSearch() {
		searchService.deleteAllJourneyData();
		User user1 = User.findByUsername('admin@racloop.com');
		User user = user1;
		List<Journey> journeys = journeyDataService.findAllJourneyForUser(user.profile.mobile);
		for (Journey dbJourney: journeys) {
			Journey esJourney = searchService.getJourney(dbJourney.getId());
			if(esJourney == null) {
				log.info("In populateElasticSearch no data in ES ${dbJourney}")
				searchService.indexJourney(dbJourney, dbJourney.getId());
			}
			else {
				log.info("Found in ES ${dbJourney}")
			}
		}
		User user2 = User.findByUsername('user@racloop.com');
		user = user2;
		journeys = journeyDataService.findAllJourneyForUser(user.profile.mobile);
		for (Journey dbJourney: journeys) {
			Journey esJourney = searchService.getJourney(dbJourney.getId());
			if(esJourney == null) {
				log.info("In populateElasticSearch no data in ES ${dbJourney}")
				searchService.indexJourney(dbJourney, dbJourney.getId());
			}
			else {
				log.info("Found in ES ${dbJourney}")
			}
		}
		User user3 = User.findByUsername('driver@racloop.com');
		user = user3;
		journeys = journeyDataService.findAllJourneyForUser(user.profile.mobile);
		for (Journey dbJourney: journeys) {
			Journey esJourney = searchService.getJourney(dbJourney.getId());
			if(esJourney == null) {
				log.info("In populateElasticSearch no data in ES ${dbJourney}")
				searchService.indexJourney(dbJourney, dbJourney.getId());
			}
			else {
				log.info("Found in ES ${dbJourney}")
			}
		}
		User user4 = User.findByUsername('rider@racloop.com');
		user = user4;
		journeys = journeyDataService.findAllJourneyForUser(user.profile.mobile);
		for (Journey dbJourney: journeys) {
			Journey esJourney = searchService.getJourney(dbJourney.getId());
			if(esJourney == null) {
				log.info("In populateElasticSearch no data in ES ${dbJourney}")
				searchService.indexJourney(dbJourney, dbJourney.getId());
			}
			else {
				log.info("Found in ES ${dbJourney}")
			}
		}
	}
}
