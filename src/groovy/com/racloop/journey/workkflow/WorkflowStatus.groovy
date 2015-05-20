package com.racloop.journey.workkflow


enum WorkflowStatus {
	
	REQUESTED("Requested"),
	REQUEST_RECIEVED("Request Recieved"),
	ACCEPTED("Accepted"),
	REJECTED("Rejected"),
	CANCELLED("Cancelled"),
	INHERITED("Inherited"),
	DELEGATED("Delegated")

	private final String status;
	private static final REQUESTED_ACTIONS = [WorkflowAction.CANCEL.getAction()];
	private static final REQUEST_RECIEVED_ACTIONS = [WorkflowAction.ACCEPT.getAction(), WorkflowAction.REJECT.getAction()];
	private static final ACCEPTED_ACTIONS = [WorkflowAction.CANCEL.getAction()];
	private static final REJECTED_ACTIONS = [];
	private static final CANCELLED_ACTIONS = [];
	private static final INHERITED_ACTIONS = [WorkflowAction.REJECT.getAction()];
	private static final DELEGATED_ACTIONS = [WorkflowAction.CANCEL.getAction()];
	private static final HashMap<WorkflowStatus, String []> statusToActionMapping = new HashMap<WorkflowStatus, String []>();
	
	static {
		statusToActionMapping.put(WorkflowStatus.REQUESTED, REQUESTED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.REQUEST_RECIEVED, REQUEST_RECIEVED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.ACCEPTED, ACCEPTED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.REJECTED, REJECTED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.CANCELLED, CANCELLED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.INHERITED, INHERITED_ACTIONS);
		statusToActionMapping.put(WorkflowStatus.DELEGATED, DELEGATED_ACTIONS);
	}

	WorkflowStatus(String status) {
		this.status = status
	}

	public String getStatus() {
		return status
	}
	
	public String[] getActions() {
		return statusToActionMapping.get(this);
	}

	public static WorkflowStatus fromString(String status) {
		if (status != null) {
			for (WorkflowStatus b : WorkflowStatus.values()) {
				if (status.equalsIgnoreCase(b.status)) {
					return b;
				}
			}
		}
		return null;
	}

	public static boolean canBeIgnored(String status){
		boolean canBeIgnored = false;
		WorkflowStatus workflowstate = this.get(status)
		switch(workflowstate) {
			case REQUESTED: canBeIgnored = false
				break
			case ACCEPTED: canBeIgnored = false
				break
			case CANCELLED: canBeIgnored = false
				break
			case REJECTED: canBeIgnored = true
				break
			case REQUESTED: canBeIgnored = false
				break
			case REQUEST_RECIEVED: canBeIgnored = false
				break
			case INHERITED: canBeIgnored = false
				break
			case DELEGATED: canBeIgnored = false
				break

			default : canBeIgnored = false
		}
		return canBeIgnored
	}
}
