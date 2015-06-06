package com.racloop.persistence

import grails.transaction.Transactional

import com.racloop.domain.Journey
import com.racloop.domain.JourneyPair
import com.racloop.journey.workkflow.WorkflowDirection
import com.racloop.journey.workkflow.WorkflowStatus
import com.racloop.Constant

@Transactional
class WorkflowDataService {
	
	def journeyDataService;
	def journeyPairDataService
	def jmsService
	
	def requestJourneyAndSave(Journey unsavedJourney, String otherJourneyId) {
		journeyDataService.saveJourney(unsavedJourney);
		requestJourney(unsavedJourney.getId(), otherJourneyId);
	}
	
	def requestJourney(String requesterJourneyId, String otherJourneyId) {
		Journey requesterJourney = journeyDataService.findJourney(requesterJourneyId)
		Journey otherJourney = journeyDataService.findJourney(otherJourneyId)
		boolean anotherRequestExistsForRequester = false
		Journey thirdJourney = findThirdJourneyAvailableForPairing(requesterJourney)
		if(thirdJourney){
			anotherRequestExistsForRequester = true
		}
		else {
			thirdJourney = findThirdJourneyAvailableForPairing(otherJourney)
		}
		JourneyPair journeyPair = new JourneyPair()
		journeyPair.setInitiatorJourneyId(requesterJourneyId)
		journeyPair.setInitiatorDirection(WorkflowDirection.OUTGOING.getDirection())
		journeyPair.setInitiatorStatus(WorkflowStatus.REQUESTED.getStatus())
		journeyPair.setRecieverJourneyId(otherJourneyId)
		journeyPair.setRecieverDirection(WorkflowDirection.INCOMING.getDirection())
		journeyPair.setRecieverStatus(WorkflowStatus.REQUEST_RECIEVED.getStatus())
		
		journeyPairDataService.createJourneyPair(journeyPair)
		if(thirdJourney){
			JourneyPair thirdJourneyPair = new JourneyPair()
			if(anotherRequestExistsForRequester){
				thirdJourneyPair.setInitiatorJourneyId(thirdJourney.getId())
				thirdJourneyPair.setInitiatorDirection(WorkflowDirection.FORCED_OUTGOING.getDirection())
				thirdJourneyPair.setInitiatorStatus(WorkflowStatus.DELEGATED.getStatus())
				thirdJourneyPair.setRecieverJourneyId(otherJourneyId)
				thirdJourneyPair.setRecieverDirection(WorkflowDirection.FORCED_INCOMING.getDirection())
				thirdJourneyPair.setRecieverStatus(WorkflowStatus.INHERITED.getStatus())
				journeyPairDataService.createJourneyPair(thirdJourneyPair)
				requesterJourney.addPairIdToJourney(journeyPair.getId())
				requesterJourney.incrementNumberOfCopassengers();
				otherJourney.addPairIdToJourney(journeyPair.getId(),thirdJourneyPair.getId())
				otherJourney.incrementNumberOfCopassengers(2)
				thirdJourney.addPairIdToJourney(thirdJourneyPair.getId())
				thirdJourney.incrementNumberOfCopassengers()
				journeyDataService.makeJourneyNonSearchable(otherJourney.getId())
			}
			else {
				thirdJourneyPair.setInitiatorJourneyId(requesterJourneyId)
				thirdJourneyPair.setInitiatorDirection(WorkflowDirection.FORCED_OUTGOING.getDirection())
				thirdJourneyPair.setInitiatorStatus(WorkflowStatus.DELEGATED.getStatus())
				thirdJourneyPair.setRecieverJourneyId(thirdJourney.getId())
				thirdJourneyPair.setRecieverDirection(WorkflowDirection.FORCED_INCOMING.getDirection())
				thirdJourneyPair.setRecieverStatus(WorkflowStatus.INHERITED.getStatus())
				journeyPairDataService.createJourneyPair(thirdJourneyPair)
				
				requesterJourney.addPairIdToJourney(journeyPair.getId(), thirdJourneyPair.getId())
				requesterJourney.incrementNumberOfCopassengers(2)
				otherJourney.addPairIdToJourney(journeyPair.getId())
				otherJourney.incrementNumberOfCopassengers()
				thirdJourney.addPairIdToJourney(thirdJourneyPair.getId())
				thirdJourney.incrementNumberOfCopassengers()
				journeyDataService.makeJourneyNonSearchable(requesterJourneyId)
			}
			saveJourneys(requesterJourney, otherJourney, thirdJourney)
		}
		else {
			requesterJourney.addPairIdToJourney(journeyPair.getId())
			requesterJourney.incrementNumberOfCopassengers()
			otherJourney.addPairIdToJourney(journeyPair.getId())
			otherJourney.incrementNumberOfCopassengers()
			journeyDataService.makeJourneyNonSearchable(requesterJourneyId)
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
				throw new RuntimeException("Somethig is not right. Requester already has more than one active journey pairs ${journey}")
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

	def acceptRequest(String journeyPairId) {
		JourneyPair pairToBeAccepted = journeyPairDataService.findPairById(journeyPairId)
		Journey journeyToBeAccepted = journeyDataService.findJourney(pairToBeAccepted.getInitiatorJourneyId())
		Journey myJourney = journeyDataService.findJourney(pairToBeAccepted.getRecieverJourneyId())
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		
		pairToBeAccepted.setInitiatorStatus(WorkflowStatus.ACCEPTED.getStatus())
		pairToBeAccepted.setRecieverStatus(WorkflowStatus.ACCEPTED.getStatus())
		journeyPairDataService.saveJourneyPair(pairToBeAccepted)
		
		sendNotificationForWorkflowStateChange(myJourney.getId(), journeyToBeAccepted.getId(), WorkflowStatus.ACCEPTED.getStatus())
	}

	def rejectRequest(String journeyPairId){
		JourneyPair pairToBeRejected = journeyPairDataService.findPairById(journeyPairId)
		Journey journeyToBeRejected = journeyDataService.findJourney(pairToBeRejected.getInitiatorJourneyId())
		Journey myJourney = journeyDataService.findJourney(pairToBeRejected.getRecieverJourneyId())
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		
		pairToBeRejected.setInitiatorStatus(WorkflowStatus.REJECTED.getStatus())
		pairToBeRejected.setRecieverStatus(WorkflowStatus.REJECTED.getStatus())
		journeyPairDataService.saveJourneyPair(pairToBeRejected)
		myJourney.decrementNumberOfCopassengers()
		journeyToBeRejected.decrementNumberOfCopassengers()
		Journey thirdJourney = findThirdJourneyAvailableForPairing(journeyToBeRejected)
		if(thirdJourney){
			journeyPairs = journeyPairDataService.findPairsByIds(journeyToBeRejected.getJourneyPairIds())
			pairToBeRejected = null
			for(JourneyPair pair : journeyPairs){
				if(pair.getInitiatorJourneyId().equals(thirdJourney.getId()) || pair.getRecieverJourneyId().equals(thirdJourney.getId())){
					pairToBeRejected = pair;
					break;
				}
			}
			pairToBeRejected.setInitiatorStatus(WorkflowStatus.REJECTED.getStatus())
			pairToBeRejected.setRecieverStatus(WorkflowStatus.REJECTED.getStatus())
			journeyPairDataService.saveJourneyPair(pairToBeRejected)
			thirdJourney.decrementNumberOfCopassengers()
			journeyToBeRejected.decrementNumberOfCopassengers()
			saveJourneys(thirdJourney)
		}
		saveJourneys(myJourney,journeyToBeRejected)
		if(journeyToBeRejected.getNumberOfCopassengers()<1){
			journeyDataService.makeJourneySearchable(journeyToBeRejected)
		}
		sendNotificationForWorkflowStateChange(myJourney.getId(), journeyToBeRejected.getId(), WorkflowStatus.REJECTED.getStatus())
	}
	
	def cancelRequest(String journeyPairId){
		JourneyPair pairToBeCancelled = journeyPairDataService.findPairById(journeyPairId)
		Journey journeyToBeCancelled = journeyDataService.findJourney(pairToBeCancelled.getInitiatorJourneyId())
		Journey myJourney = journeyDataService.findJourney(pairToBeCancelled.getRecieverJourneyId())
		
		pairToBeCancelled.setInitiatorStatus(WorkflowStatus.CANCELLED.getStatus())
		pairToBeCancelled.setRecieverStatus(WorkflowStatus.CANCELLED.getStatus())
		journeyPairDataService.saveJourneyPair(pairToBeCancelled)
		myJourney.decrementNumberOfCopassengers()
		journeyToBeCancelled.decrementNumberOfCopassengers()
		saveJourneys(myJourney,journeyToBeCancelled)
		if(journeyToBeCancelled.getNumberOfCopassengers()<1){
			journeyDataService.makeJourneySearchable(journeyToBeCancelled)
		}
		sendNotificationForWorkflowStateChange(myJourney.getId(), journeyToBeCancelled.getId(), WorkflowStatus.CANCELLED.getStatus())
	}
	
	private cancelMyRequest(String journeyPairId, String myJourneyId){
		boolean iAmRequesterForJourney = false
		Journey journeyToBeCancelled = null
		Journey myJourney = null
		JourneyPair pairToBeCancelled = journeyPairDataService.findPairById(journeyPairId)
		if (pairToBeCancelled.getInitiatorJourneyId().equals(myJourneyId)){
			iAmRequesterForJourney = true
			journeyToBeCancelled = journeyDataService.findJourney(pairToBeCancelled.getRecieverJourneyId())
			myJourney = journeyDataService.findJourney(pairToBeCancelled.getInitiatorJourneyId())
			pairToBeCancelled.setInitiatorStatus(WorkflowStatus.CANCELLED.getStatus())
			pairToBeCancelled.setRecieverStatus(WorkflowStatus.CANCELLED_BY_REQUESTER.getStatus())
		}
		else {
			journeyToBeCancelled = journeyDataService.findJourney(pairToBeCancelled.getInitiatorJourneyId())
			myJourney = journeyDataService.findJourney(pairToBeCancelled.getRecieverJourneyId())
			pairToBeCancelled.setInitiatorStatus(WorkflowStatus.CANCELLED.getStatus())
			pairToBeCancelled.setRecieverStatus(WorkflowStatus.CANCELLED_BY_REQUESTER.getStatus())
		}
		
		
		journeyPairDataService.saveJourneyPair(pairToBeCancelled)
		myJourney.decrementNumberOfCopassengers()
		journeyToBeCancelled.decrementNumberOfCopassengers()
		saveJourneys(myJourney,journeyToBeCancelled)
		if(journeyToBeCancelled.getNumberOfCopassengers()<1){
			journeyDataService.makeJourneySearchable(journeyToBeCancelled)
		}
		if(myJourney.getNumberOfCopassengers()<1){
			journeyDataService.makeJourneySearchable(myJourney)
		}
		sendNotificationForWorkflowStateChange(myJourney.getId(), journeyToBeCancelled.getId(), WorkflowStatus.CANCELLED.getStatus())
	}
	
	
	
	def cancelMyJourney(String myJourneyId){
		String otherJourneyId = null;
		Journey myJourney = journeyDataService.findJourney(myJourneyId);
		myJourney.setStatusAsParent(WorkflowStatus.CANCELLED.getStatus());
		journeyDataService.makeJourneyNonSearchable(myJourneyId)
		saveJourneys(myJourney);
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		for(JourneyPair pair : journeyPairs){
			pair.setInitiatorStatus(WorkflowStatus.CANCELLED.getStatus())
			pair.setRecieverStatus(WorkflowStatus.CANCELLED.getStatus())
			journeyPairDataService.saveJourneyPair(pair)
			otherJourneyId = pair.getInitiatorJourneyId().equals(myJourneyId) ? pair.getRecieverJourneyId() : pair.getInitiatorJourneyId()
			Journey otherJourney = journeyDataService.findJourney(otherJourneyId)
			otherJourney.decrementNumberOfCopassengers();
			saveJourneys(otherJourney);
			//saveJourneys(myJourney);
			if(otherJourney.getNumberOfCopassengers()<1){
				journeyDataService.makeJourneySearchable(otherJourney)
			}
			sendNotificationForWorkflowStateChange(myJourneyId, otherJourneyId, WorkflowStatus.CANCELLED.getStatus())
		}
	}
	
	private void sendNotificationForWorkflowStateChange (String sourceId,String targetId, String state){
		def  messageMap =[(Constant.JOURNEY_WORKFLOW_SOURCE_ID):sourceId,(Constant.JOURNEY_WORKFLOW_TARGET_ID):targetId, (Constant.WORKFLOW_STATE_KEY):state]
		jmsService.send(queue: Constant.NOTIFICATION_WORKFLOW_STATE_CHANGE_QUEUE, messageMap)
	}
}
