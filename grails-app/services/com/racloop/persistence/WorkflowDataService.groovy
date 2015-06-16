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
//				journeyDataService.updateElasticsearchForPassangeCountIfRequired(otherJourney.id, otherJourney.numberOfCopassengers)
				journeyDataService.updateElasticsearchForPassangeCountIfRequired(thirdJourney.id, thirdJourney.numberOfCopassengers)
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
//				journeyDataService.updateElasticsearchForPassangeCountIfRequired(requesterJourney.id, requesterJourney.numberOfCopassengers)
				journeyDataService.updateElasticsearchForPassangeCountIfRequired(otherJourney.id, otherJourney.numberOfCopassengers)
				journeyDataService.updateElasticsearchForPassangeCountIfRequired(thirdJourney.id, thirdJourney.numberOfCopassengers)
			}
			saveJourneys(requesterJourney, otherJourney, thirdJourney)
		}
		else {
			requesterJourney.addPairIdToJourney(journeyPair.getId())
			requesterJourney.incrementNumberOfCopassengers()
			otherJourney.addPairIdToJourney(journeyPair.getId())
			otherJourney.incrementNumberOfCopassengers()
			journeyDataService.makeJourneyNonSearchable(requesterJourneyId)
//			journeyDataService.updateElasticsearchForPassangeCountIfRequired(requesterJourney.id, requesterJourney.numberOfCopassengers)
			journeyDataService.updateElasticsearchForPassangeCountIfRequired(otherJourney.id, otherJourney.numberOfCopassengers)
			saveJourneys(requesterJourney, otherJourney)
		}
		
		sendNotificationForWorkflowStateChange(requesterJourneyId, otherJourneyId, WorkflowStatus.REQUESTED.getStatus())
	}
	
	
	private Journey findThirdJourneyAvailableForPairing(Journey journey){
		def thirdJourneyId =[] as Set
		def Journey thirdJourney = null
		if(journey.getJourneyPairIds()){
			List journeyPairsFromRequest = journeyPairDataService.findPairsByIds(journey.getJourneyPairIds())
			for (JourneyPair pair : journeyPairsFromRequest){
				if(shouldBeIncludedForPairing(pair)) {
					thirdJourneyId << findTheOtherJourneyId(pair,journey.getId())
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
		String myJourneyId = pairToBeAccepted.getRecieverJourneyId()
		String otherJourneyId = pairToBeAccepted.getInitiatorJourneyId()
		pairToBeAccepted.setInitiatorStatus(WorkflowStatus.ACCEPTED.getStatus())
		pairToBeAccepted.setRecieverStatus(WorkflowStatus.ACCEPTED.getStatus())
		journeyPairDataService.saveJourneyPair(pairToBeAccepted)
		sendNotificationForWorkflowStateChange(myJourneyId,otherJourneyId, WorkflowStatus.ACCEPTED.getStatus())
	}

	def rejectRequest(String journeyPairId){
		JourneyPair pairToBeRejected = journeyPairDataService.findPairById(journeyPairId)
		Journey journeyToBeRejected = journeyDataService.findJourney(pairToBeRejected.getInitiatorJourneyId())
		Journey myJourney = journeyDataService.findJourney(pairToBeRejected.getRecieverJourneyId())
		
		pairToBeRejected.setInitiatorStatus(WorkflowStatus.REJECTED.getStatus())
		pairToBeRejected.setRecieverStatus(WorkflowStatus.REJECTED_BY_ME.getStatus())
		journeyPairDataService.saveJourneyPair(pairToBeRejected)
		myJourney.decrementNumberOfCopassengers()
		journeyToBeRejected.decrementNumberOfCopassengers()
		
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		JourneyPair otherPair = findOtherPairToBeUpdated(journeyPairs, journeyPairId)
		if(otherPair){
			updateThirdJourneyAsForceCancelled(otherPair, myJourney.getId())
			myJourney.decrementNumberOfCopassengers()
			
		}
		else {
			journeyPairs = journeyPairDataService.findPairsByIds(journeyToBeRejected.getJourneyPairIds())
			otherPair = findOtherPairToBeUpdated(journeyPairs, journeyPairId)
			if(otherPair){
				updateThirdJourneyAsForceCancelled(otherPair, journeyToBeRejected.getId())
				journeyToBeRejected.decrementNumberOfCopassengers()
				
			}
		}
		saveJourneys(myJourney,journeyToBeRejected)
		if(journeyToBeRejected.getNumberOfCopassengers()<1){
			journeyDataService.makeJourneySearchable(journeyToBeRejected)
		}
		journeyDataService.makeJourneySearchable(myJourney)
		journeyDataService.updateElasticsearchForPassangeCountIfRequired(journeyToBeRejected.id, journeyToBeRejected.numberOfCopassengers)
		sendNotificationForWorkflowStateChange(myJourney.getId(), journeyToBeRejected.getId(), WorkflowStatus.REJECTED.getStatus())
	}
	
	
	
	def cancelMyJourney(String myJourneyId){
		boolean isPrimary = true
		String otherJourneyId = null;
		Journey otherJourney = null
		Journey myJourney = journeyDataService.findJourney(myJourneyId);
		myJourney.setStatusAsParent(WorkflowStatus.CANCELLED.getStatus());
		journeyDataService.makeJourneyNonSearchable(myJourneyId)
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		for(JourneyPair pair : journeyPairs){
			if(!WorkflowStatus.canBeIgnored(pair.getRecieverStatus())) {
				myJourney.decrementNumberOfCopassengers()
				otherJourneyId = findTheOtherJourneyId(pair, myJourneyId)
				otherJourney = journeyDataService.findJourney(otherJourneyId)
				otherJourney.decrementNumberOfCopassengers();
				if(WorkflowStatus.isIndirectStatus(pair.getRecieverStatus())){
					isPrimary = false
					pair.setInitiatorStatus(WorkflowStatus.FORCED_CANCELLED.getStatus())
					pair.setRecieverStatus(WorkflowStatus.FORCED_CANCELLED.getStatus())
					sendNotificationForWorkflowStateChange(myJourneyId, otherJourneyId, WorkflowStatus.FORCED_CANCELLED.getStatus())
				}
				else {
					if(pair.getInitiatorJourneyId().equals(myJourneyId)){
						pair.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
						pair.setRecieverStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
					}
					else {
						pair.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
						pair.setRecieverStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
					}

					sendNotificationForWorkflowStateChange(myJourneyId, otherJourneyId, WorkflowStatus.CANCELLED.getStatus())
				}
				journeyPairDataService.saveJourneyPair(pair)

				saveJourneys(otherJourney);
				//saveJourneys(myJourney);
				if(otherJourney.getNumberOfCopassengers()<1){
					journeyDataService.makeJourneySearchable(otherJourney)
				}

			}
		}
		saveJourneys(myJourney);
		if(otherJourneyId != null && isPrimary){
			journeyPairs = journeyPairDataService.findPairsByIds(otherJourney.getJourneyPairIds())
			for(JourneyPair pair : journeyPairs){
				if(!WorkflowStatus.canBeIgnored(pair.getRecieverStatus())) {
					updateThirdJourneyAsForceCancelled(pair,otherJourneyId)
					otherJourney.decrementNumberOfCopassengers()
					if(otherJourney.getNumberOfCopassengers() < 1){
						journeyDataService.makeJourneySearchable(otherJourney)
					}
					saveJourneys(otherJourney)
					
				}
			}
			journeyDataService.updateElasticsearchForPassangeCountIfRequired(otherJourney.id, otherJourney.numberOfCopassengers)
			sendNotificationForWorkflowStateChange(myJourneyId, otherJourneyId, WorkflowStatus.CANCELLED.getStatus())
		}
	}
	
	def cancelMyRequestOld(String journeyPairId, String myJourneyId){
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
				journeyDataService.updateElasticsearchForPassangeCountIfRequired(otherJourney.id, otherJourney.numberOfCopassengers)
			}

		}
		journeyDataService.updateElasticsearchForPassangeCountIfRequired(myJourney.id, myJourney.numberOfCopassengers)
	}
	
	def cancelMyRequest(String journeyPairId, String myJourneyId){
		String thirdJourneyId = null;
		Journey thirdJourney = null;
		JourneyPair pairTobeCancelled = journeyPairDataService.findPairById(journeyPairId)
		String otherJourneyId = findTheOtherJourneyId(pairTobeCancelled, myJourneyId)
		Journey myJourney = journeyDataService.findJourney(myJourneyId);
		Journey otherJourney = journeyDataService.findJourney(otherJourneyId);
		
		if(pairTobeCancelled.getInitiatorJourneyId().equals(myJourneyId)){
			pairTobeCancelled.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
			pairTobeCancelled.setRecieverStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
			sendNotificationForWorkflowStateChange(myJourneyId, pairTobeCancelled.getRecieverJourneyId(), WorkflowStatus.CANCELLED_BY_REQUESTER.getStatus())

		}
		else {
			pairTobeCancelled.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
			pairTobeCancelled.setRecieverStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
			sendNotificationForWorkflowStateChange(pairTobeCancelled.getInitiatorJourneyId(), myJourneyId, WorkflowStatus.CANCELLED.getStatus())
		}
		myJourney.decrementNumberOfCopassengers()
		otherJourney.decrementNumberOfCopassengers()
		journeyPairDataService.saveJourneyPair(pairTobeCancelled)
		
		List journeyPairs = journeyPairDataService.findPairsByIds(myJourney.getJourneyPairIds())
		JourneyPair otherPair = findOtherPairToBeUpdated(journeyPairs, journeyPairId)
		if(otherPair){
			updateThirdJourneyAsForceCancelled(otherPair, myJourneyId)
			myJourney.decrementNumberOfCopassengers()
			
		}
		else {
			journeyPairs = journeyPairDataService.findPairsByIds(otherJourney.getJourneyPairIds())
			otherPair = findOtherPairToBeUpdated(journeyPairs, journeyPairId)
			if(otherPair){
				updateThirdJourneyAsForceCancelled(otherPair, otherJourney.getId())
				otherJourney.decrementNumberOfCopassengers()
				
			}
		}
		
		saveJourneys(myJourney, otherJourney);
		if(otherJourney.getNumberOfCopassengers()<1){
			journeyDataService.makeJourneySearchable(otherJourney)
		}
		if(myJourney.getNumberOfCopassengers()<1){
			journeyDataService.makeJourneySearchable(myJourney)
		}
		journeyDataService.updateElasticsearchForPassangeCountIfRequired(myJourney.id, myJourney.numberOfCopassengers)
		journeyDataService.updateElasticsearchForPassangeCountIfRequired(otherJourney.id, otherJourney.numberOfCopassengers)
	}
	
	private void sendNotificationForWorkflowStateChange (String sourceId,String targetId, String state){
		def  messageMap =[(Constant.JOURNEY_WORKFLOW_SOURCE_ID):sourceId,(Constant.JOURNEY_WORKFLOW_TARGET_ID):targetId, (Constant.WORKFLOW_STATE_KEY):state]
		jmsService.send(queue: Constant.NOTIFICATION_WORKFLOW_STATE_CHANGE_QUEUE, messageMap)
	}
	
	private JourneyPair findOtherPairToBeUpdated(List journeyPairs, String existingPairId){
		JourneyPair pairToBeUpdated = null
		for(JourneyPair pair : journeyPairs){
			if(!pair.getId().equals(existingPairId) && !WorkflowStatus.canBeIgnored(pair.getInitiatorStatus()) && WorkflowStatus.isIndirectStatus(pair.getInitiatorStatus())){
				pairToBeUpdated = pair;
			}
		}
		
		return pairToBeUpdated
	}
	
	private String findTheOtherJourneyId(JourneyPair pair, String myJourneyId){
		String otherJourneyId = pair.getInitiatorJourneyId().equals(myJourneyId) ? pair.getRecieverJourneyId() : pair.getInitiatorJourneyId()
		return otherJourneyId
	}
	
	private updateThirdJourneyAsForceCancelled(JourneyPair otherPair, String journeyId){
		otherPair.setInitiatorStatus(WorkflowStatus.FORCED_CANCELLED.getStatus())
		otherPair.setRecieverStatus(WorkflowStatus.FORCED_CANCELLED.getStatus())
		journeyPairDataService.saveJourneyPair(otherPair)
		String thirdJourneyId = findTheOtherJourneyId(otherPair, journeyId)
		Journey thirdJourney = journeyDataService.findJourney(thirdJourneyId)
		thirdJourney.decrementNumberOfCopassengers();
		saveJourneys(thirdJourney);
		if(thirdJourney.getNumberOfCopassengers()<1){
			journeyDataService.makeJourneySearchable(thirdJourney)
		}
		journeyDataService.updateElasticsearchForPassangeCountIfRequired(thirdJourney.id, thirdJourney.numberOfCopassengers)
		sendNotificationForWorkflowStateChange(journeyId, thirdJourneyId, WorkflowStatus.FORCED_CANCELLED.getStatus())
	}
}
