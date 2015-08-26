package com.racloop.marshaller

import grails.converters.JSON

import com.racloop.domain.Journey;
import com.racloop.workflow.JourneyWorkflow

class JourneyMarshaller {
	
	void register() {
		JSON.registerObjectMarshaller(Journey) { Journey journey ->
			return [
				id : journey.id,
				mobile : journey.mobile,
				dateOfJourney : journey.dateOfJourney,
				email : journey.email,
				name : journey.name,
				fromLatitude : journey.fromLatitude,
				fromLongitude : journey.fromLongitude,
				from : journey.from,
				toLatitude : journey.toLatitude,
				toLongitude : journey.toLongitude,
				to : journey.to,
				isMale : journey.isMale,
				isDriver : journey.isDriver,
				isTaxi : journey.isTaxi,
				tripDistance : journey.tripDistance,
				photoUrl : journey.photoUrl,
				journeyPairIds : journey.journeyPairIds,
				incomingJourneyPairIds : journey.incomingJourneyPairIds,
				outgoingJourneyPairIds : journey.outgoingJourneyPairIds,
				numberOfCopassengers : journey.numberOfCopassengers,
				isDummy : journey.isDummy,
				statusAsParent : journey.statusAsParent,
				myStatus : journey.myStatus,
				tripTimeInSeconds : journey.tripTimeInSeconds,
				myDirection : journey.myDirection,
				myActions : journey.myActions, 
				myPairId : journey.myPairId,
				isRecurring:journey.getIsRecurring(),
				journeyRecurrence:journey.journeyRecurrence,
				femaleOnlySearch : journey.femaleOnlySearch,
				//journeyPairs : journey.journeyPairs,
				relatedJourneys : journey.relatedJourneys.collect() { Journey relatedJourney ->
					[ id : relatedJourney.id,
					mobile : relatedJourney.mobile,
					dateOfJourney : relatedJourney.dateOfJourney,
					email : relatedJourney.email,
					name : relatedJourney.name,
					fromLatitude : relatedJourney.fromLatitude,
					fromLongitude : relatedJourney.fromLongitude,
					from : relatedJourney.from,
					toLatitude : relatedJourney.toLatitude,
					toLongitude : relatedJourney.toLongitude,
					to : relatedJourney.to,
					isMale : relatedJourney.isMale,
					isDriver : relatedJourney.isDriver,
					isTaxi : relatedJourney.isTaxi,
					tripDistance : relatedJourney.tripDistance,
					tripTimeInSeconds : journey.tripTimeInSeconds,
					photoUrl : relatedJourney.photoUrl,
					relatedJourneyPairIds : relatedJourney.journeyPairIds,
					numberOfCopassengers : relatedJourney.numberOfCopassengers,
					isDummy : relatedJourney.isDummy,
					myStatus : relatedJourney.myStatus,
					myDirection : relatedJourney.myDirection,
					myPairId : relatedJourney.myPairId,
					myActions : relatedJourney.myActions ]
				}
				
			]
		}
		
	}

}
