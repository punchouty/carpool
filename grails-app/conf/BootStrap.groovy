import grails.plugin.nimble.InstanceGenerator
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
		
		if (Environment.current == Environment.DEVELOPMENT) {
            intializeStaticData()
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
		elasticSearchService.createMainIndex();		
		// Master Data Index
		elasticSearchService.createMasterLocationIndexIfNotExists();//TODO - check if it need to be here???
		//Dummy Data Index
		elasticSearchService.createGeneratedDataIndexIfNotExists();//TODO - check if it need to be here???
		elasticSearchService.createWorkflowIndex()
		
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
			userProfile.email = "tamanna.punchouty@gmail.com"
			userProfile.owner = user
			userProfile.isMale = false
			userProfile.mobile = '9800000002'
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
			userProfile.email = "rajan@racloop.com"
			userProfile.owner = user
			userProfile.isMale = true
			userProfile.mobile = '9800000003'
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
			userProfile.email = "khwaish.punchouty@gmail.com"
			userProfile.owner = user
			userProfile.isMale = false
			userProfile.mobile = '9800000004'
			user.profile = userProfile

			log.info("Creating default user account with username:khwaish")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example khwaish")
			}
		}
		
		if(!UserBase.findByUsername("sample.user")) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = "sample.user"
			user.pass = 'P@ssw0rd'
			user.passConfirm = 'P@ssw0rd'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Sample User"
			userProfile.email = "sample.user@racloop.com"
			userProfile.owner = user
			userProfile.isMale = false
			userProfile.mobile = '9800000005'
			user.profile = userProfile

			log.info("Creating default user account with username:sample.user")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example sample.user")
			}
		}
		
		if(!UserBase.findByUsername("sample.driver")) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = "sample.driver"
			user.pass = 'P@ssw0rd'
			user.passConfirm = 'P@ssw0rd'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Sample Driver"
			userProfile.email = "sample.driver@racloop.com"
			userProfile.owner = user
			userProfile.isMale = false
			userProfile.mobile = '9800000006'
			user.profile = userProfile

			log.info("Creating default user account with username:sample.user")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example sample.user")
			}
		}
		
		if(!UserBase.findByUsername("sample.rider")) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = "sample.rider"
			user.pass = 'P@ssw0rd'
			user.passConfirm = 'P@ssw0rd'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Sample Rider"
			userProfile.email = "sample.rider@racloop.com"
			userProfile.owner = user
			userProfile.isMale = false
			userProfile.mobile = '9800000007'
			user.profile = userProfile

			log.info("Creating default user account with username:sample.user")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example sample.user")
			}
		}
	}
	
	private def createRequiredUsers() {
		if(!UserBase.findByUsername("admin")) {
			// Create example Administrative account
			def admins = Role.findByName(AdminsService.ADMIN_ROLE)
			def admin = InstanceGenerator.user(grailsApplication)
			admin.username = "admin"
			admin.pass = "admiN123!"
			admin.passConfirm = "admiN123!"
			admin.enabled = true

			def adminProfile = InstanceGenerator.profile(grailsApplication)
			adminProfile.fullName = "Administrator"
			adminProfile.email = "admin@racloop.com"
			adminProfile.owner = admin
			adminProfile.isMale = false
			adminProfile.mobile = '9800000000'
			admin.profile = adminProfile

			log.info("Creating default admin account with username:admin")

			def savedAdmin = userService.createUser(admin)
			if (savedAdmin.hasErrors()) {
				savedAdmin.errors.each { log.error(it) }
				throw new RuntimeException("Error creating administrator")
			}

			adminsService.add(admin)
		}		
		
		if(!UserBase.findByUsername("user")) {
			// Create example User account
			def user = InstanceGenerator.user(grailsApplication)
			user.username = "user"
			user.pass = 'useR123!'
			user.passConfirm = 'useR123!'
			user.enabled = true

			def userProfile = InstanceGenerator.profile(grailsApplication)
			userProfile.fullName = "Test User"
			userProfile.email = "test.user@racloop.com"
			userProfile.owner = user
			userProfile.isMale = false
			userProfile.mobile = '9800000001'
			user.profile = userProfile

			log.info("Creating default user account with username:user")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example user")
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
		StaticData staticData  = StaticData.find {key == 'safety'}
		if(!staticData){
			StaticData terms = [key:'safety', data :'<p>Safty</p>']
			terms.save()
		}
		staticData  = StaticData.find {key == 'faq'}
		if(!staticData){
			StaticData about = [key:'faq', data :'<p>FAQ</p>']
			about.save()
		}
		staticData  = StaticData.find {key == 'about'}
		if(!staticData){
			StaticData about = [key:'about', data :'<p>About Us</p>']
			about.save()
		}
		staticData  = StaticData.find {key == 'terms'}
		if(!staticData){
			StaticData terms = [key:'terms', data :'<p>Terms and Condition</p>']
			terms.save()
		}
		staticData  = StaticData.find {key == 'privacy'}
		if(!staticData){
			StaticData etiquettes = [key:'privacy', data :'<p>Privacy Policy</p>']
			etiquettes.save()
		}
		staticData  = StaticData.find {key == 'etiquettes'}
		if(!staticData){
			StaticData etiquettes = [key:'etiquettes', data :'<p>Etiquettes</p>']
			etiquettes.save()
		}
	}
}
