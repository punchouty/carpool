package com.racloop.workflow

import grails.plugin.jms.JmsService
import grails.test.mixin.*
import grails.test.mixin.domain.DomainClassUnitTestMixin

import org.junit.*

import com.racloop.ElasticSearchService
import com.racloop.Journey
import com.racloop.JourneyRequestCommand
import com.racloop.User

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(JourneyWorkflowService)
@Mock([ElasticSearchService, Journey, JourneyWorkflow, User])
@TestMixin(DomainClassUnitTestMixin)
class JourneyWorkflowServiceTests {

    void testSaveJourneyAndInitiateWorkflow() {
		JourneyRequestCommand myRequest = new JourneyRequestCommand()
		myRequest.name = 'user1'
		myRequest.user = 'user1'
		myRequest.id=1
		myRequest.isSaved = false
		myRequest.dateOfJourneyString=""
		myRequest.fromPlace ="From"
		myRequest.toPlace ="To"
		myRequest.isDriver = false
		JourneyRequestCommand myMatch = new JourneyRequestCommand()
		myMatch.name = 'user2'
		myMatch.user = 'user1'
		myMatch.isSaved = true
		myMatch.id=2
		myMatch.dateOfJourneyString=""
		myMatch.fromPlace ="From"
		myMatch.toPlace ="To"
		myMatch.isDriver= true
		def mockJmsService = mockFor(JmsService)
		mockJmsService.demand.send(){->true}
		service.jmsService =  mockJmsService.createMock()
		def mockElasticSearchService = mockFor(ElasticSearchService)
		mockElasticSearchService.demand.indexWorkflow(){->true}
		service.elasticSearchService =  mockElasticSearchService.createMock()
        JourneyWorkflow workflow = service.initiateWorkflow(myRequest, myMatch)
		assert workflow != null
    }
	
}
