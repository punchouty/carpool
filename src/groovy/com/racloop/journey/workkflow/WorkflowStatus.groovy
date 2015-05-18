package com.racloop.journey.workkflow


enum WorkflowStatus {
	INITIATED("New"),
	ACCEPTED("Accepted"),
	CANCELLED("Cancelled"),
	REJECTED("Rejected"),
	CANCELLED_BY_REQUESTER("Cancelled By Requester"),
	REQUESTED("Requested"),
	REQUEST_RECIEVED("Request Recieved"),
	NA("NA")

	WorkflowStatus(String status) {
		this.status = status
	}

	private final String status
	private static final Map<String,WorkflowStatus> lookup= new HashMap<String,WorkflowStatus>()

	static {
		for(WorkflowStatus s : EnumSet.allOf(WorkflowStatus.class))
			lookup.put(s.getStatus(), s);
	}

	public String getStatus() {
		return status
	}

	public static WorkflowStatus get(String state) {
		return lookup.get(state);
	}

	public static boolean canBeIgnored(String status){
		boolean canBeIgnored = false;
		WorkflowStatus workflowstate = this.get(status)
		switch(workflowstate) {
			case INITIATED: canBeIgnored = false
				break
			case ACCEPTED: canBeIgnored = false
				break
			case CANCELLED: canBeIgnored = false
				break
			case REJECTED: canBeIgnored = true
				break
			case CANCELLED_BY_REQUESTER: canBeIgnored = true
				break
			case REQUESTED: canBeIgnored = false
				break
			case REQUEST_RECIEVED: canBeIgnored = false
				break
			case NA: canBeIgnored = true
				break

			default : canBeIgnored = false
		}
		return canBeIgnored
	}

	public static List getAction (String state) {
		def action=[]
		WorkflowStatus workflowstate = this.get(state)
		switch(workflowstate) {
			case INITIATED: action=["Accept", "Reject"]
				break
			case ACCEPTED: action=["Cancel"]
				break
			case CANCELLED: action =[]
				break
			case REJECTED: action=[]
				break
			case CANCELLED_BY_REQUESTER: action=[]
				break
			default : action=[]
		}
		return action
	}
}
