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

grails.resources.debug = false

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'prototype'

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

grails.email.mail.from="admin@racloop.com"
grails.email.exception.one="rajan@racloop.com"
grails.email.exception.two="rohit@racloop.com"
grails.sms.emergency.one="9810095625"
grails.sms.emergency.two="7307392447"

grails.messaging.mail.from="admin@racloop.com"

grails.approx.distance.to.match = 3.0
grails.approx.time.to.match = 30
grails.max.active.requests = 5
grails.approx.time.range= 45
grails.max.days.to.search= 7

grails.aws.dynamodb.local = true
grails.plugin.awssdk.accessKey = "AKIAJ74MXKGZZPYXKEWA"
grails.plugin.awssdk.secretKey = "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"
grails.plugin.awssdk.region = 'ap-southeast-1'


environments {
	development {
		grails.serverURL="http://localhost:8080/app"
		grails.startup.elasticsearch.index.create = true
		grails.startup.sampleUsers.create = true
		grails.startup.masterData.places.create = true
		grails.startup.sampleData.create = true
		grails.enable.delete.all = true
		grails.delete.user.password = "s3cr3t"
		grails.logging.jul.usebridge = true
		grails.plugin.facebooksdk.app.id = 417141881785415
		grails.plugin.facebooksdk.app.permissions = ['email','read_stream','user_birthday']
		grails.plugin.facebooksdk.app.secret = '3461eaa74e391bb6add17f9b25a38355'
		grails.sms.enable=false
		grails.email.enable=true
		
		grails.activemq.data.dir = "${System.getProperty('user.home')}/racloop-data/activemq/data"
		
		grails.es.cluster.name = "racloop-cluster"
		grails.es.path.data = "${System.getProperty('user.home')}/racloop-data/es/data"
		grails.es.path.work = "${System.getProperty('user.home')}/racloop-data/es/work"
		grails.es.path.logs = "${System.getProperty('user.home')}/racloop-data/es/logs"
		grails.es.path.plugin = "${System.getProperty('user.home')}/racloop-data/es/plugin"
		grails.es.discovery.type = "ec2"
		grails.es.discovery.zen.ping.multicast.enabled = false
		grails.es.discovery.ec2.groups = "sg-13e86476"
		grails.es.http.cors.enabled = true
		grails.es.http.cors.allow.origin = "*"
	}
	production {
		grails.serverURL="http://www.cabshare.in"
		grails.startup.elasticsearch.index.create = false
		grails.startup.sampleUsers.create = false
		grails.startup.masterData.places.create = false
		grails.startup.sampleData.create = false
		grails.enable.delete.all = true
		grails.delete.user.password = "s3cr3t"
		
		//grails.serverURL = "http://awseb-e-r-AWSEBLoa-14MW1J02IWQX1-349308203.ap-southeast-1.elb.amazonaws.com"
		grails.plugin.facebooksdk.app.id = 393926260773644
		grails.plugin.facebooksdk.app.permissions = ['email','read_stream','user_birthday']
		grails.plugin.facebooksdk.app.secret = 'ac2825df67c12aaf2f0d0816e958c60b'
		grails.sms.enable=true
		grails.email.enable=true
		
		grails.activemq.data.dir = "/var/cache/tomcat7/activemq/data"
		
		grails.es.cluster.name = "racloop-cluster"
		grails.es.client.mode = true
		grails.es.path.data = "/var/cache/tomcat7/es/data"
		grails.es.path.work = "/var/cache/tomcat7/es/work"
		grails.es.path.logs = "/var/cache/tomcat7/es/logs"
		grails.es.path.plugin = "/var/cache/tomcat7/es/plugin"
		grails.es.discovery.type = "ec2"
		grails.es.discovery.zen.ping.multicast.enabled = false
		grails.es.discovery.ec2.groups = "sg-13e86476"
		grails.es.http.cors.enabled = true
		grails.es.http.cors.allow.origin = "*"
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
		from = "Cab Share <admin@cabshare.in>"
		host = "email-smtp.us-east-1.amazonaws.com"
		port = 587
		username = "AKIAILEU3VLFSXHCEVDA"
		password = "ArfdTbe4x9x2DwfDLm+5XsZV835E7/DBpuQednR2RbtR"
		props = [
				"mail.transport.protocol": "smtp",
				"mail.smtp.port": 587,
				"mail.smtp.auth":"true", 
                "mail.smtp.starttls.enable":"true", 
                "mail.smtp.starttls.required":"true"]
	}
}

// Login to account - http://www.smsgatewaycenter.com/login
sms {
	url = "http://www.smsgatewaycenter.com/library/send_sms_2.php"
	mask = "CABSHR"
	username = "cabshare"
	password = "eC4yrnzu"
	templates {
		verification = 'Your verification code for www.cabshare.in is $verificationCode'
		newRequest = 'There is new cab share request against your journey on $journeyDate by user $name'
		acceptRequest = 'Your cab share request for journey on $journeyDate has been ACCEPTED by user $name. Mobile : $mobile'
		rejectRequest = 'Your cab share request for journey on $journeyDate has been REJECTED by user $name.'
		cancelRequest = 'Your cab share request for journey on $journeyDate has been CANCELLED by user $name.'
		sos = '${name} is in danger. Please contact immediately. If unreachable please call police. Location: ${lat}, ${lng}'
		sosUser = 'You have raised SOS alert. If you dont need emergency assistance, Please cancel it on app home screen.'
		sosAdmin = '${name} | ${mobile} | ${email} | ${emergencyContactOne} | ${emergencyContactTwo} | ${lat} | ${lng} | ${journeyIds}'
		newPassword = 'Your new password for www.racloop.com is ${newPassword}'
	}
}

// log4j configuration
log4j = {
	// Example of changing the log pattern for the default console appender:
	//

	info "grails.app"

	appenders {
		console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
		file name: 'file', file: "${System.getProperty('user.home')}/racloop-data/logs/tomcat-racloop.log"
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