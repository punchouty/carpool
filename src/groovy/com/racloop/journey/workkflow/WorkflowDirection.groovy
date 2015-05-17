package com.racloop.journey.workkflow


enum WorkflowDirection {
	INCOMING("Incoming"),
	OUTGOING("Outgoing"),
	FORCED_INCOMING("Forced Incoming"),
	FORCED_OUTGOING("Forced Outgoing")
	
	WorkflowDirection(String direction) {
		this.direction = direction
	}

	private final String direction
	
	public String getDirection() {
		return direction
	}
	
	
}
