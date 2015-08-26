package com.racloop;

import com.racloop.domain.Journey;
import com.racloop.journey.workkflow.WorkflowStatus;

import java.util.Comparator;

public class HistoryComparator implements Comparator<Journey> {

	@Override
	public int compare(Journey o1, Journey o2) {
		return o2.getDateOfJourney().compareTo(o1.getDateOfJourney());
	}

}
