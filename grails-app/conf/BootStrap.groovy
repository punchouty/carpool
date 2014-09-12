import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.AdminsService
import grails.plugin.nimble.core.Role
import grails.plugin.nimble.core.UserBase
import grails.util.Environment
import liquibase.util.csv.opencsv.CSVReader

import org.elasticsearch.common.geo.GeoPoint

import com.racloop.Place
import com.racloop.staticdata.StaticData


class BootStrap {
	
	def grailsApplication
	def elasticSearchService
	def journeyManagerService
	def nimbleService
	def userService
	def adminsService
	def sampleDataService
		
    def init = { servletContext ->
		internalBootStap(servletContext)
		createRequiredUsers()
		Boolean createSampleUsers = grailsApplication.config.grails.startup.sampleUsers.create
		if(createSampleUsers) {
			log.info("Start creating initial users in database")
			createUsers();
			log.info("Users created successfully in Elasticsearch")
		}
		//Initialising Elasticsearch
		elasticSearchService.init()
		Boolean createIndex = grailsApplication.config.grails.startup.elasticsearch.index.create
		if(createIndex) {
			log.info("Start creating indexes in Elasticsearch")
			createIndexes();
			log.info("Indexes created successfully in Elasticsearch")
		}
		Boolean createMasterData = grailsApplication.config.grails.startup.masterData.places.create
		if(createMasterData) {
			log.info("Start creating master data for places in Elasticsearch")
			createMasterDataForPlaces();
			log.info("Master data for places created successfully in Elasticsearch")
		}
		
		intializeStaticData();
		
		if (Environment.current == Environment.DEVELOPMENT) {
			sampleDataService.deleteSampleData()
			log.info("Populating Sample Data");
			sampleDataService.populateSampleData();
			
        }
    }
	
    def destroy = {
		elasticSearchService.destroy()
    }

	private internalBootStap(servletContext) {
		nimbleService.init()
	}
	
	private def createIndexes() {
		// Main Index
		elasticSearchService.createMainJourneyIndex()		
		// Master Data Index
		elasticSearchService.createMasterLocationIndexIfNotExists();//TODO - check if it need to be here???
		//Dummy Data Index
		elasticSearchService.createGeneratedDataIndexIfNotExists();//TODO - check if it need to be here???
		elasticSearchService.createWorkflowIndex()
		
	}
	
	private def createRequiredUsers() {
		String adminUser ='admin@racloop.com'
		if(!UserBase.findByUsername(adminUser)) {
			// Create example Administrative account
			def admins = Role.findByName(AdminsService.ADMIN_ROLE)
			def admin = InstanceGenerator.user(grailsApplication)
			admin.username = adminUser
			admin.pass = "admiN123!"
			admin.passConfirm = "admiN123!"
			admin.enabled = true

			def adminProfile = InstanceGenerator.profile(grailsApplication)
			adminProfile.fullName = "Administrator"
			adminProfile.email = adminUser
			adminProfile.owner = admin
			adminProfile.isMale = true
			adminProfile.mobile = '9800000001'
			admin.profile = adminProfile

			log.info("Creating default admin account with username:admin")

			def savedAdmin = userService.createUser(admin)
			if (savedAdmin.hasErrors()) {
				savedAdmin.errors.each { log.error(it) }
				throw new RuntimeException("Error creating administrator")
			}

			adminsService.add(admin)
		}
	}
	
	private def createUsers() {
		String sampleUser = 'sample.user@racloop.com'
		String sampleDriver ='sample.driver@racloop.com'
		String sampleRider = 'sample.rider@racloop.com'
		//mail.live.com - user : sample.user@racloop.com, password : S@pient1
		if(!UserBase.findByUsername(sampleUser)) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = sampleUser
			user.pass = 'P@ssw0rd'
			user.passConfirm = 'P@ssw0rd'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Sample User"
			userProfile.email = sampleUser
			userProfile.owner = user
			userProfile.isMale = true
			userProfile.mobile = '9800000002'
			user.profile = userProfile

			log.info("Creating default user account with username:sample.user")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example sample.user")
			}
		}
		
		//mail.live.com - user : sample.driver@racloop.com, password : S@pient1
		if(!UserBase.findByUsername(sampleDriver)) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = sampleDriver
			user.pass = 'P@ssw0rd'
			user.passConfirm = 'P@ssw0rd'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Sample Driver"
			userProfile.email = sampleDriver
			userProfile.owner = user
			userProfile.isMale = true
			userProfile.mobile = '9800000003'
			user.profile = userProfile

			log.info("Creating default user account with username:sample.user")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example sample.user")
			}
		}
		
		//mail.live.com - user : sample.rider@racloop.com, password : S@pient1
		if(!UserBase.findByUsername(sampleRider)) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = sampleRider
			user.pass = 'P@ssw0rd'
			user.passConfirm = 'P@ssw0rd'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Sample Rider"
			userProfile.email = sampleRider
			userProfile.owner = user
			userProfile.isMale = false
			userProfile.mobile = '9800000004'
			user.profile = userProfile

			log.info("Creating default user account with username:sample.user")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example sample.user")
			}
		}
	}
	
	private def createMasterDataForPlaces() {
		CSVReader reader = new CSVReader(new InputStreamReader(this.class.classLoader.getResourceAsStream(grailsApplication.config.grails.startup.masterData.places.file)));
		Place previousPlace = null; //Assuming data is sorted in file w.r.t location
		List lines = reader.readAll();
		lines.each {  line ->
			Place place = new Place();
			String name = line[0]
			Double latitude = Double.parseDouble(line[1])
			Double longitude = Double.parseDouble(line[2])
			place.name = name
			place.location = new GeoPoint(latitude, longitude)
			if(!place.equals(previousPlace)) {
				elasticSearchService.indexLocation(place);
			}
			previousPlace = place;
		}
		reader.close();
	}
	
	private void intializeStaticData() {
		StaticData staticData  = StaticData.find {staticDataKey == 'safety'}
		if(!staticData){
			StaticData terms = [staticDataKey:'safety', pageData :'<p>Safty</p>']
			terms.save()
		}
		staticData  = StaticData.find {staticDataKey == 'faq'}
		if(!staticData){
			StaticData about = [staticDataKey:'faq', pageData :'<p>FAQ</p>']
			about.save()
		}
		staticData  = StaticData.find {staticDataKey == 'about'}
		if(!staticData){
			StaticData about = [staticDataKey:'about', pageData :'<p>About Us</p>']
			about.save()
		}
		staticData  = StaticData.find {staticDataKey == 'terms'}
		if(!staticData){
			StaticData terms = [staticDataKey:'terms', pageData :'<p>Terms and Condition</p>']
			terms.save()
		}
		staticData  = StaticData.find {staticDataKey == 'privacy'}
		if(!staticData){
			StaticData etiquettes = [staticDataKey:'privacy', pageData :'<p>Privacy Policy</p>']
			etiquettes.save()
		}
		staticData  = StaticData.find {staticDataKey == 'etiquettes'}
		if(!staticData){
			StaticData etiquettes = [staticDataKey:'etiquettes', pageData :'<p>Etiquettes</p>']
			etiquettes.save()
		}
	}
}
