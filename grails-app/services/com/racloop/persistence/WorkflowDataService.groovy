package com.racloop.persistence

import grails.transaction.Transactional

import com.racloop.domain.Journey
import com.racloop.domain.JourneyPair
import com.racloop.journey.workkflow.WorkflowDirection
import com.racloop.journey.workkflow.WorkflowStatus
import com.racloop.journey.workkflow.WorkflowAction
import com.racloop.Constant

@Transactional
class WorkflowDataService {
	
	def journeyDataService;
	def journeyPairDataService
	def jmsService
	
	def requestJourneyAndSave(Journey unsavedJourney, String otherJourneyId) {
		journeyDataService.saveJourney(unsavedJourney);
		requestJourney(unsavedJourney.getId(), otherJourneyId);
		unsavedJourney
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
		Journey myJourney = journeyDataService.findJourney(pairToBeAccepted.getRecieverJourneyId())
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		for (JourneyPair pair : journeyPairs){
			if(pair.getRecieverJourneyId().equals(myJourney.getId())){
				List actions= WorkflowStatus.fromString(pair.getRecieverStatus()).actions.toList()
				if(actions.contains(WorkflowAction.ACCEPT.action)){
					pair.setInitiatorStatus(WorkflowStatus.ACCEPTED.getStatus())
					pair.setRecieverStatus(WorkflowStatus.ACCEPTED.getStatus())
					journeyPairDataService.saveJourneyPair(pair)
					sendNotificationForWorkflowStateChange(myJourney.getId(), pair.getInitiatorJourneyId(), WorkflowStatus.ACCEPTED.getStatus())
				}
			}
		}
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
		Journey thirdJourney = findThirdJourneyAvailableForPairing(myJourney)
		if(thirdJourney){
			journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
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
			myJourney.decrementNumberOfCopassengers()
			saveJourneys(thirdJourney)
			sendNotificationForWorkflowStateChange(myJourney.getId(), thirdJourney.getId(), WorkflowStatus.REJECTED.getStatus())
		}
		saveJourneys(myJourney,journeyToBeRejected)
		if(journeyToBeRejected.getNumberOfCopassengers()<1){
			journeyDataService.makeJourneySearchable(journeyToBeRejected)
		}
		journeyDataService.makeJourneySearchable(myJourney)
		sendNotificationForWorkflowStateChange(myJourney.getId(), journeyToBeRejected.getId(), WorkflowStatus.REJECTED.getStatus())
	}
	
	
	
	def cancelMyJourney(String myJourneyId){
		String otherJourneyId = null;
		Journey myJourney = journeyDataService.findJourney(myJourneyId);
		myJourney.setStatusAsParent(WorkflowStatus.CANCELLED.getStatus());
		journeyDataService.makeJourneyNonSearchable(myJourneyId)
		saveJourneys(myJourney);
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		for(JourneyPair pair : journeyPairs){
			
			if(pair.getInitiatorJourneyId().equals(myJourneyId)){
				pair.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
				pair.setRecieverStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
			}
			else {
				pair.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
				pair.setRecieverStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
			}
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
	
	def cancelMyRequest(String journeyPairId, String myJourneyId){
		String otherJourneyId = null;
		Journey myJourney = journeyDataService.findJourney(myJourneyId);
		journeyDataService.makeJourneySearchable(myJourney)
		myJourney.decrementNumberOfCopassengers()
		saveJourneys(myJourney);
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		for(JourneyPair pair : journeyPairs){
			List actions= WorkflowStatus.fromString(pair.getRecieverStatus()).actions.toList()
			if(actions){
				if(pair.getInitiatorJourneyId().equals(myJourneyId)){
					pair.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
					pair.setRecieverStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
					sendNotificationForWorkflowStateChange(myJourneyId, pair.getRecieverJourneyId(), WorkflowStatus.CANCELLED_BY_REQUESTER.getStatus())

				}
				else {
					pair.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
					pair.setRecieverStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
					sendNotificationForWorkflowStateChange(pair.getInitiatorJourneyId(), myJourneyId, WorkflowStatus.CANCELLED.getStatus())
				}
				journeyPairDataService.saveJourneyPair(pair)
				otherJourneyId = pair.getInitiatorJourneyId().equals(myJourneyId) ? pair.getRecieverJourneyId() : pair.getInitiatorJourneyId()
				Journey otherJourney = journeyDataService.findJourney(otherJourneyId)
				otherJourney.decrementNumberOfCopassengers();
				saveJourneys(otherJourney);
				//saveJourneys(myJourney);
				if(otherJourney.getNumberOfCopassengers()<1){
					journeyDataService.makeJourneySearchable(otherJourney)
				}
			}

		}
	}
	
	private void sendNotificationForWorkflowStateChange (String sourceId,String targetId, String state){
		def  messageMap =[(Constant.JOURNEY_WORKFLOW_SOURCE_ID):sourceId,(Constant.JOURNEY_WORKFLOW_TARGET_ID):targetId, (Constant.WORKFLOW_STATE_KEY):state]
		jmsService.send(queue: Constant.NOTIFICATION_WORKFLOW_STATE_CHANGE_QUEUE, messageMap)
	}
}
