package com.racloop.journey.workkflow


enum WorkflowAction {
	DELETE("Delete"),
	CANCEL("Cancel"),
	REJECT("Reject"),
	ACCEPT("Accept"),
	NONE("")

	private String action

	WorkflowAction(String action) {
		this.action = action
	}

	public String getAction() {
		return action
	}

	public static WorkflowAction fromString(String action) {
		if (action != null) {
			for (WorkflowAction b : WorkflowAction.values()) {
				if (action.equalsIgnoreCase(b.action)) {
					return b;
				}
			}
		}
		return null;
	}
}
