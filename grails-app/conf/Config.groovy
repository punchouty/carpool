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

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = [
	'Gecko',
	'WebKit',
	'Presto',
	'Trident'
]
grails.mime.types = [ // the first one is the default format
	all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
	atom:          'application/atom+xml',
	css:           'text/css',
	csv:           'text/csv',
	form:          'application/x-www-form-urlencoded',
	html:          [
		'text/html',
		'application/xhtml+xml'
	],
	js:            'text/javascript',
	json:          [
		'application/json',
		'text/json'
	],
	multipartForm: 'multipart/form-data',
	rss:           'application/rss+xml',
	text:          'text/plain',
	hal:           [
		'application/hal+json',
		'application/hal+xml'
	],
	xml:           [
		'text/xml',
		'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = [
	'/images/*',
	'/css/*',
	'/js/*',
	'/plugins/*'
]

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

grails.resources.debug = true

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
	views {
		gsp {
			encoding = 'UTF-8'
			htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
			codecs {
				expression = 'html' // escapes values inside ${}
				scriptlet = 'html' // escapes output from scriptlets in GSPs
				taglib = 'none' // escapes output from taglibs
				staticparts = 'none' // escapes output from static template parts
			}
		}
		// escapes all not-encoded output at final stage of outputting
		filteringCodecForContentType {
			//'text/html' = 'html'
		}
	}
}

grails.converters.encoding = "UTF-8"
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

grails.email.exception.one="rajan@racloop.com"
grails.email.exception.two="rohit@racloop.com"

grails.messaging.mail.from="help@racloop.com"

grails.approx.distance.to.match = 3.0
grails.approx.time.to.match = 30
grails.max.active.requests = 5

environments {
	development {
		grails.logging.jul.usebridge = true
	}
	production {
		grails.logging.jul.usebridge = false
		grails.serverURL = "http://www.racloop.com"
	}
}

auditLog {
	largeValueColumnTypes = true
	actorClosure = { request, session ->
	  org.apache.shiro.SecurityUtils.getSubject()?.getPrincipal()
	}
}

grails {
	mail {
		from = "raC looP <help@racloop.com>"
		host = "smtp.racloop.com"
		port = 25
		username = "help@racloop.com"
		password = "P@ssw0rd"
//		props = ["mail.smtp.starttls.enable":"true", 
//                  "mail.smtp.port":"587"]
	}
}

// Login to account - http://www.smsgatewaycenter.com/login
sms {
	url = "http://www.smsgatewaycenter.com/library/send_sms_2.php"
	mask = "RACLOP"
	username = "mcs47chd"
	password = "MCS47CHD?123"
	templates {
		verification = 'Your verification code for www.racloop.com code is $verificationCode'
		newRequest = 'There is new car pool request against your journey on $journeyDate'
		acceptRequest = 'Your car pool request for journey on $journeyDate is ACCEPTED by user $name. Mobile : $mobile'
		rejectRequest = 'Your car pool request for journey on $journeyDate is REJECTED by user $name.'
		cancelRequest = 'User $name has CANCELLED his journey on $journeyDate'
	}
}

// log4j configuration
log4j = {
	// Example of changing the log pattern for the default console appender:
	//

	info "grails.app"

	appenders {
		console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
		file name: 'file', file: '/var/racloop/log/tomcat-racloop.log'
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