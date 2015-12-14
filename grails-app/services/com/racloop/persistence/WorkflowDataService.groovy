package com.racloop.persistence

import grails.transaction.Transactional

import com.racloop.domain.Journey
import com.racloop.domain.JourneyPair
import com.racloop.journey.workkflow.WorkflowDirection
import com.racloop.journey.workkflow.WorkflowStatus
import com.racloop.journey.workkflow.WorkflowAction
import com.racloop.mobile.data.response.MobileResponse
import com.racloop.Constant
import com.racloop.GenericUtil;

@Transactional
class WorkflowDataService {
	
	def journeyDataService;
	def journeyPairDataService
	def jmsService
	def recurrenceJourneyService
	
	def requestJourneyAndSave(Journey unsavedJourney, String otherJourneyId) {
		journeyDataService.createJourney(unsavedJourney);
		requestJourney(unsavedJourney.getId(), otherJourneyId);
		unsavedJourney
	}
	
	
	def requestJourney(String requesterJourneyId, String otherJourneyId) {
		boolean isDummy = false
		Journey requesterJourney = journeyDataService.findChildJourneys(requesterJourneyId)
		Journey otherJourney = journeyDataService.findChildJourneys(otherJourneyId)
		if(!otherJourney){
			otherJourney = saveDummyJounrney(otherJourneyId)
			isDummy = true
		}
		validateIfJourneyIsNotAccepted(requesterJourney)
		validateIfJourneyIsNotAccepted(otherJourney)
		JourneyPair journeyPair = new JourneyPair()
		journeyPair.setInitiatorJourneyId(requesterJourneyId)
		journeyPair.setInitiatorDirection(WorkflowDirection.OUTGOING.getDirection())
		journeyPair.setInitiatorStatus(WorkflowStatus.REQUESTED.getStatus())
		journeyPair.setRecieverJourneyId(otherJourney.getId())
		journeyPair.setRecieverDirection(WorkflowDirection.INCOMING.getDirection())
		journeyPair.setRecieverStatus(WorkflowStatus.REQUEST_RECIEVED.getStatus())
		journeyPair.setIsDummy(isDummy)
		journeyPairDataService.createJourneyPair(journeyPair)
		requesterJourney.addPairIdToJourney(journeyPair.getId())
		otherJourney.addPairIdToJourney(journeyPair.getId())
		saveJourneys(requesterJourney, otherJourney)
		sendNotificationForWorkflowStateChange(requesterJourneyId, otherJourney.getId(), WorkflowStatus.REQUESTED.getStatus())
	}
	
	def requestJourneyAgain(String requesterJourneyId, String existingPairId){
		JourneyPair journeyPair = journeyPairDataService.findPairById(existingPairId)
		String otherJourneyId = findTheOtherJourneyId(journeyPair, requesterJourneyId)
		Journey requesterJourney = journeyDataService.findJourney(requesterJourneyId)
		Journey otherJourney = journeyDataService.findJourney(otherJourneyId)
		processInviteAgain(requesterJourney, otherJourney,journeyPair)
	}
	
	def inviteAgain(String otherJourneyId, String existingPairId) {
		JourneyPair journeyPair = journeyPairDataService.findPairById(existingPairId)
		String requesterJourneyId = findTheOtherJourneyId(journeyPair, otherJourneyId)
		Journey requesterJourney = journeyDataService.findJourney(requesterJourneyId)
		Journey otherJourney = journeyDataService.findJourney(otherJourneyId)
		processInviteAgain(requesterJourney, otherJourney,journeyPair)
	}
	
	private processInviteAgain(Journey requesterJourney, Journey otherJourney ,JourneyPair journeyPair) {
		validateIfJourneyIsNotAccepted(requesterJourney)
		validateIfJourneyIsNotAccepted(otherJourney)
		journeyPair.setInitiatorJourneyId(requesterJourney.getId())
		journeyPair.setInitiatorDirection(WorkflowDirection.OUTGOING.getDirection())
		journeyPair.setInitiatorStatus(WorkflowStatus.REQUESTED.getStatus())
		journeyPair.setRecieverJourneyId(otherJourney.getId())
		journeyPair.setRecieverDirection(WorkflowDirection.INCOMING.getDirection())
		journeyPair.setRecieverStatus(WorkflowStatus.REQUEST_RECIEVED.getStatus())
		journeyPair.setIsDummy(false)
		journeyPairDataService.saveJourneyPair(journeyPair)
		requesterJourney.addPairIdToJourney(journeyPair.getId())
		otherJourney.addPairIdToJourney(journeyPair.getId())
		saveJourneys(requesterJourney, otherJourney)
		sendNotificationForWorkflowStateChange(requesterJourney.getId(), otherJourney.getId(), WorkflowStatus.REQUESTED.getStatus())
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

	def String acceptRequest(String journeyPairId) {
		JourneyPair pairToBeAccepted = journeyPairDataService.findPairById(journeyPairId)
		String myJourneyId = pairToBeAccepted.getRecieverJourneyId()
		String otherJourneyId = pairToBeAccepted.getInitiatorJourneyId()
		pairToBeAccepted.setInitiatorStatus(WorkflowStatus.ACCEPTED.getStatus())
		pairToBeAccepted.setRecieverStatus(WorkflowStatus.ACCEPTED.getStatus())
		journeyPairDataService.saveJourneyPair(pairToBeAccepted)
		Journey myJourney = journeyDataService.findJourney(myJourneyId)
		invalidateAllRequestExceptAccepted(myJourney, pairToBeAccepted.getId())
		Journey otherJourney = journeyDataService.findJourney(otherJourneyId)
		invalidateAllRequestExceptAccepted(otherJourney, pairToBeAccepted.getId())
		//journeyDataService.makeJourneyNonSearchable(myJourneyId)
		myJourney.incrementNumberOfCopassengers()
		otherJourney.incrementNumberOfCopassengers()
		saveJourneys(myJourney,otherJourney)
		journeyDataService.updateElasticsearchForPassangeCountIfRequired(myJourney.id, myJourney.numberOfCopassengers)
		journeyDataService.makeJourneyNonSearchable(otherJourneyId)
		sendNotificationForWorkflowStateChange(myJourneyId,otherJourneyId, WorkflowStatus.ACCEPTED.getStatus())
		return myJourneyId
	}
	
	private invalidateAllRequestExceptAccepted(Journey journey, String pairToBeAccepted) {
		for(String pairId : journey.getJourneyPairIds()) {
			if(!pairId.equals(pairToBeAccepted)) {
				JourneyPair pair = journeyPairDataService.findPairById(pairId)
				if(shouldBeIncludedForPairing(pair)) {
					pair.setInitiatorStatus(WorkflowStatus.AUTO_REJECTED.getStatus())
					pair.setRecieverStatus(WorkflowStatus.AUTO_REJECTED.getStatus())
					journeyPairDataService.saveJourneyPair(pair)
					String otherJourneyId = findTheOtherJourneyId(pair, journey.getId())
					//TODO: Need to think around Notification
					sendNotificationForWorkflowStateChange(journey.getId(),otherJourneyId, WorkflowStatus.AUTO_REJECTED.getStatus())
				}
			}
		}
	}
	
	private makeAllRequestAvailableExceptOne(Journey myJourney, String exceptedPair) {
		for(String pairId : myJourney.getJourneyPairIds()) {
			if(!pairId.equals(exceptedPair)) {
				JourneyPair pair = journeyPairDataService.findPairById(pairId)
				String otherJourneyId = findTheOtherJourneyId(pair, myJourney.getId())
				Journey otherJourney = journeyDataService.findChildJourneys(otherJourneyId)
				if(!otherJourney.getHasAcceptedRequest() && pair.getInitiatorStatus().equals(WorkflowStatus.AUTO_REJECTED.getStatus())) {
					pair.setInitiatorStatus(WorkflowStatus.AVAILABLE.getStatus())
					pair.setRecieverStatus(WorkflowStatus.AVAILABLE.getStatus())
					journeyPairDataService.saveJourneyPair(pair)
				}
			}
		}
	}

	def String rejectRequest(String journeyPairId){
		JourneyPair pairToBeRejected = journeyPairDataService.findPairById(journeyPairId)
		Journey journeyToBeRejected = journeyDataService.findJourney(pairToBeRejected.getInitiatorJourneyId())
		Journey myJourney = journeyDataService.findJourney(pairToBeRejected.getRecieverJourneyId())
		pairToBeRejected.setInitiatorStatus(WorkflowStatus.REJECTED.getStatus())
		pairToBeRejected.setRecieverStatus(WorkflowStatus.REJECTED_BY_ME.getStatus())
		journeyPairDataService.saveJourneyPair(pairToBeRejected)
		journeyDataService.makeJourneySearchable(journeyToBeRejected)
		journeyDataService.makeJourneySearchable(myJourney)
		sendNotificationForWorkflowStateChange(myJourney.getId(), journeyToBeRejected.getId(), WorkflowStatus.REJECTED.getStatus())
		myJourney.getId()
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
			if(shouldBeIncludedForPairing(pair)) {
				otherJourneyId = findTheOtherJourneyId(pair, myJourneyId)
				otherJourney = journeyDataService.findJourney(otherJourneyId)
				if(pair.isPairAccepted()) {
					journeyDataService.makeJourneySearchable(otherJourney)
					makeAllRequestAvailableExceptOne(otherJourney, pair.getId())
					otherJourney.decrementNumberOfCopassengers()
					saveJourneys(otherJourney)
					journeyDataService.updateElasticsearchForPassangeCountIfRequired(otherJourneyId, otherJourney.numberOfCopassengers)
					
				}
				
				if(pair.getInitiatorJourneyId().equals(myJourneyId)){
					pair.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
					pair.setRecieverStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
				}
				else {
					pair.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
					pair.setRecieverStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
				}

				sendNotificationForWorkflowStateChange(myJourneyId, otherJourneyId, WorkflowStatus.CANCELLED.getStatus())
				journeyPairDataService.saveJourneyPair(pair)
				
				//journeyDataService.updateElasticsearchForPassangeCountIfRequired(otherJourney.id, otherJourney.numberOfCopassengers)
				//saveJourneys(myJourney);
				/*if(otherJourney.getNumberOfCopassengers()<1){
					journeyDataService.makeJourneySearchable(otherJourney)
				}*/

			}
		}
		saveJourneys(myJourney);
		recurrenceJourneyService.deleteRecurringJourney( myJourneyId)
	}
	
	
	def String cancelMyRequest(String journeyPairId, String myJourneyId, String currentUserName){
		String thirdJourneyId = null;
		Journey thirdJourney = null;
		JourneyPair pairTobeCancelled = journeyPairDataService.findPairById(journeyPairId)
		Journey jouurney1 = journeyDataService.findJourney(pairTobeCancelled.getInitiatorJourneyId());
		if(currentUserName.equals(jouurney1.getUser())) {
			myJourneyId = pairTobeCancelled.getInitiatorJourneyId()
		} 
		else {
			myJourneyId = pairTobeCancelled.getRecieverJourneyId()
		}
		String otherJourneyId = findTheOtherJourneyId(pairTobeCancelled, myJourneyId)
		Journey myJourney = journeyDataService.findJourney(myJourneyId);
		Journey otherJourney = journeyDataService.findJourney(otherJourneyId);
		if(pairTobeCancelled.isPairAccepted()) {
			journeyDataService.makeJourneySearchable(myJourney)
			journeyDataService.makeJourneySearchable(otherJourney)
			makeAllRequestAvailableExceptOne(myJourney, pairTobeCancelled.getId())
			makeAllRequestAvailableExceptOne(otherJourney, pairTobeCancelled.getId())
			myJourney.decrementNumberOfCopassengers()
			journeyDataService.updateElasticsearchForPassangeCountIfRequired(myJourneyId, myJourney.numberOfCopassengers)
			otherJourney.decrementNumberOfCopassengers()
			journeyDataService.updateElasticsearchForPassangeCountIfRequired(otherJourneyId, otherJourney.numberOfCopassengers)
			
		}
		
		if(pairTobeCancelled.getInitiatorJourneyId().equals(myJourneyId)){
			pairTobeCancelled.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
			pairTobeCancelled.setRecieverStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
			sendNotificationForWorkflowStateChange(myJourneyId, pairTobeCancelled.getRecieverJourneyId(), WorkflowStatus.CANCELLED_BY_REQUESTER.getStatus())

		}
		else {
			pairTobeCancelled.setInitiatorStatus(WorkflowStatus.CANCELLED_BY_OTHER.getStatus())
			pairTobeCancelled.setRecieverStatus(WorkflowStatus.CANCELLED_BY_ME.getStatus())
			sendNotificationForWorkflowStateChange(myJourneyId, pairTobeCancelled.getInitiatorJourneyId(), WorkflowStatus.CANCELLED.getStatus())
		}
		
		journeyPairDataService.saveJourneyPair(pairTobeCancelled)
		
		saveJourneys(myJourney, otherJourney);
		
		return myJourneyId
	}
	
	private void sendNotificationForWorkflowStateChange (String sourceId,String targetId, String state){
		def  messageMap =[(Constant.JOURNEY_WORKFLOW_SOURCE_ID):sourceId,(Constant.JOURNEY_WORKFLOW_TARGET_ID):targetId, (Constant.WORKFLOW_STATE_KEY):state]
		jmsService.send(queue: Constant.NOTIFICATION_WORKFLOW_STATE_CHANGE_QUEUE, messageMap)
	}
	
	private JourneyPair findOtherPairWithIndirectStatusToBeUpdated(List journeyPairs, String existingPairId){
		JourneyPair pairToBeUpdated = null
		for(JourneyPair pair : journeyPairs){
			if(!pair.getId().equals(existingPairId) && shouldBeIncludedForPairing(pair) && WorkflowStatus.isIndirectStatus(pair.getInitiatorStatus())){
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
		/*otherPair.setInitiatorStatus(WorkflowStatus.FORCED_CANCELLED.getStatus())
		otherPair.setRecieverStatus(WorkflowStatus.FORCED_CANCELLED.getStatus())
		journeyPairDataService.saveJourneyPair(otherPair)*/
		String thirdJourneyId = findTheOtherJourneyId(otherPair, journeyId)
		Journey thirdJourney = journeyDataService.findJourney(thirdJourneyId)
		thirdJourney.decrementNumberOfCopassengers();
		thirdJourney.removePairIdFromJourney(otherPair.getId())
		saveJourneys(thirdJourney);
		journeyPairDataService.deleteJourneyPair(otherPair)
		if(thirdJourney.getNumberOfCopassengers()<1){
			journeyDataService.makeJourneySearchable(thirdJourney)
		}
		journeyDataService.updateElasticsearchForPassangeCountIfRequired(thirdJourney.id, thirdJourney.numberOfCopassengers)
		sendNotificationForWorkflowStateChange(journeyId, thirdJourneyId, WorkflowStatus.FORCED_CANCELLED.getStatus())
	}
	
	private Journey saveDummyJounrney(String dummyJourneyId){
		Journey journey = journeyDataService.findJourneyFromElasticSearch(dummyJourneyId, true)
		journey.setIsDummy(true)
		journeyDataService.saveJourney(journey)
		return journey
	}
	
	def cancelAllAgedDummyJourneyRequest() {
		Date currentDate = GenericUtil.getCurrentDateInIST()
		List allJourneys = journeyDataService.findMyJourneys(Constant.DUMMY_USER_MOBILE, currentDate)
		for(Journey journey: allJourneys){
			if(!WorkflowStatus.CANCELLED.getStatus().equals(journey.getStatusAsParent())) {
				if(journeyHasAgedEnough(journey.getCreatedDate(), currentDate)) {
					this.cancelMyJourney(journey.getId())
				}
			}
		}
	}
	
	private boolean journeyHasAgedEnough(Date journeyDate, Date currentDate){
		int minutesToBeAdded = 120
		int millsInAMinute = 60*1000
		Date dateAfterAdingNMinutes = new Date (journeyDate.getTime() + minutesToBeAdded*millsInAMinute)
		return currentDate.after(dateAfterAdingNMinutes)
	}
	
	def boolean validateInvitationRequest(Journey currentJourney, String otherJourneyId, MobileResponse mobileResponse  ) {
		boolean isValidRequest = true
		Journey otherJourney = journeyDataService.findChildJourneys(otherJourneyId)
		if(otherJourney) {
			if(otherJourney.getUser().equals(currentJourney.getUser())){
				isValidRequest = false;
				mobileResponse.success = false;
				mobileResponse.message ="Sorry, you cannot invite yourself."
				return isValidRequest
			}
			if(otherJourney.getHasAcceptedRequest()) {
				isValidRequest = false;
				mobileResponse.success = false;
				mobileResponse.message ="Sorry, The passenger has already accepted ride with someone else."
				return isValidRequest
			}
		} 
		else {
			//See if its a dummy journey
			Journey dummyJourney = journeyDataService.findJourneyFromElasticSearch(otherJourneyId, true)
			if(!dummyJourney){
				isValidRequest = false;
				mobileResponse.success = false;
				mobileResponse.message= "Something is wrong with the journey that you are trying to connect."
				return isValidRequest
			}
			
		}
		return isValidRequest
	}
	
	private void makeAtleastOneJourneySearchable(String journeyId) {
		boolean journeyExsists = false
		Journey journey = journeyDataService.findJourney(journeyId)
		for (String pairId : journey?.getJourneyPairIds()) {
			JourneyPair pair = journeyPairDataService.findPairById(pairId)
			if(shouldBeIncludedForPairing(pair)) {
				Journey reciver = journeyDataService.findJourneyFromElasticSearch(pair.getRecieverJourneyId())
				Journey initiator = journeyDataService.findJourneyFromElasticSearch(pair.getInitiatorJourneyId())
				if(reciver && !initiator) {
					//Do nothing
					break
				}
				else if (reciver && initiator) {
					//remove initiator
					journeyDataService.makeJourneyNonSearchable(initiator.getId())
					break
				}
				else if (!reciver && !initiator) {
					reciver = journeyDataService.findJourney(pair.getRecieverJourneyId()) //Reload from Dynamo
					journeyDataService.makeJourneySearchable(reciver)
					break
				}
				else if (!reciver && initiator) {
					//Check if its not dummy. if not then swap
					initiator = journeyDataService.findJourney(initiator.getId())
					if(!initiator.getIsDummy()) {
						journeyDataService.makeJourneyNonSearchable(initiator.getId())
						reciver = journeyDataService.findJourney(pair.getRecieverJourneyId())
						journeyDataService.makeJourneySearchable(reciver)
					}
					break

				}


			}

		}
	}
	
	private validateIfJourneyIsNotAccepted(Journey journey) {
		if(journey.getHasAcceptedRequest()) {
			throw new RuntimeException("Somethig is not right. Journey already has one accepted request. Journey is: ${journey}")
		}
	}
}
