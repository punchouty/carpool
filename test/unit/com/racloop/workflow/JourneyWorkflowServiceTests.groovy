package com.racloop.workflow



import grails.test.mixin.*
import grails.test.mixin.domain.DomainClassUnitTestMixin

import org.junit.*

import com.racloop.ElasticSearchService
import com.racloop.Journey
import com.racloop.JourneyManagerService
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
		myRequest.id=1
		myRequest.isSaved = false
		JourneyRequestCommand myMatch = new JourneyRequestCommand()
		myMatch.name = 'user2'
		myMatch.isSaved = true
		myMatch.id=2
		def mockJourneyManager = mockFor(JourneyManagerService)
		mockJourneyManager.demand.createJourney(){->true}
		service.journeyManagerService =  mockJourneyManager.createMock()
        JourneyWorkflow workflow = service.saveJourneyAndInitiateWorkflow(myRequest, myMatch)
		assert workflow != null
    }
	
}
