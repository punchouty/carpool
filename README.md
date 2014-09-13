carpool
=======

Amazon Deploy
-------------

Changes in code base

* Bump up the version in application.properties in grails application root folder
* Edit active-mq path in /conf/spring/resources.groovy 
'''
amq.broker(xmlns:"http://activemq.apache.org/schema/core",
	brokerName:"localhost",
	dataDirectory:"/data/activemq" ){
		amq.transportConnectors{
			amq.transportConnector(name:"vm", uri:"vm://localhost" )
		}
	}
'''
* Edit elasticserach file in /conf/elasticsearch.yml 
'''
path.data: /data/es/data
path.work: /data/es/work
path.logs: /data/es/logs
path.plugins: /data/plugins
'''
* Create war file - grails prod war
 

Deployment
* Login to Amazon - https://037732290427.signin.aws.amazon.com/console with rajan/P@swword
* Click on Services > Elastic Beanstalk 
* In _All Application_ click on racloop > Click on _Upload and Deploy_
* Upload the was created in previous step

