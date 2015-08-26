package com.racloop;

import com.racloop.domain.Journey;
import com.racloop.journey.workkflow.WorkflowStatus;

import java.util.Comparator;

public class JourneyComparator implements Comparator<Journey> {

	@Override
	public int compare(Journey o1, Journey o2) {
		if(o1.getStatusAsParent().equals(o2.getStatusAsParent())) {
			return o1.getDateOfJourney().compareTo(o2.getDateOfJourney());
		}
		else {
			if(o1.getStatusAsParent().equals(WorkflowStatus.CANCELLED.getStatus())) return 1;
			if(o2.getStatusAsParent().equals(WorkflowStatus.CANCELLED.getStatus())) return -1;
		}
		return 0;
	}

}
