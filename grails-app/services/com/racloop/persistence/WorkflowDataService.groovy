package com.racloop.persistence

import grails.transaction.Transactional

import com.racloop.domain.Journey
import com.racloop.domain.JourneyPair
import com.racloop.util.date.DateUtil
import com.racloop.journey.workkflow.WorkflowDirection
import com.racloop.journey.workkflow.WorkflowStatus
import com.racloop.Constant

@Transactional
class WorkflowDataService {
	
	def journeyDataService;
	def journeyPairDataService
	def jmsService
	
	
	def requestJourney(String requesterJourneyId, String otherJourneyId) {
		Journey requesterJourney = journeyDataService.findJourney(requesterJourneyId)
		Journey otherJourney = journeyDataService.findJourney(otherJourneyId)
		
		JourneyPair journeyPair = new JourneyPair()
		journeyPair.setInitiatorJourneyId(requesterJourneyId)
		journeyPair.setInitiatorDirection(WorkflowDirection.OUTGOING.getDirection())
		journeyPair.setInitiatorStatus(WorkflowStatus.REQUESTED.getStatus())
		journeyPair.setRecieverJourneyId(otherJourneyId)
		journeyPair.setRecieverDirection(WorkflowDirection.INCOMING.getDirection())
		journeyPair.setRecieverStatus(WorkflowStatus.REQUEST_RECIEVED.getStatus())
		
		journeyPairDataService.createJourneyPair(journeyPair)
		
		boolean anotherRequestExistsForRequester = false
		
		Journey thirdJourney = findThirdJourneyAvailableForPairing(requesterJourney)
		if(thirdJourney){
			anotherRequestExistsForRequester = false
		}
		else {
			thirdJourney = findThirdJourneyAvailableForPairing(otherJourney)
		}
		if(thirdJourney){
			JourneyPair thirdJourneyPair = new JourneyPair()
			if(anotherRequestExistsForRequester){
				thirdJourneyPair.setInitiatorJourneyId(thirdJourney.getId())
				thirdJourneyPair.setInitiatorDirection(WorkflowDirection.FORCED_OUTGOING.getDirection())
				thirdJourneyPair.setInitiatorStatus(WorkflowStatus.NA.getStatus())
				thirdJourneyPair.setRecieverJourneyId(otherJourneyId)
				thirdJourneyPair.setRecieverDirection(WorkflowDirection.FORCED_INCOMING.getDirection())
				thirdJourneyPair.setRecieverStatus(WorkflowStatus.NA.getStatus())
				journeyPairDataService.createJourneyPair(thirdJourneyPair)
				requesterJourney.addPairIdToJourney(journeyPair.getId())
				requesterJourney.incrementNumberOfCopassengers();
				otherJourney.addPairIdToJourney(journeyPair.getId(),thirdJourneyPair.getId())
				otherJourney.incrementNumberOfCopassengers(2)
				thirdJourney.addPairIdToJourney(thirdJourneyPair.getId())
				thirdJourney.incrementNumberOfCopassengers()
			}
			else {
				thirdJourneyPair.setInitiatorJourneyId(requesterJourneyId)
				thirdJourneyPair.setInitiatorDirection(WorkflowDirection.FORCED_OUTGOING.getDirection())
				thirdJourneyPair.setInitiatorStatus(WorkflowStatus.NA.getStatus())
				thirdJourneyPair.setRecieverJourneyId(thirdJourney.getId())
				thirdJourneyPair.setRecieverDirection(WorkflowDirection.FORCED_INCOMING.getDirection())
				thirdJourneyPair.setRecieverStatus(WorkflowStatus.NA.getStatus())
				journeyPairDataService.createJourneyPair(thirdJourneyPair)
				
				requesterJourney.addPairIdToJourney(journeyPair.getId(), thirdJourneyPair.getId())
				requesterJourney.incrementNumberOfCopassengers(2)
				otherJourney.addPairIdToJourney(journeyPair.getId())
				otherJourney.incrementNumberOfCopassengers()
				thirdJourney.addPairIdToJourney(thirdJourneyPair.getId())
				thirdJourney.incrementNumberOfCopassengers()
			}
			saveJourneys(requesterJourney, otherJourney, thirdJourney)
		}
		else {
			requesterJourney.addPairIdToJourney(journeyPair.getId())
			requesterJourney.incrementNumberOfCopassengers()
			otherJourney.addPairIdToJourney(journeyPair.getId())
			otherJourney.incrementNumberOfCopassengers()
			saveJourneys(requesterJourney, otherJourney)
		}
		
		sendNotificationForWorkflowStateChange(requesterJourneyId, otherJourneyId, WorkflowStatus.REQUESTED.getStatus())
	}
	
	
	private Journey findThirdJourneyAvailableForPairing(Journey journey){
		def thirdJourneyId =[] as Set
		def Journey thirdJourney = null
		if(journey.getJourneyPairIds()){
			List journeyPairsFromRequest = journeyPairDataService.findPairsByIds(journey.getJourneyPairIds())
			journeyPairsFromRequest.each {it->
				if(shouldBeIncludedForPairing(it)){
					if(it.initiatorJourneyId.equals(journey.getId())){
						thirdJourneyId<<it.recieverJourneyId
					}
					else {
						thirdJourneyId<<it.initiatorJourneyId
					}
				}
			}
			if(thirdJourneyId.size()>1){
				throw new RuntimeException("Somethig is not right. Requester already has more than one 1 journey pairs {requestJourney}")
			}
		}
		if(thirdJourneyId){
			thirdJourney = journeyDataService.findJourney(thirdJourneyId.toList().first())
		}
		return thirdJourney
	}
	
	private boolean shouldBeIncludedForPairing(JourneyPair journeyPair){
		return !WorkflowStatus.canBeIgnored(journeyPair.getInitiatorStatus())
	}

	private saveJourneys(Journey ... journeys){
		journeys.each {it ->
			journeyDataService.saveJourney(it)
		}
	}

	def acceptRequest(String journeyIdToBeAccepted, String myJourneyId) {
		Journey journeyToBeAccepted = journeyDataService.findJourney(journeyIdToBeAccepted)
		Journey myJourney = journeyDataService.findJourney(myJourneyId)
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		JourneyPair pairToBeAccepted = null
		for(JourneyPair pair : journeyPairs){
			if(pair.getInitiatorJourneyId().equals(journeyIdToBeAccepted) && pair.getRecieverJourneyId().equals(myJourneyId)){
				pairToBeAccepted = pair;
				break;
			}
		}
		pairToBeAccepted.setInitiatorStatus(WorkflowStatus.ACCEPTED.getStatus())
		pairToBeAccepted.setRecieverStatus(WorkflowStatus.ACCEPTED.getStatus())
		journeyPairDataService.saveJourneyPair(pairToBeAccepted)
		
		sendNotificationForWorkflowStateChange(myJourneyId, journeyIdToBeAccepted, WorkflowStatus.ACCEPTED.getStatus())
	}

	def rejectRequest(String journeyIdToBeRejected, String myJourneyId){
		Journey journeyToBeRejected = journeyDataService.findJourney(journeyIdToBeRejected)
		Journey myJourney = journeyDataService.findJourney(myJourneyId)
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		JourneyPair pairToBeRejected = null
		for(JourneyPair pair : journeyPairs){
			if(pair.getInitiatorJourneyId().equals(journeyIdToBeRejected) && pair.getRecieverJourneyId().equals(myJourneyId)){
				pairToBeRejected = pair;
				break;
			}
		}
		pairToBeRejected.setInitiatorStatus(WorkflowStatus.REJECTED.getStatus())
		pairToBeRejected.setRecieverStatus(WorkflowStatus.REJECTED.getStatus())
		journeyPairDataService.saveJourneyPair(pairToBeAccepted)
		myJourney.decrementNumberOfCopassengers()
		journeyToBeRejected.decrementNumberOfCopassengers()
		saveJourneys(myJourney,journeyToBeRejected)
		//TODO:  Send rejected journey to ES if required
		sendNotificationForWorkflowStateChange(myJourneyId, journeyToBeRejected, WorkflowStatus.REJECTED.getStatus())
	}
	
	private void sendNotificationForWorkflowStateChange (String sourceId,String targetId, String state){
		def  messageMap =[(Constant.JOURNEY_WORKFLOW_SOURCE_ID):sourceId,(Constant.JOURNEY_WORKFLOW_TARGET_ID):targetId, (Constant.WORKFLOW_STATE_KEY):state]
		jmsService.send(queue: Constant.NOTIFICATION_WORKFLOW_STATE_CHANGE_QUEUE, messageMap)
	}
}
