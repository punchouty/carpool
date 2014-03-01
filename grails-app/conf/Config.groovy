// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

//******************** IMPORTANT PATH INFORMATION ******************************************//
// Elastic search related directories (refer elasticsearch.yml) - c:\\data\\elasticsearch	//
// Active MQ related directories (refer resources.groovy) - C:/data/activemq				//
// H2 database related directories (refer datasource.groovy) - C:/data/h2database			//
//******************************************************************************************//

// Below configuration after cleaning c:\\data
grails.startup.elasticsearch.index.create = true
grails.startup.sampleUsers.create = true
grails.startup.masterData.places.create = true
//Below configuration will not refresh data
//grails.startup.elasticsearch.index.create = false
//grails.startup.sampleUsers.create = false
//grails.startup.masterData.places.create = false

grails.generatedData.index.name = 'generated_data'//generated journeys by application
grails.masterData.places.index.name = 'location_master'
grails.masterData.places.index.type = 'india'

grails.ui.dateFormat = 'dd/MM/yyyy hh:mm a'
grails.journeyIndexNameFormat = 'yy-MM-W'
grails.startup.sampleData.file = "journey.csv"
grails.startup.masterData.places.file = "all.csv"

environments {
    development {
        grails.logging.jul.usebridge = true
		grails.resources.debug = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

grails {
   mail {
     host = "smtp.live.com"
     port = 587
     username = "help@racloop.com"
	 password = "R@cloop1"
	 props = [
		 "mail.smtp.auth":"true",
		 "mail.smtp.starttls.enable":"true", 
		 "mail.transport.protocol":"smtp",
         "mail.smtp.host":"smtp.live.com",
         "mail.smtp.port":"587"
         ]
   }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
	
	info "grails.app"
	
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    }

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}
