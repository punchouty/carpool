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
				tripDistance : journey.tripDistance,
				photoUrl : journey.photoUrl,
				journeyPairIds : journey.journeyPairIds,
				numberOfCopassengers : journey.numberOfCopassengers,
				isDummy : journey.isDummy,
				myStatus : journey.myStatus,
				myDirection : journey.myDirection,
				myActions : journey.myActions, 
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
					tripDistance : relatedJourney.tripDistance,
					photoUrl : relatedJourney.photoUrl,
					relatedJourneyPairIds : relatedJourney.journeyPairIds,
					numberOfCopassengers : relatedJourney.numberOfCopassengers,
					isDummy : relatedJourney.isDummy,
					myStatus : relatedJourney.myStatus,
					myDirection : relatedJourney.myDirection,
					myActions : relatedJourney.myActions ]
				}
				
			]
		}
		
	}

}