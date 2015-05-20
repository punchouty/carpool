package com.racloop.journey.workkflow


enum WorkflowDirection {
	INCOMING("Incoming"),
	OUTGOING("Outgoing"),
	FORCED_INCOMING("Forced Incoming"),
	FORCED_OUTGOING("Forced Outgoing")

	private String direction

	WorkflowDirection(String direction) {
		this.direction = direction
	}

	public String getDirection() {
		return direction
	}

	public static WorkflowDirection fromString(String direction) {
		if (direction != null) {
			for (WorkflowDirection b : WorkflowDirection.values()) {
				if (direction.equalsIgnoreCase(b.direction)) {
					return b;
				}
			}
		}
		return null;
	}
}
