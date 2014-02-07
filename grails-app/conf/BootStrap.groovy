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
		elasticSearchService.init()
		Boolean createIndex = grailsApplication.config.grails.startup.elasticsearch.index.create
		if(createIndex) {
			log.info("Start creating indexes in Elasticsearch")
			createIndexes();
			log.info("Indexes created successfully in Elasticsearch")
		}
		Boolean createSampleUsers = grailsApplication.config.grails.startup.sampleUsers.create
		if(createSampleUsers) {
			log.info("Start creating initial users in database")
			createUsers();
			log.info("Users created successfully in Elasticsearch")
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
			userProfile.isMale = false
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
			userProfile.isMale = true
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
			userProfile.isMale = false
			user.profile = userProfile

			log.info("Creating default user account with username:khwaish")

			def savedUser = userService.createUser(user)
			if (savedUser.hasErrors()) {
				savedUser.errors.each { log.error(it) }
				throw new RuntimeException("Error creating example khwaish")
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
		StaticData staticData  = StaticData.find {key == 'term'}
		if(!staticData){
			StaticData terms = [key:'term', data :'<p>Terms and Condition</p>']
			terms.save()
		}
		staticData  = StaticData.find {key == 'about'}
		if(!staticData){
			StaticData about = [key:'about', data :'<p>About Us</p>']
			about.save()
		}
		staticData  = StaticData.find {key == 'etiquettes'}
		if(!staticData){
			StaticData etiquettes = [key:'etiquettes', data :'<p>Etiquettes</p>']
			etiquettes.save()
		}
	}
}
