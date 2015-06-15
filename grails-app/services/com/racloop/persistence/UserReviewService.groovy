package com.racloop.persistence

import org.elasticsearch.common.joda.time.DateTime;

import com.racloop.Profile;
import com.racloop.User;
import com.racloop.domain.JourneyPair;
import com.racloop.journey.workkflow.WorkflowStatus;

import grails.transaction.Transactional

@Transactional
class UserReviewService {
	
	def journeyDataService
	def journeyPairDataService
	
	/**
	 * To test it use url- sampleData/runArchiveJob
	 * @return
	 */
    def markUsersForPendingReview(){
		DateTime currentDate = new DateTime();
		DateTime yesterday = currentDate.minusDays(1);
		def journeys = journeyDataService.findAllJourneysForADate(yesterday.toDate());
		HashSet<String> mobileProcessed = new HashSet<String>()
		journeys.each { journey ->
			String mobile = journey.mobile;
			if(mobileProcessed.contains(mobile)) {
				log.info "${mobile} of user : ${journey.name} is already processed"
			}
			else {
				Set<String> journeyPairIds = journey.getJourneyPairIds();
				boolean isMarkedForReview = false;
				journeyPairIds.each { pairId ->
					if(!isMarkedForReview) {
						JourneyPair journeyPair = journeyPairDataService.findPairById(pairId);
						if(journeyPair.getInitiatorStatusAsEnum() == WorkflowStatus.ACCEPTED) {
							isMarkedForReview = true;
						}
					}
				}
				if(isMarkedForReview) {
					Profile profile = Profile.findByMobile(mobile);
					User owner = profile.owner
					owner.journeyIdForReview = journey.id
					owner.save();
					mobileProcessed.add(mobile);
				}
			}
			journeyDataService.makeJourneyNonSearchable(journey.id);
		}
	}
	
	def findJourneysToBeReviewdForAUser(){
		//Find last journey for the user (use my history)
		//Find all related journeys
	}
	
	def clearUserForReview(){
		
	}
	
}
