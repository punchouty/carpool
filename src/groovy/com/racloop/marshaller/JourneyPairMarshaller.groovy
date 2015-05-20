package com.racloop.marshaller

import grails.converters.JSON

import com.racloop.domain.Journey;
import com.racloop.domain.JourneyPair;
import com.racloop.workflow.JourneyWorkflow

class JourneyPairMarshaller {
	
	void register() {
		JSON.registerObjectMarshaller(JourneyPair) { JourneyPair journeyPair ->
			return [
				id : journeyPair.id,
				initiatorJourneyId : journeyPair.initiatorJourneyId,
				recieverJourneyId : journeyPair.recieverJourneyId,
				initiatorStatus : journeyPair.initiatorStatus,
				recieverStatus : journeyPair.recieverStatus,
				initiatorDirection : journeyPair.initiatorDirection,
				recieverDirection : journeyPair.recieverDirection
			]
		}
		
	}

}
