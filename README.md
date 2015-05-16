# Racloop Web Application

## Important TODO

1. change primary key to mobile instead of email. Implemented in server side need to do in client side.
2. Remove dependency on JourneyRequestCommand instead use Journey
3. Move User data to Dynamo DB
4. Relook at Edit Profile

## Amazon Deploy

### Changes in code base

1. Bump up the version in *application.properties* in grails application root folder
2. Edit active-mq path in /conf/spring/resources.groovy 

```groovy
amq.broker(xmlns:"http://activemq.apache.org/schema/core",
	brokerName:"localhost",
	dataDirectory:"/data/activemq" ){
		amq.transportConnectors{
			amq.transportConnector(name:"vm", uri:"vm://localhost" )
		}
	}
```

3 Edit elasticserach file in /conf/elasticsearch.yml 

```
path.data: /data/es/data
path.work: /data/es/work
path.logs: /data/es/logs
path.plugins: /data/plugins
```

4. Create war file - **grails prod war**
 

### Deployment
* Login to Amazon - https://037732290427.signin.aws.amazon.com/console with rajan/P@swword
* Click on Services > Elastic Beanstalk 
* In _All Application_ click on racloop > Click on _Upload and Deploy_
* Upload the was created in previous step

