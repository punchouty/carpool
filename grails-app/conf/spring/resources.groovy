// Place your Spring DSL code here
import org.apache.activemq.ActiveMQConnectionFactory
import org.springframework.jms.connection.SingleConnectionFactory

import com.racloop.marshaller.CustomObjectMarshallers
import com.racloop.marshaller.UserMarshaller

beans = {

	xmlns amq:"http://activemq.apache.org/schema/core"

	amq.broker(xmlns:"http://activemq.apache.org/schema/core",
	brokerName:"localhost",
	dataDirectory:"~/data/activemq" ){


		amq.transportConnectors{
			amq.transportConnector(name:"vm", uri:"vm://localhost" )
		}
	}

	jmsConnectionFactory(SingleConnectionFactory) {
        targetConnectionFactory = { ActiveMQConnectionFactory cf ->
            brokerURL = 'vm://localhost?create=false&waitForStart=100'
        }
    }
	
	customObjectMarshallers( CustomObjectMarshallers ) {
		marshallers = [
				new UserMarshaller()
		]
	}
}
